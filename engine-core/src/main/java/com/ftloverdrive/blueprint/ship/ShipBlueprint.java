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
	 * Creates ShipCreationsEvent and sends it the event manager.
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
	 * 			X position of rooms first tile
	 * @param y
	 * 			Y position of rooms first tile
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
	 * 			X position of rooms first tile
	 * @param y
	 * 			Y position of rooms first tile
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
	 * 			X position of rooms first tile
	 * @param y
	 * 			Y position of rooms first tile
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

		return 0;
	}
}
