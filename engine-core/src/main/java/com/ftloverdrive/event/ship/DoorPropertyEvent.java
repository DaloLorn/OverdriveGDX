package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;


public class DoorPropertyEvent extends AbstractPropertyEvent implements Poolable {
	protected int doorRefId = -1;


	public DoorPropertyEvent() {
	}

	/**
	 * Pseudo-constructor for an integer property change.
	 */
	public void init( int doorRefId, int propertyType, int action, String propertyKey, int intValue ) {
		this.doorRefId = doorRefId;
		this.propertyType = propertyType;
		this.action = action;
		this.propertyKey = propertyKey;
		this.intValue = intValue;
	}

	public void init( int doorRefId, int propertyType, int action, String propertyKey, boolean boolValue ) {
		this.doorRefId = doorRefId;
		this.propertyType = propertyType;
		this.action = action;
		this.propertyKey = propertyKey;
		this.boolValue = boolValue;
	}

	public int getDoorRefId() {
		return doorRefId;
	}

	@Override
	public void reset() {
		super.reset();
		doorRefId = -1;
	}
}
