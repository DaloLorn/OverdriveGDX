package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.util.OVDConstants;


public class TestShipModel extends AbstractShipModel {

	public TestShipModel() {
		super();

		// Info for later:
		// X + 350, Y + 170 --> ship's offset from the window's top left corner in FTL
		// Shield alpha --> 0.4 + 0.17 per level (1: 0.57, 2: 0.74, 3: 0.91, 4: 1.00)

		this.setShipOffset( 0, 70 );
		this.setHullOffset( -59, -103 );
		this.setHullSize( 652, 418 );
		this.setFloorOffset( 45, 93 );
		this.setCloakOffset( 19, 19 );
		this.setShieldEllipse( -30, 0, 350, 220 );

		this.setShieldImageSpec( new ImageSpec( OVDConstants.SHIP_ATLAS, "kestral-shields1" ) );
		this.setBaseImageSpec( new ImageSpec( OVDConstants.SHIP_ATLAS, "kestral-base" ) );
		this.setCloakImageSpec( new ImageSpec( OVDConstants.SHIP_ATLAS, "kestral-cloak" ) );
		this.setFloorImageSpec( new ImageSpec( OVDConstants.SHIP_ATLAS, "kestral-floor" ) );

		this.getProperties().setInt( OVDConstants.HULL_MAX, 30 );
		this.getProperties().setInt( OVDConstants.POWER_MAX, 30 );
		this.getProperties().setInt( OVDConstants.POWER, 12 );
		this.getProperties().setInt( OVDConstants.SHIELD_MAX, 4 );
		this.getProperties().setInt( OVDConstants.SHIELD, getProperties().getInt( OVDConstants.SHIELD_MAX ) );

	}
}
