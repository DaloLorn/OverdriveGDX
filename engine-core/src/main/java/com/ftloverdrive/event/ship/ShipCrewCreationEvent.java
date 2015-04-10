package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;

public class ShipCrewCreationEvent extends AbstractOVDEvent implements Poolable {
	protected int crewRefId = -1;


	public ShipCrewCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param crewRefId  a reserved reference id for the new door
	 */
	public void init( int crewRefId ) {
		this.crewRefId = crewRefId;
	}

	public int getCrewRefId() {
		return crewRefId;
	}

	@Override
	public void reset() {
		super.reset();
		crewRefId = -1;
	}
}
