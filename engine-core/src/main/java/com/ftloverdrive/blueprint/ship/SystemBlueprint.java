package com.ftloverdrive.blueprint.ship;

import com.ftloverdrive.blueprint.PropertyOVDBlueprint;
import com.ftloverdrive.util.OVDConstants;


public abstract class SystemBlueprint extends PropertyOVDBlueprint {

	public SystemBlueprint( SystemBlueprint prototype ) {
		super( prototype );
		properties.setBool( OVDConstants.SELF_POWERED, false );
		properties.setInt( OVDConstants.HEALTH_MAX, 100 );
		properties.setInt( OVDConstants.HEALTH, 100 );
		properties.setInt( OVDConstants.LEVEL_MAX, 8 );
		properties.setInt( OVDConstants.LEVEL, 1 );
		properties.setInt( OVDConstants.POWER_INCREMENT, 1 );
	}
}
