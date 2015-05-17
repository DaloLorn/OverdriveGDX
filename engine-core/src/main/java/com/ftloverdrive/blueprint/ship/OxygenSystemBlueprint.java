package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.SystemCreationEvent;
import com.ftloverdrive.util.OVDConstants;


public class OxygenSystemBlueprint extends SystemBlueprint {


	public OxygenSystemBlueprint() {
		super( null );
		properties.setString( OVDConstants.BLUEPRINT_NAME, getClass().getSimpleName() );

		properties.setString( OVDConstants.SYSTEM_ICON_NAME, "s-oxygen" );
		properties.setInt( OVDConstants.LEVEL_MAX, 3 );
		properties.setInt( OVDConstants.POWER_IONED, 2 );
		properties.setInt( OVDConstants.POWER, 1 );
		properties.setInt( OVDConstants.ION_FRACTION, 1000 );
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
