package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class ShipLayoutConnectTeleportPadsEvent extends AbstractOVDEvent implements Poolable {

	protected int tpadRefId = -1;
	protected int targetTpadRefId = -1;


	public ShipLayoutConnectTeleportPadsEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param tpadRefId
	 *            reference id for the TeleportPadModel
	 * @param targetTpadRefId
	 *            reference id for the connected TeleportPadModel
	 */
	public void init( int tpadRefId, int targetTpadRefId ) {
		this.tpadRefId = tpadRefId;
		this.targetTpadRefId = targetTpadRefId;
	}

	public int getTeleportPadRefId() {
		return tpadRefId;
	}

	public int getTargetTeleportPadRefId() {
		return targetTpadRefId;
	}

	@Override
	public void reset() {
		super.reset();
		tpadRefId = -1;
		targetTpadRefId = -1;
	}
}
