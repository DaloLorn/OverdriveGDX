package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.PropertyOVDBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.SystemCreationEvent;
import com.ftloverdrive.util.OVDConstants;


public abstract class SystemBlueprint extends PropertyOVDBlueprint {

	public SystemBlueprint( SystemBlueprint prototype ) {
		super( prototype );
		properties.setInt( OVDConstants.HEALTH_MAX, 100 );
		properties.setInt( OVDConstants.HEALTH, 100 );
		properties.setInt( OVDConstants.LEVEL_MAX, 8 );
		properties.setInt( OVDConstants.LEVEL, 1 );
		properties.setInt( OVDConstants.POWER_INCREMENT, 1 );
	}

	@Override
	public int construct( OverdriveContext context ) {
		int systemRefId = context.getNetManager().requestNewRefId();

		SystemCreationEvent createE = Pools.get( SystemCreationEvent.class ).obtain();
		createE.init( systemRefId, getString( OVDConstants.BLUEPRINT_NAME ), properties );
		context.getScreenEventManager().postDelayedEvent( createE );

		return systemRefId;
	}
}
