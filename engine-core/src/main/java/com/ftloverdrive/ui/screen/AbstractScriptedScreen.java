package com.ftloverdrive.ui.screen;

import java.util.HashMap;
import java.util.Map;

import bsh.EvalError;
import bsh.NameSpace;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.script.OVDScriptManager;

/**
 * An abstract class for Screens that want to expose various internals to scripts.
 */
public abstract class AbstractScriptedScreen implements OVDScreen, Disposable {

	/* All methods that this class will look for in scripts. */
	protected static final String M_RESOURCES	= "usingResources";
	protected static final String M_INIT		= "init";
	protected static final String M_RENDER		= "render";
	protected static final String M_RESIZE		= "resize";
	protected static final String M_SHOW		= "show";
	protected static final String M_HIDE		= "hide";
	protected static final String M_PAUSE		= "pause";
	protected static final String M_RESUME		= "resume";

	protected Logger log;

	protected OVDStageManager stageManager = null;
	protected OVDEventManager eventManager = null;
	protected OVDScriptManager scriptManager = null;

	protected OverdriveContext context;

	protected NameSpace screenNameSpace;
	protected Map<String, Class> resourceMap;


	protected AbstractScriptedScreen( OverdriveContext srcContext ) {
		this.context = Pools.get( OverdriveContext.class ).obtain();
		this.context.init( srcContext );
		this.context.setScreen( this );

		log = new Logger( LoadingScreen.class.getCanonicalName(), Logger.INFO );

		stageManager = new OVDStageManager();
		eventManager = new OVDEventManager();
		scriptManager = new OVDScriptManager();

		resourceMap = new HashMap<String, Class>();
	}

	/**
	 * Loads the specified script, and then executes its init() method.
	 */
	protected void runScript( String scriptPath ) {
		loadScript( scriptPath );
		invokeIfExists( M_INIT, context );
	}

	/**
	 * Evaluates the specified script and loads the resources it declared.
	 */
	@SuppressWarnings("unchecked")
	protected void loadScript( String scriptPath ) {
		if ( screenNameSpace == null )
			throw new IllegalStateException( "Screen namespace has not yet been initialized!" );

		try {
			FileHandleResolver resolver = context.getFileHandleResolver();
			scriptManager.eval( resolver.resolve( scriptPath ), screenNameSpace );

			Object result = invokeIfExists( M_RESOURCES );

			if ( result instanceof Map<?, ?> ) {
				Map<String,Class> temp = (Map<String, Class>)result;
				for ( Map.Entry<String,Class> entry : temp.entrySet() ) {
					if ( !resourceMap.containsKey( entry.getKey() ) )
						context.getAssetManager().load( entry.getKey(), entry.getValue() );
					resourceMap.put( entry.getKey(), entry.getValue() );
				}

				context.getAssetManager().finishLoading();
			} else if ( result != null ) {
				log.error( "usingResources() method does not return a Map: " + result.getClass() );
			}
		}
		catch ( Exception e ) {
			log.error( "Error evaluating script.", e );
		}
	}

	public void render( float delta ) {
		// TODO: Gets called a lot, probably not a good idea to expose this?
		invokeIfExists( M_RENDER, delta );
	}

	public void resize( int width, int height ) {
		invokeIfExists( M_RESIZE, width, height );
	}

	public void show() {
		invokeIfExists( M_SHOW );
	}

	public void hide() {
		invokeIfExists( M_HIDE );
	}

	public void pause() {
		invokeIfExists( M_PAUSE );
	}

	public void resume() {
		invokeIfExists( M_RESUME );
	}

	/**
	 * Unloads all resources loaded by the script, and clears the screens namespace, if it exists.
	 */
	public void dispose() {
		for ( String key : resourceMap.keySet() )
			context.getAssetManager().unload( key );
		if ( screenNameSpace != null )
			screenNameSpace.clear();
	}

	public OVDStageManager getStageManager() {
		return stageManager;
	}

	public OVDEventManager getEventManager() {
		return eventManager;
	}

	public OVDScriptManager getScriptManager() {
		return scriptManager;
	}

	/**
	 * @see {@link OVDScriptManager#invoke(NameSpace, String, Object...)}
	 */
	protected Object invokeIfExists( String name, Object... args ) {
		try {
			return scriptManager.invoke( screenNameSpace, name, args );
		} catch ( NoSuchMethodException e ) {
			// Do nothing
		} catch ( EvalError e ) {
			log.error( "Error evaluating scripted method " + name, e );
		}
		return null;
	}
}
