package com.ftloverdrive.packer;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import net.vhati.ftldat.FTLDat;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by mateusz on 2015-10-31.
 */
public class ResourcePacker {
    final private File resDir;
    final private File outDir;
    private File resourceDatFile;
    private File outResourceDir;
    private FTLDat.FTLPack resP;
    private Map<String,List<String>> dirInnerPathsMap;
    private JProgressBar progressBar;
    private List<Pattern> skipPtns;

    public ResourcePacker(File resDir, File outDir){
        this.resDir = resDir;
        this.outDir = outDir;
    }

    public boolean setup(JProgressBar progressBar) throws IOException {
        setSkippedPaths();
        outResourceDir = new File(outDir, "resources");
        if(outResourceDir.exists()) {
            deleteRecursively(outResourceDir);
        }
        outResourceDir.mkdirs();

        resourceDatFile = new File(resDir, "resource.dat");
        if(!resourceDatFile.exists()) {
            return false;
        }

        this.progressBar = progressBar;

        resP = new FTLDat.FTLPack(resourceDatFile, "r");

        return true;
    }

    private void setSkippedPaths() {
        skipPtns = new ArrayList<Pattern>();
        // Irrelevant dirs.
        skipPtns.add( Pattern.compile( "^(?!img/.*[.]png$).*" ) );

        // Needlessly big. It's an empty window with a tab (TODO: Crop the tab).
        skipPtns.add( Pattern.compile( "img/box_options_configure[.]png" ) );

        // Redundant. Pieces exist to reconstruct this.
        skipPtns.add( Pattern.compile( "img/scoreUI/score_main[.]png" ) );
        skipPtns.add( Pattern.compile( "img/screenshot[.]png" ) );  // Junk.

        // Stripping whitespace breaks TextureRegion.split().
        // http://code.google.com/p/libgdx/issues/detail?id=1192
        // Not that stripping whitespace helps much for FTL anyway.
    }

    public boolean sortInnerPaths(){
        List<String> allInnerPaths = new ArrayList<String>( resP.list() );
        Collections.sort(allInnerPaths);

        // Sort innerPaths into separate dir lists.
        dirInnerPathsMap = new LinkedHashMap<String,List<String>>();

        int i = 0;
        int max = allInnerPaths.size();

        for ( String innerPath : allInnerPaths ) {
            String dirPath = innerPath.replaceAll( "/[^/]*$", "/" );
            if ( !dirInnerPathsMap.containsKey( dirPath ) ) {
                dirInnerPathsMap.put( dirPath, new LinkedList<String>() );
            }
            dirInnerPathsMap.get( dirPath ).add( innerPath );
            updateProgressBar(++i, max);
        }

        return true;
    }

    public boolean copyFonts() throws IOException {
        InputStream is = null;
        OutputStream os = null;

        int i = 0;
        int max = dirInnerPathsMap.get("fonts/").size();

        // Copy fonts.
        for ( String innerPath : dirInnerPathsMap.get( "fonts/" ) ) {
            File dstFile = new File( outResourceDir, innerPath );
            dstFile.getParentFile().mkdirs();

            is = resP.getInputStream( innerPath );
            os = new BufferedOutputStream( new FileOutputStream( dstFile ) );

            byte[] buf = new byte[4096];
            int len;
            while ( (len = is.read(buf)) >= 0 ) {
                os.write( buf, 0, len );
            }

            is.close();
            os.close();
            updateProgressBar(++i, max);
        }
        return true;
    }

    public boolean copyTextures() throws IOException {
        TexturePacker.Settings normalSettings = new TexturePacker.Settings();
        TexturePacker.Settings bgSettings = new TexturePacker.Settings();
        bgSettings.paddingX = 0;
        bgSettings.paddingY = 0;

        int i = 0;
        int max = dirInnerPathsMap.entrySet().size();

        for ( Map.Entry<String,List<String>> entry : dirInnerPathsMap.entrySet() ) {
            String dirPath = entry.getKey();
            List<String> dirPaths = entry.getValue();

            if ( dirPaths.isEmpty() || isSkippedDir( dirPaths) ) continue;
            File nestedOutputDir = new File( outResourceDir, dirPath );

            TexturePacker.Settings dirSettings = new TexturePacker.Settings( normalSettings );
            if ( dirPath.equals( "img/map/" ) || dirPath.contains( "img/ipad" ) ) {
                dirSettings.paddingX = 0;  // Some images are exactly 1024, padding puts them over the limit.
                dirSettings.paddingY = 0;
            }
            else if ( dirPath.equals( "img/stars/" ) ) {
                dirSettings.paddingX = 0;  // Some images are exactly 512.
                dirSettings.paddingY = 0;
            }

            TexturePacker dirPacker = new TexturePacker( dirSettings );

            for ( String innerPath : dirPaths ) {
                if ( isSkippedInnerPath( innerPath ) ) continue;
                if ( !nestedOutputDir.exists() ) nestedOutputDir.mkdirs();

                InputStream imgStream = null;
                try {
                    String baseName = innerPath.replaceAll( ".*/", "" );
                    baseName = baseName.replaceAll( "[.]9([.][^.]+)$", "-9$1" );  // .9.ext is special in GDX.
                    baseName = baseName.replaceAll( "_", "-" );  // Underscores are special in GDX.
                    baseName = baseName.replaceAll( "[.][^.]+$", "" );  // Strip extension.

                    imgStream = resP.getInputStream( innerPath );
                    BufferedImage rawImage = ImageIO.read(imgStream);

                    int rawW = rawImage.getWidth();
                    int rawH = rawImage.getHeight();
                    if ( rawW > 1024 || rawH > 1024 ) {
                        // Pack large images in their own atlas of fragments.

                        TexturePacker bgPacker = new TexturePacker( bgSettings );
                        int regionCount = 0;
                        String regionName;
                        BufferedImage regionImage;
                        int regionW; int regionH;
                        int regionSize = 256;
                        for ( int regionY=0; regionY < rawH; regionY += regionSize ) {
                            for ( int regionX=0; regionX < rawW; regionX += regionSize ) {
                                regionName = baseName +"_"+ ++regionCount;  // GDX treats "_123" as a frame index.
                                regionW = Math.min( regionSize, rawW - regionX );
                                regionH = Math.min( regionSize, rawH - regionY );
                                regionImage = rawImage.getSubimage( regionX, regionY, regionW, regionH );
                                bgPacker.addImage( regionImage, regionName );
                            }
                        }

                        bgPacker.pack( nestedOutputDir, baseName +"-bigcols"+ ((rawW+regionSize-1)/regionSize) +".atlas" );
                        bgPacker = null;
                        System.gc();
                    }
                    else {
                        // Add small images to the directory's shared packer.
                        dirPacker.addImage( rawImage, baseName );
                    }
                }
                finally {
                    try {if ( imgStream != null ) imgStream.close();}
                    catch ( IOException e ) {}
                }
            }
            dirPacker.pack( nestedOutputDir, "pack.atlas" );
            dirPacker = null;
            System.gc();
            updateProgressBar(++i, max);
        }
        updateProgressBar(max, max);
        return true;
    }

    private void updateProgressBar(int i, int max) {
        final float value = (float) i / (float) max * 100.0f;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setValue((int) value);
            }
        });
    }

    private boolean deleteRecursively( File f ) {
        if( f.exists() && f.isDirectory() ) {
            File[] files = f.listFiles();
            if ( files != null ) {
                for ( int i=0; i < files.length; i++ ) {
                    deleteRecursively( files[i] );
                }
            }
        }
        return( f.delete() );
    }

    private boolean isSkippedDir( List<String> dirPaths ) {
        boolean allSkipped = true;
        for ( String innerPath : dirPaths ) {
            if ( !isSkippedInnerPath( innerPath ) ) {
                allSkipped = false;
                break;
            }
        }
        return allSkipped;
    }

    private boolean isSkippedInnerPath( String innerPath ) {
        for ( Pattern p : skipPtns ) {
            if ( p.matcher( innerPath ).matches() ) {
                return true;
            }
        }
        return false;
    }

}
