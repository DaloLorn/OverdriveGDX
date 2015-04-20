package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;
import com.ftloverdrive.model.ship.ShipCoordinate;


public class ShipLayoutSystemIconAddEvent extends AbstractOVDEvent implements Poolable {

	protected int shipRefId = -1;
	protected int roomRefId = -1;
	protected ShipCoordinate iconCoords = null;


	public ShipLayoutSystemIconAddEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param shipRefId
	 *            a reserved reference id for the ShipModel
	 * @param roomRefId
	 *            a reserved reference id for the RoomModel
	 * @param iconCoords
	 *            ShipCoordinates to associate with the icon
	 */
	public void init( int shipRefId, int roomRefId, ShipCoordinate iconCoords ) {
		this.shipRefId = shipRefId;
		this.roomRefId = roomRefId;
		this.iconCoords = iconCoords;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public int getRoomRefId() {
		return roomRefId;
	}

	public ShipCoordinate getIconCoords() {
		return iconCoords;
	}

	@Override
	public void reset() {
		super.reset();
		shipRefId = -1;
		roomRefId = -1;
		iconCoords = null;
	}
}
