package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.util.OVDConstants;


public class DefaultTeleportPadModel implements TeleportPadModel {

	protected ImageSpec padAnimSpec;


	public DefaultTeleportPadModel() {
		setAnimSpec( new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "teleporter-on" ) );
	}

	@Override
	public void setAnimSpec( ImageSpec spec ) {
		padAnimSpec = spec;
	}

	@Override
	public ImageSpec getAnimSpec() {
		return padAnimSpec;
	}
}
