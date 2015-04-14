package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class ShipTeleportPadCreationEvent extends AbstractOVDEvent implements Poolable {
	protected int tpadRefId = -1;

	public ShipTeleportPadCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param tpadRefId
	 *            a reserved reference id for the new teleport pad
	 */
	public void init(int tpadRefId) {
		this.tpadRefId = tpadRefId;
	}

	public int getTeleportPadRefId() {
		return tpadRefId;
	}

	@Override
	public void reset() {
		super.reset();
		tpadRefId = -1;
	}
}
