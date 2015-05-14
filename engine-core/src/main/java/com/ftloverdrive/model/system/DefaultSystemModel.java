package com.ftloverdrive.model.system;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.util.OVDConstants;


public class DefaultSystemModel implements SystemModel {

	protected NamedProperties systemProperties = new NamedProperties();


	public DefaultSystemModel() {
		super();
		systemProperties.setInt( OVDConstants.HEALTH_MAX, 0 );
		systemProperties.setInt( OVDConstants.HEALTH, 0 );
		systemProperties.setInt( OVDConstants.POWER_INCREMENT, 0 );
		systemProperties.setBool( OVDConstants.SELF_POWERED, false );
		systemProperties.setString( OVDConstants.ICON_NAME, "" );
		systemProperties.setInt( OVDConstants.POWER_IONED, 0 );
		systemProperties.setInt( OVDConstants.POWER_MAX, 0 );
		systemProperties.setInt( OVDConstants.POWER, 0 );
		systemProperties.setInt( OVDConstants.LEVEL_MAX, 0 );
		systemProperties.setInt( OVDConstants.LEVEL, 0 );
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

	@Override
	public int getPowerIncrement() {
		return systemProperties.getInt( OVDConstants.POWER_INCREMENT );
	}

	@Override
	public boolean isSelfPowered() {
		return systemProperties.getBool( OVDConstants.SELF_POWERED );
	}

	@Override
	public String getIconName() {
		return systemProperties.getString( OVDConstants.ICON_NAME );
	}
}
