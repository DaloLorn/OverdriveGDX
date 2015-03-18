package com.ftloverdrive.blueprint;


public abstract class AbstractOVDBlueprint implements OVDBlueprint {

	protected OVDBlueprint prototype;


	public AbstractOVDBlueprint( OVDBlueprint prototype ) {
		this.prototype = prototype;
	}

	@Override
	public OVDBlueprint getPrototype() {
		return prototype;
	}
}
