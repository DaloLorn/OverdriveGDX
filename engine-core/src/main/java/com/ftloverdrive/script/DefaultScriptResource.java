package com.ftloverdrive.script;

import bsh.NameSpace;

public class DefaultScriptResource implements ScriptResource {

	protected NameSpace ns;

	public DefaultScriptResource( NameSpace ns ) {
		this.ns = ns;
	}

	@Override
	public NameSpace getNamespace() {
		return ns;
	}
}
