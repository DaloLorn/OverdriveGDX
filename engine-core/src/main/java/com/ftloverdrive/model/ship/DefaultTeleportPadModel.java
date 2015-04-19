package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.util.OVDConstants;


public class DefaultTeleportPadModel implements TeleportPadModel {

	protected ImageSpec padAnimSpec;
	protected int connectedTpadRefId = -1;


	public DefaultTeleportPadModel() {
		// TODO: Load the image specs from a skin file?
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

	@Override
	public void setConnectedTPadRefId( int tpadRefId ) {
		connectedTpadRefId = tpadRefId;
		if ( tpadRefId == -1 ) {
			setAnimSpec( new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "teleporter-off" ) );
		}
		else {
			setAnimSpec( new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "teleporter-on" ) );
		}
	}

	@Override
	public int getConnectedTPadRefId() {
		return connectedTpadRefId;
	}
}
