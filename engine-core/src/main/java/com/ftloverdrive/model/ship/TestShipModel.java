package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.util.OVDConstants;


public class TestShipModel extends AbstractShipModel {

	public TestShipModel() {
		super();

		// Info for later:
		// X + 350, Y + 170 --> ship's offset from the window's top left corner in FTL
		// Shield alpha --> 0.4 + 0.17 per level (1: 0.57, 2: 0.74, 3: 0.91, 4: 1.00) 

		// TODO: The shield isn't moved enough?
		// Shield ellipse Y+4: Hull height is 4px taller than shield image.
		//   Slides shield up to match their top-left corners instead of
		//   bottom-left.
		// Shield ellipse X-17: ???

		// TODO: solve magic numbers problem, eg. X + 17, Y - 6 for shield -- data & calculations
		// are 100% correct, has to be something else

		this.setShipOffset( 0, -70 );
		this.setHullOffset( -59, 103 );
		this.setHullSize( 652, 418 );
		this.setFloorOffset( 45, 93 );
		this.setCloakOffset( 19, 19 );
		this.setShieldEllipse( -30, 0, 350, 220 );
		
		this.setShieldImageSpec( new ImageSpec( OVDConstants.SHIP_ATLAS, "kestral-shields1" ) );
		this.setBaseImageSpec( new ImageSpec( OVDConstants.SHIP_ATLAS, "kestral-base" ) );
		this.setCloakImageSpec( new ImageSpec( OVDConstants.SHIP_ATLAS, "kestral-cloak" ) );
		this.setFloorImageSpec( new ImageSpec( OVDConstants.SHIP_ATLAS, "kestral-floor" ) );

		this.getProperties().setInt( OVDConstants.HULL_MAX, 30 );
	}
}
