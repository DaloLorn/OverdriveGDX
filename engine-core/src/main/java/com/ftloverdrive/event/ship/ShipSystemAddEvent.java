package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class ShipSystemAddEvent extends AbstractOVDEvent implements Poolable {

	protected int shipRefId = -1;
	protected int roomRefId = -1;
	protected int systemRefId = -1;


	public ShipSystemAddEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param shipRefId
	 *            a reserved reference id for the ShipModel
	 * @param roomRefId
	 *            a reserved reference id for the RoomModel
	 * @param systemRefId
	 *            a reserved reference id for the SystemModel
	 */
	public void init( int shipRefId, int roomRefId, int systemRefId ) {
		this.shipRefId = shipRefId;
		this.roomRefId = roomRefId;
		this.systemRefId = systemRefId;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public int getRoomRefId() {
		return roomRefId;
	}

	public int getSystemRefId() {
		return systemRefId;
	}

	@Override
	public void reset() {
		super.reset();
		shipRefId = -1;
		roomRefId = -1;
		systemRefId = -1;
	}
}
