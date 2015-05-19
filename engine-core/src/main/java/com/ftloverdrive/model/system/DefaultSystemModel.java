package com.ftloverdrive.model.system;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.io.ButtonSpec;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.util.OVDConstants;


public abstract class DefaultSystemModel implements SystemModel {

	protected NamedProperties systemProperties = new NamedProperties();


	public DefaultSystemModel() {
		super();
		systemProperties.setBool( OVDConstants.MANNED, false );
		systemProperties.setInt( OVDConstants.HEALTH_MAX, 100 );
		systemProperties.setInt( OVDConstants.HEALTH, 100 );
		systemProperties.setInt( OVDConstants.POWER_INCREMENT, 1 );
		systemProperties.setInt( OVDConstants.ION_FRACTION_MAX, 1000 );
		systemProperties.setInt( OVDConstants.ION_FRACTION, 1000 );
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
		return false;
	}

	@Override
	public String getIconName() {
		return null;
	}

	@Override
	public boolean isManned() {
		return systemProperties.getBool( OVDConstants.MANNED );
	}

	@Override
	public String getTooltipSystemDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTooltipPowerDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTooltipManningDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTooltipStatusDescription() {
		StringBuilder buf = new StringBuilder();

		int power = getCurrentPower();
		int powerMax = getPowerCapacity();
		int destroyed = systemProperties.getInt( OVDConstants.POWER_DESTROYED );

		// TODO: Replace with references to strings from misc.xml
		if ( power == 0 )
			buf.append( "- Unpowered" );
		else if ( power < powerMax )
			buf.append( "- Partially Powered" );
		else if ( power == powerMax )
			buf.append( "- Fully Powered" );

		if ( destroyed > 0 ) {
			if ( destroyed < powerMax ) {
				buf.append( buf.length() > 0 ? "\n" : "" );
				buf.append( "- Damaged" );
			}
			else if ( destroyed == powerMax ) {
				buf.append( buf.length() > 0 ? "\n" : "" );
				buf.append( "- Destroyed" );
			}
		}

		// TODO: Fire status check
		if ( true == false ) {
			buf.append( buf.length() > 0 ? "\n" : "" );
			buf.append( "- On Fire" );
		}

		return buf.toString();
	}

	@Override
	public String getAddPowerKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemovePowerKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ButtonSpec[] getButtons() {
		return null;
	}
}
