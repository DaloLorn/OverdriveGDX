package com.ftloverdrive.script;

import java.util.Map;

import bsh.EvalError;

import com.ftloverdrive.event.OVDEventManager;


/**
 * This interface is used after script files are evaluated to provide access
 * to commonly used methods they define.
 * 
 * Use in conjunction with {@link OVDScriptManager#getInterface(ScriptResource, Class)}.
 * For example:
 * 
 *  	ScriptResource script = context.getAssetManager().get( "path/to/somescript.java", ScriptResource.class );
 *  	OVDScript proxy = context.getScreenScriptManager().getInterface( script, OVDScript.class );
 */
public interface OVDScript {

	/** Returns a unique identifier for this script. TODO: Not needed? */
	public String getScriptId();

	/**
	 * Use this method to register listeners to allow the script to react to in-game events.
	 * Called on all scripts when a screen is created.
	 * 
	 * @param eventManager the current screen's event manager
	 * @param screenKey the string identifier of the current screen
	 */
	public void registerListeners( OVDEventManager eventManager, String screenKey ) throws EvalError;

	/**
	 * Declares resources the script will be using. Overdrive handles un/loading internally.
	 * Called on all scripts when a screen is created.
	 */
	public Map<String, Class> usingResources() throws EvalError;
}
