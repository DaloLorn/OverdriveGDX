package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractPropertyEvent;


public class ShipPropertyEvent extends AbstractPropertyEvent implements Poolable {
	protected int shipRefId = -1;


	public ShipPropertyEvent() {
	}

	/**
	 * Pseudo-constructor for an integer property change.
	 */
	public void init( int shipRefId, int propertyType, int action, String propertyKey, int intValue ) {
		this.shipRefId = shipRefId;
		this.propertyType = propertyType;
		this.action = action;
		this.propertyKey = propertyKey;
		this.intValue = intValue;
	}

	public void init( int shipRefId, int propertyType, int action, String propertyKey, boolean boolValue ) {
		this.shipRefId = shipRefId;
		this.propertyType = propertyType;
		this.action = action;
		this.propertyKey = propertyKey;
		this.boolValue = boolValue;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	@Override
	public void reset() {
		shipRefId = -1;
		propertyType = -1;
		action = -1;
		propertyKey = null;
		intValue = 0;
	}
}
