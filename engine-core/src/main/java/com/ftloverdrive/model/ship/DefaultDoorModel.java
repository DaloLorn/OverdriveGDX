package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.AnimSpec;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.util.OVDConstants;

public class DefaultDoorModel implements DoorModel {

	protected NamedProperties doorProperties;
	protected AnimSpec doorAnimSpec;

	public DefaultDoorModel() {
		doorProperties = new NamedProperties();
		doorProperties.setBool( OVDConstants.DOOR_OPEN, false );
		doorProperties.setInt( OVDConstants.DOOR_LEVEL, 0 );
		doorProperties.setInt( OVDConstants.DOOR_HEALTH, 50 );
		doorProperties.setInt( OVDConstants.DOOR_HEALTH_MAX, 50 );
		setAnimSpec( new AnimSpec( OVDConstants.EFFECTS_ATLAS, "door-sheet", 35, 35, 5, 0, 0, 0.25f ) );
	}

	@Override
	public NamedProperties getProperties() {
		return doorProperties;
	}

	@Override
	public void setAnimSpec( AnimSpec spec ) {
		doorAnimSpec = spec;
	}

	@Override
	public AnimSpec getAnimSpec() {
		return doorAnimSpec;
	}
}
