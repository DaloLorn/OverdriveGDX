package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.SystemCreationEvent;
import com.ftloverdrive.util.OVDConstants;


public class EngineSystemBlueprint extends SystemBlueprint {


	public EngineSystemBlueprint() {
		super( null );
		systemIcon = "s-engines";
		propertyMap.put( OVDConstants.BLUEPRINT_NAME, getClass().getSimpleName() );
	}


	@Override
	public int construct( OverdriveContext context ) {
		int systemRefId = context.getNetManager().requestNewRefId();

		SystemCreationEvent createE = Pools.get( SystemCreationEvent.class ).obtain();
		createE.init( systemRefId, getProperty( OVDConstants.BLUEPRINT_NAME, String.class ) );
		context.getScreenEventManager().postDelayedEvent( createE );

		return systemRefId;
	}
}
