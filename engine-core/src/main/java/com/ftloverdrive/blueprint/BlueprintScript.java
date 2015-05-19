package com.ftloverdrive.blueprint;

import bsh.EvalError;


public interface BlueprintScript {

	public PropertyOVDBlueprint create() throws EvalError;
}
