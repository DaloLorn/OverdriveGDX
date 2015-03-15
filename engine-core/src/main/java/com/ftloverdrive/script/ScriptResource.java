package com.ftloverdrive.script;

import bsh.NameSpace;

/**
 * This interface represents a script file as a loadable resource.
 * 
 * TODO: Stub. Needs anything other than the namespace? Already associated
 * with an identifier via the AssetManager...
 */
public interface ScriptResource {

	public NameSpace getNamespace();
}
