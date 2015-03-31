package com.ftloverdrive.io;

import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.ftloverdrive.ui.DistanceFieldShader;
import com.ftloverdrive.ui.TiledNinePatchDrawable;


/**
 * A customized Skin that can load fonts from any path that the game itself can load.
 * Also capable of loading truetype fonts, and TiledNinePatchDrawables. Doesn't require
 * an .atlas file with the same name as the skin.
 * 
 * Stores a reference to the AssetManager as a field. Not very elegant, or even
 * architecturally sound, but allows the skin to get a hold of a TextureAtlas, if it is
 * referenced in the skin, instead of needlessly loading it twice.
 * 
 * Always use this class instead of the default libGDX Skins.
 */
public class OVDSkin extends Skin {

	protected FileHandleResolver resolver = null;
	protected AssetManager manager;
	protected Pattern argsPtn = Pattern.compile( "&?([A-Za-z0-9]+)=([A-Za-z0-9]+)(#?)" );


	public OVDSkin( AssetManager manager, FileHandleResolver resolver ) {
		super();
		this.manager = manager;
		this.resolver = resolver;
	}

	public OVDSkin( TextureAtlas atlas, AssetManager manager, FileHandleResolver resolver ) {
		super( atlas );
		this.manager = manager;
		this.resolver = resolver;
	}

	protected FileHandle resolve( FileHandle skinFile, String path ) {
		FileHandle fontFile = skinFile.parent().child( path );
		if ( !fontFile.exists() ) fontFile = Gdx.files.internal( path );
		if ( !fontFile.exists() ) fontFile = resolver.resolve( scrub( path ) );
		if ( !fontFile.exists() ) throw new SerializationException( "Font file not found: " + fontFile );
		return fontFile;
	}

	protected String scrub( String path ) {
		int qMark = path.lastIndexOf( "?" );
		if ( qMark != -1 ) {
			path = path.substring( 0, qMark );
		}
		return path;
	}

	@Override
	protected Json getJsonLoader( final FileHandle skinFile ) {
		final Skin skin = this;
		Json json = super.getJsonLoader( skinFile );

		json.setSerializer( BitmapFont.class, new ReadOnlySerializer<BitmapFont>() {

			public BitmapFont read( Json json, JsonValue jsonData, Class type ) {
				String path = json.readValue( "file", String.class, jsonData );
				int scaledSize = json.readValue( "scaledSize", int.class, -1, jsonData );
				Boolean flip = json.readValue( "flip", Boolean.class, false, jsonData );

				FileHandle fontFile = resolve( skinFile, path );

				// if ( manager.isLoaded( path, BitmapFont.class ) )
				// return manager.get( path, BitmapFont.class );

				String scrubbedPath = scrub( path );
				if ( scrubbedPath.endsWith( ".ttf" ) ) {
					FreeTypeFontLoader fontLoader = new FreeTypeFontLoader( resolver );
					BitmapFont font = fontLoader.loadSync( manager, path, fontFile, null );
					skin.add( path, font );
					return font;
				}

				// Use a region with the same name as the font, else use a PNG file in the same directory as the FNT file.
				String regionName = fontFile.nameWithoutExtension();
				try {
					BitmapFont font;
					TextureRegion region = skin.optional( regionName, TextureRegion.class );
					if ( region != null )
						font = new BitmapFont( fontFile, region, flip );
					else {
						FileHandle imageFile = fontFile.parent().child( regionName + ".png" );
						if ( imageFile.exists() )
							font = new BitmapFont( fontFile, imageFile, flip );
						else
							font = new BitmapFont( fontFile, flip );
					}
					// Scaled size is the desired cap height to scale the font to.
					if ( scaledSize != -1 ) font.setScale( scaledSize / font.getCapHeight() );
					skin.add( path, font );
					return font;
				}
				catch ( RuntimeException ex ) {
					throw new SerializationException( "Error loading bitmap font: " + fontFile, ex );
				}
			}
		} );

		json.setSerializer( TiledNinePatchDrawable.class, new ReadOnlySerializer<TiledNinePatchDrawable>() {

			public TiledNinePatchDrawable read( Json json, JsonValue jsonData, Class type ) {
				String specName = json.readValue( "texture", String.class, jsonData );
				ImageSpec spec = skin.get( specName, ImageSpec.class );
				TextureAtlas atlas = manager.get( spec.atlasPath, TextureAtlas.class );
				TextureRegion region = atlas.findRegion( spec.regionName );

				return new TiledNinePatchDrawable( region,
						json.readValue( "left", int.class, jsonData ),
						json.readValue( "right", int.class, jsonData ),
						json.readValue( "top", int.class, jsonData ),
						json.readValue( "bot", int.class, jsonData ) );
			}
		} );

		json.setSerializer( FileHandle.class, new ReadOnlySerializer<FileHandle>() {

			public FileHandle read( Json json, JsonValue jsonData, Class type ) {
				FileHandle fh = resolve( skinFile, json.readValue( "path", String.class, jsonData ) );
				return fh;
			}
		} );

		json.setSerializer( DistanceFieldShader.class, new ReadOnlySerializer<DistanceFieldShader>() {

			public DistanceFieldShader read( Json json, JsonValue jsonData, Class type ) {
				float smoothing = json.readValue( "smoothing", Float.class, jsonData );
				String key = DistanceFieldShader.class.getName() + "?" + smoothing;
				DistanceFieldShader shader = null;
				if ( skin.has( key, DistanceFieldShader.class ) ) {
					shader = skin.get( key, DistanceFieldShader.class );
				}
				else {
					shader = new DistanceFieldShader( smoothing );
					skin.add( key, shader, DistanceFieldShader.class );
				}
				return shader;
			}
		} );

		return json;
	}

	public Drawable getDrawable( String name ) {
		Drawable drawable = optional( name, TiledNinePatchDrawable.class );
		if ( drawable != null ) return drawable;

		return super.getDrawable( name );
	}

	public void dispose() {
		super.dispose();
		// TODO: Need to perform any additional disposing?
	}
}
