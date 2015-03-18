package com.ftloverdrive.event.ship;

import com.ftloverdrive.event.AbstractOVDEvent;


public class ShipDoorCreationEvent extends AbstractOVDEvent {
	protected int doorRefId = -1;

	public ShipDoorCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param doorRefId
	 *            a reserved reference id for the new door
	 */
	public void init(int doorRefId) {
		this.doorRefId = doorRefId;
	}

	public int getDoorRefId() {
		return doorRefId;
	}

	@Override
	public void reset() {
		doorRefId = -1;
	}
}
