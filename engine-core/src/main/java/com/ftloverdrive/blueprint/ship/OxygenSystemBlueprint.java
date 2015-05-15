package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.SystemCreationEvent;
import com.ftloverdrive.util.OVDConstants;


public class OxygenSystemBlueprint extends SystemBlueprint {


	public OxygenSystemBlueprint() {
		super( null );
		properties.setString( OVDConstants.BLUEPRINT_NAME, getClass().getSimpleName() );

		properties.setString( OVDConstants.ICON_NAME, "s-oxygen" );
		properties.setBool( OVDConstants.SELF_POWERED, false );
		properties.setInt( OVDConstants.LEVEL_MAX, 3 );
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
