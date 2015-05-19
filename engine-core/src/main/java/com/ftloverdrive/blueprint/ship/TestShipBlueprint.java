package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.OVDBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.ship.ShipCreationEvent;
import com.ftloverdrive.event.ship.ShipCrewAddEvent;
import com.ftloverdrive.event.ship.ShipCrewCreationEvent;
import com.ftloverdrive.event.ship.ShipDoorCreationEvent;
import com.ftloverdrive.event.ship.ShipLayoutConnectTeleportPadsEvent;
import com.ftloverdrive.event.ship.ShipLayoutCrewPlacementEvent;
import com.ftloverdrive.event.ship.ShipLayoutDoorAddEvent;
import com.ftloverdrive.event.ship.ShipLayoutRoomAddEvent;
import com.ftloverdrive.event.ship.ShipLayoutSystemIconAddEvent;
import com.ftloverdrive.event.ship.ShipLayoutTeleportPadAddEvent;
import com.ftloverdrive.event.ship.ShipRoomCreationEvent;
import com.ftloverdrive.event.ship.ShipRoomImageChangeEvent;
import com.ftloverdrive.event.ship.ShipSystemAddEvent;
import com.ftloverdrive.event.ship.ShipTeleportPadCreationEvent;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.model.ship.ShipLayout;
import com.ftloverdrive.util.OVDConstants;


public class TestShipBlueprint extends ShipBlueprint {

	public TestShipBlueprint( ShipBlueprint prototype ) {
		super( prototype );

		properties.setString( OVDConstants.BLUEPRINT_NAME, getClass().getSimpleName() );
	}

	@Override
	public int construct( OverdriveContext context ) {
		int shipRefId = context.getNetManager().requestNewRefId();

		ShipCreationEvent shipCreateEvent = Pools.get( ShipCreationEvent.class ).obtain();
		shipCreateEvent.init( shipRefId, getString( OVDConstants.BLUEPRINT_NAME ), properties );
		context.getScreenEventManager().postDelayedEvent( shipCreateEvent );

		int roomRefId = -1;
		ShipCoordinate[] roomCoords = null;

		// The Y-offset is 1 higher than the original FTL's layout.txt.
		int[][] roomsXYWH = new int[][] {
				new int[] { 14, 3, 1, 2 },
				new int[] { 12, 3, 2, 2 },
				new int[] { 10, 3, 2, 1 },
				new int[] { 10, 4, 2, 1 },
				new int[] { 8, 2, 2, 2 },
				new int[] { 8, 4, 2, 2 },
				new int[] { 6, 1, 2, 1 },
				new int[] { 6, 2, 2, 2 },
				new int[] { 6, 4, 2, 2 },
				new int[] { 6, 6, 2, 1 },
				new int[] { 4, 3, 2, 2 },
				new int[] { 3, 2, 2, 1 },
				new int[] { 3, 5, 2, 1 },
				new int[] { 1, 2, 2, 1 },
				new int[] { 1, 3, 2, 2 },
				new int[] { 1, 5, 2, 1 },
				new int[] { 0, 3, 1, 2 }
		};
		ImageSpec[] roomsDecor = new ImageSpec[roomsXYWH.length];
		roomsDecor[0] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-pilot" );
		roomsDecor[2] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-doors" );
		roomsDecor[3] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-sensors" );
		roomsDecor[4] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-medbay" );
		roomsDecor[13] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-oxygen" );
		roomsDecor[5] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-shields" );
		roomsDecor[14] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-engines" );
		roomsDecor[10] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-weapons" );

		int[] roomRefIds = new int[roomsXYWH.length];
		for ( int i = 0; i < roomsXYWH.length; i++ ) {
			int[] xywh = roomsXYWH[i];
			roomRefId = context.getNetManager().requestNewRefId();
			roomRefIds[i] = roomRefId;
			roomCoords = ShipLayout.createRoomCoords( xywh[0], xywh[1], xywh[2], xywh[3] );

			ShipRoomCreationEvent roomCreateEvent = Pools.get( ShipRoomCreationEvent.class ).obtain();
			roomCreateEvent.init( roomRefId );
			context.getScreenEventManager().postDelayedEvent( roomCreateEvent );

			ImageSpec decorImageSpec = roomsDecor[i];
			if ( decorImageSpec != null ) {
				ShipRoomImageChangeEvent roomDecorEvent = Pools.get( ShipRoomImageChangeEvent.class ).obtain();
				roomDecorEvent.init( ShipRoomImageChangeEvent.DECOR, roomRefId, decorImageSpec );
				context.getScreenEventManager().postDelayedEvent( roomDecorEvent );
			}

			ShipLayoutRoomAddEvent roomAddEvent = Pools.get( ShipLayoutRoomAddEvent.class ).obtain();
			roomAddEvent.init( shipRefId, roomRefId, roomCoords );
			context.getScreenEventManager().postDelayedEvent( roomAddEvent );

			ShipLayoutSystemIconAddEvent iconAddEvent = Pools.get( ShipLayoutSystemIconAddEvent.class ).obtain();
			iconAddEvent.init( shipRefId, roomRefId, xywh[0] + ( xywh[2] - 1 ) * 0.5f,
					xywh[1] + ( xywh[3] - 1 ) * 0.5f + ( xywh[3] > 1 ? 1 : 0 ) );
			context.getScreenEventManager().postDelayedEvent( iconAddEvent );
		}

		// Non-rectangular room test
		roomRefId = context.getNetManager().requestNewRefId();
		String roomLayout = "" +
				"d=w" + "\n" +
				"  c";

		roomCoords = ShipLayout.createRoomCoords( 8, 1, roomLayout );

		ShipRoomCreationEvent roomCreateEvent = Pools.get( ShipRoomCreationEvent.class ).obtain();
		roomCreateEvent.init( roomRefId );
		context.getScreenEventManager().postDelayedEvent( roomCreateEvent );

		ShipLayoutRoomAddEvent roomAddEvent = Pools.get( ShipLayoutRoomAddEvent.class ).obtain();
		roomAddEvent.init( shipRefId, roomRefId, roomCoords );
		context.getScreenEventManager().postDelayedEvent( roomAddEvent );

		ShipLayoutSystemIconAddEvent iconAddEvent = Pools.get( ShipLayoutSystemIconAddEvent.class ).obtain();
		iconAddEvent.init( shipRefId, roomRefId, 10, 1 );
		context.getScreenEventManager().postDelayedEvent( iconAddEvent );

		// =======
		// Systems

		// TODO: Add system blueprints instead?
		OVDBlueprint blue = context.getBlueprintManager().getBlueprint( "ShieldSystemBlueprint" );
		int sysRefId = blue.construct( context );

		SystemPropertyEvent sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( sysRefId, PropertyEvent.SET_ACTION, OVDConstants.LEVEL, 8 ); // TODO: Normally would be loaded from ship's blueprint
		context.getScreenEventManager().postDelayedEvent( sysPropE );

		ShipSystemAddEvent sysAddE = Pools.get( ShipSystemAddEvent.class ).obtain();
		sysAddE.init( shipRefId, roomRefIds[5], sysRefId );
		context.getScreenEventManager().postDelayedEvent( sysAddE );

		blue = context.getBlueprintManager().getBlueprint( "EnginesSystemBlueprint" );
		sysRefId = blue.construct( context );

		sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( sysRefId, PropertyEvent.SET_ACTION, OVDConstants.LEVEL, 8 ); // TODO: Normally would be loaded from ship's blueprint
		context.getScreenEventManager().postDelayedEvent( sysPropE );

		sysAddE = Pools.get( ShipSystemAddEvent.class ).obtain();
		sysAddE.init( shipRefId, roomRefIds[14], sysRefId );
		context.getScreenEventManager().postDelayedEvent( sysAddE );

		blue = context.getBlueprintManager().getBlueprint( "MedbaySystemBlueprint" );
		sysRefId = blue.construct( context );

		sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( sysRefId, PropertyEvent.SET_ACTION, OVDConstants.LEVEL, 3 ); // TODO: Normally would be loaded from ship's blueprint
		context.getScreenEventManager().postDelayedEvent( sysPropE );

		sysAddE = Pools.get( ShipSystemAddEvent.class ).obtain();
		sysAddE.init( shipRefId, roomRefIds[4], sysRefId );
		context.getScreenEventManager().postDelayedEvent( sysAddE );

		blue = context.getBlueprintManager().getBlueprint( "OxygenSystemBlueprint" );
		sysRefId = blue.construct( context );

		sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( sysRefId, PropertyEvent.SET_ACTION, OVDConstants.LEVEL, 3 ); // TODO: Normally would be loaded from ship's blueprint
		context.getScreenEventManager().postDelayedEvent( sysPropE );

		sysAddE = Pools.get( ShipSystemAddEvent.class ).obtain();
		sysAddE.init( shipRefId, roomRefIds[13], sysRefId );
		context.getScreenEventManager().postDelayedEvent( sysAddE );

		blue = context.getBlueprintManager().getBlueprint( "HackingSystemBlueprint" );
		sysRefId = blue.construct( context );

		sysPropE = Pools.get( SystemPropertyEvent.class ).obtain();
		sysPropE.init( sysRefId, PropertyEvent.SET_ACTION, OVDConstants.LEVEL, 3 ); // TODO: Normally would be loaded from ship's blueprint
		context.getScreenEventManager().postDelayedEvent( sysPropE );

		sysAddE = Pools.get( ShipSystemAddEvent.class ).obtain();
		sysAddE.init( shipRefId, roomRefIds[11], sysRefId );
		context.getScreenEventManager().postDelayedEvent( sysAddE );

		// =====
		// Doors

		int doorRefId = -1;
		ShipCoordinate doorCoords = null;

		// The Y-offset is 1 higher than the original FTL's layout.txt.
		int doorsXYLRH[][] = new int[][] {
				// Overdrive doesn't use the left and right ids that FTL needed.
				// Connection is decided based on the door's position.
				// TODO: Remove them, or find a use for them.
				new int[] { 14, 4, 0, 1, 1 },
				new int[] { 12, 4, 1, 3, 1 },
				new int[] { 12, 3, 1, 2, 1 },
				new int[] { 10, 3, 2, 4, 1 },
				new int[] { 10, 4, 3, 5, 1 },
				new int[] { 8, 4, 4, 5, 0 },
				new int[] { 8, 2, 4, 7, 1 },
				new int[] { 8, 5, 5, 8, 1 },
				new int[] { 7, 6, 8, 9, 0 },
				new int[] { 7, 2, 6, 7, 0 },
				new int[] { 6, 3, 7, 10, 1 },
				new int[] { 6, 4, 8, 10, 1 },
				new int[] { 4, 5, 10, 12, 0 },
				new int[] { 4, 3, 10, 11, 0 },
				new int[] { 3, 2, 11, 13, 1 },
				new int[] { 2, 3, 13, 14, 0 },
				new int[] { 2, 5, 14, 15, 0 },
				new int[] { 3, 5, 12, 15, 1 },
				new int[] { 1, 4, 14, 16, 1 },
				new int[] { 1, 3, 14, 16, 1 },
				new int[] { 0, 3, 16, -1, 1 },
				new int[] { 0, 4, 16, -1, 1 },
				new int[] { 6, 7, 9, -1, 0 },
				new int[] { 7, 7, 9, -1, 0 },
				new int[] { 6, 1, 6, -1, 0 },
				new int[] { 7, 1, 6, -1, 0 }
		};

		for ( int i = 0; i < doorsXYLRH.length; i++ ) {
			int[] xylrh = doorsXYLRH[i];
			doorRefId = context.getNetManager().requestNewRefId();
			if ( xylrh[4] == 0 )
				doorCoords = ShipCoordinate.doorHorizontal( xylrh[0], xylrh[1] )[0];
			else
				doorCoords = ShipCoordinate.doorVertical( xylrh[0], xylrh[1] )[0];

			ShipDoorCreationEvent doorCreateEvent = Pools.get( ShipDoorCreationEvent.class ).obtain();
			doorCreateEvent.init( doorRefId );
			context.getScreenEventManager().postDelayedEvent( doorCreateEvent );

			ShipLayoutDoorAddEvent doorAddEvent = Pools.get( ShipLayoutDoorAddEvent.class ).obtain();
			doorAddEvent.init( shipRefId, doorRefId, doorCoords );
			context.getScreenEventManager().postDelayedEvent( doorAddEvent );
		}

		// =============
		// Teleport pads

		int tpadRefId = -1;
		ShipCoordinate tpadCoords = null;

		int tpadsXYI[][] = new int[][] {
				new int[] { 13, 3, 1 },
				new int[] { 10, 2, 0 }
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
		tpadRefIds.clear();

		// ====
		// Crew

		int crewRefId = context.getNetManager().requestNewRefId();
		ShipCrewCreationEvent crewCreateEvent = Pools.get( ShipCrewCreationEvent.class ).obtain();
		crewCreateEvent.init( crewRefId );
		context.getScreenEventManager().postDelayedEvent( crewCreateEvent );

		ShipCrewAddEvent crewAddEvent = Pools.get( ShipCrewAddEvent.class ).obtain();
		crewAddEvent.init( shipRefId, crewRefId );
		context.getScreenEventManager().postDelayedEvent( crewAddEvent );

		ShipLayoutCrewPlacementEvent crewPlaceEvent = Pools.get( ShipLayoutCrewPlacementEvent.class ).obtain();
		ShipCoordinate coord = Pools.get( ShipCoordinate.class ).obtain();
		coord.init( 14, 3, ShipCoordinate.TYPE_SQUARE );
		crewPlaceEvent.init( shipRefId, crewRefId, coord );
		context.getScreenEventManager().postDelayedEvent( crewPlaceEvent );

		return shipRefId;
	}
}
