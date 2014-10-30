package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.util.OVDConstants;

public class DefaultDoorModel implements DoorModel {

	protected NamedProperties doorProperties;
	protected ImageSpec doorAnimSpec;

	public DefaultDoorModel() {
		doorProperties = new NamedProperties();
		setAnimSpec( new ImageSpec( OVDConstants.EFFECTS_ATLAS, "door-sheet" ) );
	}

	@Override
	public NamedProperties getProperties() {
		return doorProperties;
	}

	@Override
	public void setAnimSpec(ImageSpec spec) {
		doorAnimSpec = spec;
	}

	@Override
	public ImageSpec getAnimSpec() {
		return doorAnimSpec;
	}
}
