package com.ftloverdrive.blueprint;

import com.ftloverdrive.core.OverdriveContext;

public abstract class ShipBlueprint extends DefaultOVDBlueprint {

	public ShipBlueprint( ShipBlueprint prototype ) {
		super( prototype );
	}

	public abstract int createShip( OverdriveContext context );
}
