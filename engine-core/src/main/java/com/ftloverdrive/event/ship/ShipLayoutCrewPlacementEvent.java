package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;
import com.ftloverdrive.model.ship.ShipCoordinate;

/**
 * Places the crewmember within the space of a ship.
 *
 */
public class ShipLayoutCrewPlacementEvent extends AbstractOVDEvent implements Poolable {
	protected int shipRefId = -1;
	protected int crewRefId = -1;
	protected ShipCoordinate crewCoords = null;


	public ShipLayoutCrewPlacementEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param shipRefId  a reserved reference id for the ShipModel
	 * @param crewRefId  a reserved reference id for the CrewModel
	 * @param roomCoords  ShipCoordinates at which to place the crew member
	 */
	public void init( int shipRefId, int crewRefId, ShipCoordinate crewCoords ) {
		this.shipRefId = shipRefId;
		this.crewRefId = crewRefId;
		this.crewCoords = crewCoords;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public int getCrewRefId() {
		return crewRefId;
	}

	public ShipCoordinate getCrewCoords() {
		return crewCoords;
	}

	@Override
	public void reset() {
		super.reset();
		shipRefId = -1;
		crewRefId = -1;
		crewCoords = null;
	}
}
