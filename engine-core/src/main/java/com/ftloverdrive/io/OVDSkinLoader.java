package com.ftloverdrive.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;


/**
 * Loader for the customized OVDSkins.
 * Parses the skin file looking for ImageSpec entries, and lists them as dependencies.
 *
 */
public class OVDSkinLoader extends AsynchronousAssetLoader<OVDSkin, OVDSkinLoader.OVDSkinParameter> {

	// AssetLoader's resolver is private...
	protected FileHandleResolver resolver;
	protected AssetManager manager;


	public OVDSkinLoader( AssetManager manager, FileHandleResolver resolver ) {
		super( resolver );
		this.manager = manager;
		this.resolver = resolver;
	}

	@Override
	public Array<AssetDescriptor> getDependencies( String fileName, FileHandle file, OVDSkinParameter parameter ) {
		Array<AssetDescriptor> deps = new Array();
		if ( parameter == null ) {
			FileHandle atlasFile = new FileHandle( file.pathWithoutExtension() + ".atlas" );
			if ( atlasFile.exists() ) {
				deps.add( new AssetDescriptor( atlasFile, TextureAtlas.class ) );
			}
		}
		else if ( parameter.textureAtlasPath != null ) {
			deps.add( new AssetDescriptor( parameter.textureAtlasPath, TextureAtlas.class ) );
		}

		// Scan the skin file for dependencies
		Json json = new Json();
		ObjectMap<String, JsonValue> map = json.fromJson( ObjectMap.class, file );
		JsonValue jv = map.get( ImageSpec.class.getCanonicalName() );
		if ( jv != null ) {
			for ( JsonValue spec : jv ) {
				String path = spec.getString( "atlasPath" );
				FileHandle atlasFile = new FileHandle( path );

				// if the file doesn't exist, check the internal storage
				if(!atlasFile.exists()){
					atlasFile = Gdx.files.internal(path);
				}

				// if the file cannot be found, exit the application
				// TODO: proper handling of missing files
				if(!atlasFile.exists()){
					Gdx.app.exit();
				}

				AssetDescriptor descr = new AssetDescriptor( atlasFile, TextureAtlas.class );
				if ( !deps.contains( descr, false ) )
					deps.add( descr );
			}
		}

		return deps;
	}

	@Override
	public void loadAsync( AssetManager manager, String fileName, FileHandle file, OVDSkinParameter parameter ) {
	}

	@Override
	public OVDSkin loadSync( AssetManager manager, String fileName, FileHandle file, OVDSkinParameter parameter ) {
		String textureAtlasPath;
		ObjectMap<String, Object> resources;
		if ( parameter == null ) {
			textureAtlasPath = file.pathWithoutExtension() + ".atlas";
			resources = null;
		}
		else {
			textureAtlasPath = parameter.textureAtlasPath;
			resources = parameter.resources;
		}

		OVDSkin skin;
		if ( manager.isLoaded( textureAtlasPath, TextureAtlas.class ) ) {
			TextureAtlas atlas = manager.get( textureAtlasPath, TextureAtlas.class );
			skin = new OVDSkin( atlas, manager, resolver );
		}
		else
			skin = new OVDSkin( manager, resolver );

		if ( resources != null ) {
			for ( Entry<String, Object> entry : resources.entries() )
				skin.add( entry.key, entry.value );
		}
		skin.load( file );
		return skin;
	}


	public static class OVDSkinParameter extends AssetLoaderParameters<OVDSkin> {

		public final String textureAtlasPath;
		public final ObjectMap<String, Object> resources;


		public OVDSkinParameter() {
			this( null, null );
		}

		public OVDSkinParameter( String textureAtlasPath ) {
			this( textureAtlasPath, null );
		}

		public OVDSkinParameter( String textureAtlasPath, ObjectMap<String, Object> resources ) {
			this.textureAtlasPath = textureAtlasPath;
			this.resources = resources;
		}
	}
}
