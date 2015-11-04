package com.ftloverdrive.blueprint.ship;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.PropertyOVDBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.*;
import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.model.ship.ShipLayout;
import com.ftloverdrive.util.OVDConstants;


public abstract class ShipBlueprint extends PropertyOVDBlueprint {
	private OverdriveContext context;

	public ShipBlueprint( ShipBlueprint prototype ) {
		super( prototype );
	}

	public String getBlueprintName() {
		return properties.getString( OVDConstants.BLUEPRINT_NAME );
	}

	@Override
	public int construct(OverdriveContext context) {
		this.context = context;
		int shipRefId = createShip();

		return shipRefId;
	}

	/**
	 * Creates ShipCreationsEvent and sends it to the event manager.
	 * @return
	 * 		returns ship reference id
     */
	private int createShip(){
		int shipRefId = context.getNetManager().requestNewRefId();

		ShipCreationEvent shipCreateEvent = Pools.get( ShipCreationEvent.class ).obtain();
		shipCreateEvent.init( shipRefId, getString( OVDConstants.BLUEPRINT_NAME ), properties );
		context.getScreenEventManager().postDelayedEvent( shipCreateEvent );

		return shipRefId;
	}

	/**
	 * Creates room on ship with {@code shipRefId}.
	 * @param shipRefId
	 * 			Id reference of ship to create room on.
	 * @param x
	 * 			X position of room's first tile
	 * @param y
	 * 			Y position of room's first tile
	 * @param w
	 * 			width of room in tiles
	 * @param h
	 * 			height of room in tiles
	 * @return
	 * 			returns room's id reference
	 */
	public int createRoom(int shipRefId, int x, int y, int w, int h){
		return createRoom(shipRefId, x, y, w, h, null, null);
	}

	/**
	 * Creates room on ship with {@code shipRefId}. Adds room decoration if {@code roomDecor} isn't {@code null}.
	 * @param shipRefId
	 * 			Id reference of ship to create room on.
	 * @param x
	 * 			X position of room's first tile
	 * @param y
	 * 			Y position of room's first tile
	 * @param w
	 * 			width of room in tiles
	 * @param h
	 * 			height of room in tiles
	 * @param roomDecor
	 * 			ImageSpec of room decor
	 * @return
	 * 			returns room's id reference
	 */
	public int createRoom(int shipRefId, int x, int y, int w, int h, ImageSpec roomDecor){
		return createRoom(shipRefId, x, y, w, h, roomDecor, null);
	}

	/**
	 * Creates room on ship with {@code shipRefId}. Adds room decoration if {@code roomDecor} isn't {@code null}.
	 * Creates system(from blueprint) and associates it with the room, if the {@code systemBlueprintName} isn't {@code null}.
	 * @param shipRefId
	 * 			Id reference of ship to create room on.
	 * @param x
	 * 			X position of room's first tile
	 * @param y
	 * 			Y position of room's first tile
	 * @param w
	 * 			width of room in tiles
	 * @param h
	 * 			height of room in tiles
	 * @param roomDecor
	 * 			ImageSpec of room decor
	 * @param systemBlueprintName
	 * 			Blueprint's name to create system off it.
     * @return
	 * 			returns room's id reference
     */
	public int createRoom(int shipRefId, int x, int y, int w, int h, ImageSpec roomDecor, String systemBlueprintName){
		ShipCoordinate[] roomCoords = null;

		int roomRefId = context.getNetManager().requestNewRefId();
		roomCoords = ShipLayout.createRoomCoords( x, y, w, h );

		ShipRoomCreationEvent roomCreateEvent = Pools.get( ShipRoomCreationEvent.class ).obtain();
		roomCreateEvent.init( roomRefId );
		context.getScreenEventManager().postDelayedEvent( roomCreateEvent );

		if ( roomDecor != null ) {
			ShipRoomImageChangeEvent roomDecorEvent = Pools.get( ShipRoomImageChangeEvent.class ).obtain();
			roomDecorEvent.init( ShipRoomImageChangeEvent.DECOR, roomRefId, roomDecor );
			context.getScreenEventManager().postDelayedEvent( roomDecorEvent );
		}

		ShipLayoutRoomAddEvent roomAddEvent = Pools.get( ShipLayoutRoomAddEvent.class ).obtain();
		roomAddEvent.init( shipRefId, roomRefId, roomCoords );
		context.getScreenEventManager().postDelayedEvent( roomAddEvent );

		ShipLayoutSystemIconAddEvent iconAddEvent = Pools.get( ShipLayoutSystemIconAddEvent.class ).obtain();
		iconAddEvent.init( shipRefId, roomRefId, x + ( w - 1 ) * 0.5f,
				y + ( h - 1 ) * 0.5f + ( h > 1 ? 1 : 0 ) );
		context.getScreenEventManager().postDelayedEvent( iconAddEvent );

		if(systemBlueprintName != null){
			SystemBlueprint blue = (SystemBlueprint) context.getBlueprintManager().getBlueprint(systemBlueprintName);
			blue.construct(context);
			blue.addSystemToRoom(shipRefId, roomRefId);
		}

		return roomRefId;
	}

	/**
	 * Create room from layout
	 * @param shipRefId
	 * 			Id reference of ship to create room on.
	 * @param x
	 * 			X position of room's first tile
	 * @param y
	 * 			Y position of room's first tile
	 * @param roomLayout
	 * 			pattern of room
	 * 			Example:
	 * 		 	 	roomLayout = "" + "d=w" + "\n" + "  c";
     * @return
     */
	public int createRoomFromLayout(int shipRefId, int x, int y, String roomLayout){
		int roomRefId = context.getNetManager().requestNewRefId();
		/*String roomLayout = "" + "d=w" + "\n" + "  c";
				*/
		ShipCoordinate[] roomCoords = ShipLayout.createRoomCoords( x, y, roomLayout );

		ShipRoomCreationEvent roomCreateEvent = Pools.get( ShipRoomCreationEvent.class ).obtain();
		roomCreateEvent.init( roomRefId );
		context.getScreenEventManager().postDelayedEvent( roomCreateEvent );

		ShipLayoutRoomAddEvent roomAddEvent = Pools.get( ShipLayoutRoomAddEvent.class ).obtain();
		roomAddEvent.init( shipRefId, roomRefId, roomCoords );
		context.getScreenEventManager().postDelayedEvent( roomAddEvent );

		ShipLayoutSystemIconAddEvent iconAddEvent = Pools.get( ShipLayoutSystemIconAddEvent.class ).obtain();
		// TODO: fix icon offset
		iconAddEvent.init( shipRefId, roomRefId, x, y );
		context.getScreenEventManager().postDelayedEvent( iconAddEvent );
		return roomRefId;
	}

	public int createDoor(int shipRefId, int x, int y, boolean vertical){
		ShipCoordinate doorCoords = null;

		int doorRefId = context.getNetManager().requestNewRefId();

		if(vertical == true){
			doorCoords = ShipCoordinate.doorVertical(x, y)[0];
		}
		else{
			doorCoords = ShipCoordinate.doorHorizontal(x, y)[0];
		}

		ShipDoorCreationEvent doorCreateEvent = Pools.get( ShipDoorCreationEvent.class ).obtain();
		doorCreateEvent.init( doorRefId );
		context.getScreenEventManager().postDelayedEvent( doorCreateEvent );

		ShipLayoutDoorAddEvent doorAddEvent = Pools.get( ShipLayoutDoorAddEvent.class ).obtain();
		doorAddEvent.init( shipRefId, doorRefId, doorCoords );
		context.getScreenEventManager().postDelayedEvent( doorAddEvent );

		return doorRefId;
	}

	// TODO: create crew member from blueprint
	public int createCrewMember(int shipRefId, int x, int y){
		int crewRefId = context.getNetManager().requestNewRefId();
		ShipCrewCreationEvent crewCreateEvent = Pools.get( ShipCrewCreationEvent.class ).obtain();
		crewCreateEvent.init( crewRefId );
		context.getScreenEventManager().postDelayedEvent( crewCreateEvent );

		ShipCrewAddEvent crewAddEvent = Pools.get( ShipCrewAddEvent.class ).obtain();
		crewAddEvent.init( shipRefId, crewRefId );
		context.getScreenEventManager().postDelayedEvent( crewAddEvent );

		ShipLayoutCrewPlacementEvent crewPlaceEvent = Pools.get( ShipLayoutCrewPlacementEvent.class ).obtain();
		ShipCoordinate coord = Pools.get( ShipCoordinate.class ).obtain();
		coord.init( x, y, ShipCoordinate.TYPE_SQUARE );
		crewPlaceEvent.init( shipRefId, crewRefId, coord );
		context.getScreenEventManager().postDelayedEvent( crewPlaceEvent );

		return crewRefId;
	}
}
