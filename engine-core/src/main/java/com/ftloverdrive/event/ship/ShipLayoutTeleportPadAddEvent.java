package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;
import com.ftloverdrive.model.ship.ShipCoordinate;


public class ShipLayoutTeleportPadAddEvent extends AbstractOVDEvent implements Poolable {

	protected int shipRefId = -1;
	protected int tpadRefId = -1;
	protected ShipCoordinate tpadCoords = null;


	public ShipLayoutTeleportPadAddEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param shipRefId
	 *            a reserved reference id for the ShipModel
	 * @param tpadRefId
	 *            a reserved reference id for the TeleportPadModel
	 * @param tpadCoords
	 *            ShipCoordinates to associate with the teleport pad
	 */
	public void init( int shipRefId, int tpadRefId, ShipCoordinate tpadCoords ) {
		this.shipRefId = shipRefId;
		this.tpadRefId = tpadRefId;
		this.tpadCoords = tpadCoords;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public int getTeleportPadRefId() {
		return tpadRefId;
	}

	public ShipCoordinate getTeleportPadCoords() {
		return tpadCoords;
	}

	@Override
	public void reset() {
		super.reset();
		shipRefId = -1;
		tpadRefId = -1;
		tpadCoords = null;
	}
}
