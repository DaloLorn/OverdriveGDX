package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;
import com.ftloverdrive.model.ship.ShipCoordinate;


public class ShipLayoutDoorAddEvent extends AbstractOVDEvent implements Poolable {
	protected int shipRefId = -1;
	protected int doorRefId = -1;
	protected ShipCoordinate doorCoords = null;


	public ShipLayoutDoorAddEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param shipRefId  a reserved reference id for the ShipModel
	 * @param doorRefId  a reserved reference id for the DoorModel
	 * @param roomCoords  ShipCoordinates to associate with the door
	 */
	public void init( int shipRefId, int doorRefId, ShipCoordinate doorCoords ) {
		this.shipRefId = shipRefId;
		this.doorRefId = doorRefId;
		this.doorCoords = doorCoords;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public int getDoorRefId() {
		return doorRefId;
	}

	public ShipCoordinate getDoorCoords() {
		return doorCoords;
	}

	@Override
	public void reset() {
		super.reset();
		shipRefId = -1;
		doorRefId = -1;
		doorCoords = null;
	}
}
