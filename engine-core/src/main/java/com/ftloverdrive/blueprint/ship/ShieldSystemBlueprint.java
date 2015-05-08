package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.ShipSystemCreationEvent;


public class ShieldSystemBlueprint extends SystemBlueprint {


	public ShieldSystemBlueprint() {
		super( null );
		systemIcon = "s-shields-overlay";
	}


	@Override
	public int construct( OverdriveContext context ) {
		int systemRefId = context.getNetManager().requestNewRefId();

		ShipSystemCreationEvent createE = Pools.get( ShipSystemCreationEvent.class ).obtain();
		createE.init( systemRefId );
		context.getScreenEventManager().postDelayedEvent( createE );
		
		return systemRefId;
	}
}
