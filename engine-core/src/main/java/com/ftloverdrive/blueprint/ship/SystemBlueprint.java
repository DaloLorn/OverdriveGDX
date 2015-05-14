package com.ftloverdrive.blueprint.ship;

import com.ftloverdrive.blueprint.PropertyOVDBlueprint;
import com.ftloverdrive.util.OVDConstants;


public abstract class SystemBlueprint extends PropertyOVDBlueprint {

	public SystemBlueprint( SystemBlueprint prototype ) {
		super( prototype );
		properties.setInt( OVDConstants.HEALTH_MAX, 0 );
		properties.setInt( OVDConstants.HEALTH, 0 );
		properties.setInt( OVDConstants.LEVEL_MAX, 0 );
		properties.setInt( OVDConstants.LEVEL, 0 );
	}
}
