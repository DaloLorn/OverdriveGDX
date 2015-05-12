package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.SystemCreationEvent;


public class ShieldSystemBlueprint extends SystemBlueprint {


	public ShieldSystemBlueprint() {
		super( null );
		systemIcon = "s-shields";
	}


	@Override
	public int construct( OverdriveContext context ) {
		int systemRefId = context.getNetManager().requestNewRefId();

		SystemCreationEvent createE = Pools.get( SystemCreationEvent.class ).obtain();
		createE.init( systemRefId );
		context.getScreenEventManager().postDelayedEvent( createE );
		
		return systemRefId;
	}
}
