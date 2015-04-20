package com.ftloverdrive.model.system;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.util.OVDConstants;


public abstract class AbstractSystemModel implements SystemModel {

	protected NamedProperties systemProperties = new NamedProperties();


	public AbstractSystemModel() {
		super();
		// TODO: Common properties for health, etc?
		systemProperties.setInt( OVDConstants.HULL_MAX, 0 );
		systemProperties.setInt( OVDConstants.HULL, 0 );
		systemProperties.setInt( OVDConstants.POWER_MAX, 0 );
		systemProperties.setInt( OVDConstants.POWER, 0 );
	}

	@Override
	public NamedProperties getProperties() {
		return systemProperties;
	}

	@Override
	public void setCurrentPower( OverdriveContext context, int n ) {
		int selfRefId = context.getReferenceManager().getId( this );

		SystemPropertyEvent event = Pools.get( SystemPropertyEvent.class ).obtain();
		event.init( selfRefId, PropertyEvent.SET_ACTION, OVDConstants.POWER, n );
		context.getScreenEventManager().postDelayedEvent( event );
	}

	@Override
	public int getCurrentPower() {
		return systemProperties.getInt( OVDConstants.POWER );
	}

	@Override
	public int getPowerCapacity() {
		return systemProperties.getInt( OVDConstants.POWER_MAX );
	}

	@Override
	public void damage( OverdriveContext context, int value ) {
		// TODO Auto-generated method stub

	}
}
