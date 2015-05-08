package com.ftloverdrive.blueprint.ship;

import com.ftloverdrive.blueprint.PropertyOVDBlueprint;


public abstract class SystemBlueprint extends PropertyOVDBlueprint {

	protected String systemIcon;


	public SystemBlueprint( SystemBlueprint prototype ) {
		super( prototype );
	}

	public String getSystemIcon() {
		return systemIcon;
	}
}
