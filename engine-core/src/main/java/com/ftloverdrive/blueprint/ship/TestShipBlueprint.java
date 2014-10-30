package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.ShipCreationEvent;
import com.ftloverdrive.event.ship.ShipDoorCreationEvent;
import com.ftloverdrive.event.ship.ShipLayoutDoorAddEvent;
import com.ftloverdrive.event.ship.ShipLayoutRoomAddEvent;
import com.ftloverdrive.event.ship.ShipRoomCreationEvent;
import com.ftloverdrive.event.ship.ShipRoomImageChangeEvent;
import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.model.ship.ShipLayout;
import com.ftloverdrive.util.OVDConstants;


public class TestShipBlueprint implements ShipBlueprint {

	@Override
	public int createShip( OverdriveContext context ) {
		int shipRefId = context.getNetManager().requestNewRefId();

		ShipCreationEvent shipCreateEvent = Pools.get( ShipCreationEvent.class ).obtain();
		shipCreateEvent.init( shipRefId, "Test" );
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
		ImageSpec[] roomsDecor = new ImageSpec[ roomsXYWH.length ];
		roomsDecor[0] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-pilot" );
		roomsDecor[2] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-doors" );
		roomsDecor[3] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-sensors" );
		roomsDecor[4] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-medbay" );
		roomsDecor[13] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-oxygen" );
		roomsDecor[5] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-shields" );
		roomsDecor[14] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-engines" );
		roomsDecor[10] = new ImageSpec( OVDConstants.SHIP_INTERIOR_ATLAS, "room-weapons" );

		for ( int i=0; i < roomsXYWH.length; i++ ) {
			int[] xywh = roomsXYWH[i];
			roomRefId = context.getNetManager().requestNewRefId();
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
		}

		// Non-rectangular room test
		roomRefId = context.getNetManager().requestNewRefId();
		String roomLayout =
				"d=w" + "\n" +
				"  c";
		roomCoords = ShipLayout.createRoomCoords(8, 1, roomLayout);

		ShipRoomCreationEvent roomCreateEvent = Pools.get( ShipRoomCreationEvent.class ).obtain();
		roomCreateEvent.init( roomRefId );
		context.getScreenEventManager().postDelayedEvent( roomCreateEvent );

		ShipLayoutRoomAddEvent roomAddEvent = Pools.get( ShipLayoutRoomAddEvent.class ).obtain();
		roomAddEvent.init( shipRefId, roomRefId, roomCoords );
		context.getScreenEventManager().postDelayedEvent( roomAddEvent );

		// =====
		// Doors

		int doorRefId = -1;
		ShipCoordinate doorCoords = null;

		int doorsXYLRH[][] = new int[][] {
			new int[] { 14,  4,  0,  1, 1 },
			new int[] { 12,  4,  1,  3, 1 },
			new int[] { 12,  3,  1,  2, 1 },
			new int[] { 10,  3,  2,  4, 1 },
			new int[] { 10,  4,  3,  5, 1 },
			new int[] {  8,  4,  4,  5, 0 },
			new int[] {  8,  2,  4,  7, 1 },
			new int[] {  8,  5,  5,  8, 1 },
			new int[] {  7,  6,  8,  9, 0 },
			new int[] {  7,  2,  6,  7, 0 },
			new int[] {  6,  3,  7, 10, 1 },
			new int[] {  6,  4,  8, 10, 1 },
			new int[] {  4,  5, 10, 12, 0 },
			new int[] {  4,  3, 10, 11, 0 },
			new int[] {  3,  2, 11, 13, 1 },
			new int[] {  2,  3, 13, 14, 0 },
			new int[] {  2,  5, 14, 15, 0 },
			new int[] {  3,  5, 12, 15, 1 },
			new int[] {  1,  4, 14, 16, 1 },
			new int[] {  1,  3, 14, 16, 1 },
			new int[] {  0,  3, 16, -1, 1 },
			new int[] {  0,  4, 16, -1, 1 },
			new int[] {  6,  7,  9, -1, 0 },
			new int[] {  7,  7,  9, -1, 0 },
			new int[] {  6,  1,  6, -1, 0 },
			new int[] {  7,  1,  6, -1, 0 }
		};

		for ( int i=0; i < doorsXYLRH.length; i++ ) {
			int[] xylrh = doorsXYLRH[i];
			doorRefId = context.getNetManager().requestNewRefId();
			doorCoords = ShipCoordinate.door( xylrh[0], xylrh[1], xylrh[4] == 0 )[0];

			ShipDoorCreationEvent doorCreateEvent = Pools.get( ShipDoorCreationEvent.class ).obtain();
			doorCreateEvent.init( doorRefId );
			context.getScreenEventManager().postDelayedEvent( doorCreateEvent );

			ShipLayoutDoorAddEvent doorAddEvent = Pools.get( ShipLayoutDoorAddEvent.class ).obtain();
			doorAddEvent.init( shipRefId, doorRefId, doorCoords );
			context.getScreenEventManager().postDelayedEvent( doorAddEvent );
		}

		return shipRefId;
	}
}
