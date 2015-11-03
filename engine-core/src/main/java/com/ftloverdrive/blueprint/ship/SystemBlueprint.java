package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.PropertyOVDBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.ship.ShipSystemAddEvent;
import com.ftloverdrive.event.ship.SystemCreationEvent;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.util.OVDConstants;


public abstract class SystemBlueprint extends PropertyOVDBlueprint {
	private OverdriveContext context;
	private int systemRefId;

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
		this.context = context;
		int systemRefId = createSystem();

		createSystemPropertyEvent(OVDConstants.LEVEL_MAX, getProperties().getInt(OVDConstants.LEVEL_MAX));
		createSystemPropertyEvent(OVDConstants.LEVEL, getProperties().getInt(OVDConstants.LEVEL));

		return systemRefId;
	}

	protected int createSystem(){
		systemRefId = context.getNetManager().requestNewRefId();

		SystemCreationEvent creationEvent = Pools.get(SystemCreationEvent.class).obtain();
		creationEvent.init(systemRefId, getString(OVDConstants.BLUEPRINT_NAME), properties);
		context.getScreenEventManager().postDelayedEvent( creationEvent );

		return systemRefId;
	}

	public void addSystemToRoom(int shipRefId, int roomRefId){
		ShipSystemAddEvent sysAddE = Pools.get( ShipSystemAddEvent.class ).obtain();
		sysAddE.init( shipRefId, roomRefId, systemRefId );
		context.getScreenEventManager().postDelayedEvent( sysAddE );
	}

	public void updateProperties(){
		createSystemPropertyEvent(OVDConstants.POWER_INCREMENT, getProperties().getInt(OVDConstants.POWER_INCREMENT));
		createSystemPropertyEvent(OVDConstants.POWER_DESTROYED, getProperties().getInt(OVDConstants.POWER_DESTROYED));
		createSystemPropertyEvent(OVDConstants.MANNED, getProperties().getBool(OVDConstants.MANNED));
		createSystemPropertyEvent(OVDConstants.ION_FRACTION_MAX, getProperties().getInt(OVDConstants.ION_FRACTION_MAX));
		createSystemPropertyEvent(OVDConstants.ION_FRACTION, getProperties().getInt(OVDConstants.ION_FRACTION));
		createSystemPropertyEvent(OVDConstants.LEVEL_MAX, getProperties().getInt(OVDConstants.LEVEL_MAX));
		createSystemPropertyEvent(OVDConstants.LEVEL, getProperties().getInt(OVDConstants.LEVEL));
	}

	protected void createSystemPropertyEvent(String propertyKey, int value){
		SystemPropertyEvent sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( systemRefId, PropertyEvent.SET_ACTION, propertyKey, value );
		context.getScreenEventManager().postDelayedEvent( sysPropE );
	}

	protected void createSystemPropertyEvent(String propertyKey, float value){
		SystemPropertyEvent sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( systemRefId, PropertyEvent.SET_ACTION, propertyKey, value );
		context.getScreenEventManager().postDelayedEvent( sysPropE );
	}

	protected void createSystemPropertyEvent(String propertyKey, boolean value){
		SystemPropertyEvent sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( systemRefId, PropertyEvent.SET_ACTION, propertyKey, value );
		context.getScreenEventManager().postDelayedEvent( sysPropE );
	}

	protected void createSystemPropertyEvent(String propertyKey, String value){
		SystemPropertyEvent sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( systemRefId, PropertyEvent.SET_ACTION, propertyKey, value );
		context.getScreenEventManager().postDelayedEvent( sysPropE );
	}

}
