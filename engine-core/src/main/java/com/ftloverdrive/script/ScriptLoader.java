package com.ftloverdrive.script;

import java.io.IOException;

import bsh.EvalError;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ScriptLoader extends AsynchronousAssetLoader<ScriptResource, ScriptLoader.ScriptParameter> {

	private ScriptResource script = null;
	private OVDScriptManager loadScriptManager; 
	
	public ScriptLoader( FileHandleResolver resolver ) {
		super( resolver );
		loadScriptManager = new OVDScriptManager();
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, ScriptParameter parameter) {
		try {
			script = loadScriptManager.eval( file );
		} catch ( EvalError e ) {
			throw new GdxRuntimeException( String.format( "Error while loading script %s:", fileName ), e );
		} catch ( IOException e ) {
			throw new GdxRuntimeException( String.format( "Error while loading script %s:", fileName ), e );
		}
	}

	@Override
	public ScriptResource loadSync( AssetManager assetManager, String fileName, FileHandle file, ScriptParameter parameter ) {
		ScriptResource script = this.script;
		this.script = null;
		return script;
	}

	@Override
	public Array<AssetDescriptor> getDependencies( String fileName, FileHandle file, ScriptParameter parameter ) {
		// TODO: deduce dependencies from imports?
		return null;
	}

	public static class ScriptParameter extends AssetLoaderParameters<ScriptResource> {
	}
}
