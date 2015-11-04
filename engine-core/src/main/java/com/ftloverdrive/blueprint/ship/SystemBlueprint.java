package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.PropertyOVDBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.ship.ShipSystemAddEvent;
import com.ftloverdrive.event.ship.SystemCreationEvent;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.util.OVDConstants;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


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

	/**
	 *  Method creates SystemPropertyEvent for every property of the system.
	 *  Mainly to be use in scripts.
	 */
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

	public int createTelepad(){
		// TODO: implement creation of telepads based on TestShipBlueprint code.
		/*
		// =============
		// Teleport pads

		int tpadRefId = -1;
		ShipCoordinate tpadCoords = null;

		int tpadsXYI[][] = new int[][] {
				//new int[] { 13, 3, 1 },
				//new int[] { 10, 2, 0 }
		};

		Array<Integer> tpadRefIds = new Array<Integer>();
		for ( int i = 0; i < tpadsXYI.length; i++ ) {
			int[] xyi = tpadsXYI[i];
			tpadRefId = context.getNetManager().requestNewRefId();
			tpadRefIds.add( tpadRefId );
			tpadCoords = ShipCoordinate.teleportPad( xyi[0], xyi[1] )[0];

			ShipTeleportPadCreationEvent tpadCreateE = Pools.get( ShipTeleportPadCreationEvent.class ).obtain();
			tpadCreateE.init( tpadRefId );
			context.getScreenEventManager().postDelayedEvent( tpadCreateE );

			ShipLayoutTeleportPadAddEvent tpadAddE = Pools.get( ShipLayoutTeleportPadAddEvent.class ).obtain();
			tpadAddE.init( shipRefId, tpadRefId, tpadCoords );
			context.getScreenEventManager().postDelayedEvent( tpadAddE );
		}

		for ( int i = 0; i < tpadsXYI.length; ++i ) {
			ShipLayoutConnectTeleportPadsEvent tpadConnectE = Pools.get( ShipLayoutConnectTeleportPadsEvent.class ).obtain();
			int index = tpadsXYI[i][2];
			if ( index != -1 )
				index = tpadRefIds.get( index );
			tpadConnectE.init( tpadRefIds.get( i ), index );
			context.getScreenEventManager().postDelayedEvent( tpadConnectE );
		}
		tpadRefIds.clear();*/
		throw new NotImplementedException();
	}

}
