package com.ftloverdrive.blueprint.ship;

import com.ftloverdrive.blueprint.PropertyOVDBlueprint;
import com.ftloverdrive.util.OVDConstants;


public abstract class ShipBlueprint extends PropertyOVDBlueprint {

	public ShipBlueprint( ShipBlueprint prototype ) {
		super( prototype );
	}

	public String getBlueprintName() {
		return properties.getString( OVDConstants.BLUEPRINT_NAME );
	}
}
