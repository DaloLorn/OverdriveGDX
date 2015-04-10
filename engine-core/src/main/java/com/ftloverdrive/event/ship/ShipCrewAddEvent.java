package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;

/**
 * Adds the entity to the crew of a ship, controllable by the owner of the ship.
 *
 */
public class ShipCrewAddEvent extends AbstractOVDEvent implements Poolable {
	protected int shipRefId = -1;
	protected int crewRefId = -1;


	public ShipCrewAddEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param shipRefId  a reserved reference id for the ShipModel
	 * @param crewRefId  a reserved reference id for the CrewModel
	 */
	public void init( int shipRefId, int crewRefId ) {
		this.shipRefId = shipRefId;
		this.crewRefId = crewRefId;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public int getCrewRefId() {
		return crewRefId;
	}

	@Override
	public void reset() {
		super.reset();
		shipRefId = -1;
		crewRefId = -1;
	}
}
