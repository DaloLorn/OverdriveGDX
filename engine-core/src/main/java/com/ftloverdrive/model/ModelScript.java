package com.ftloverdrive.model;

import bsh.EvalError;


public interface ModelScript {

	public String getAssociatedBlueprint() throws EvalError;

	public Class<? extends OVDModel> getObjectClass() throws EvalError;
}
