package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.PropertyEvent;


public class CrewPropertyEvent extends PropertyEvent implements Poolable {
	protected int crewRefId = -1;


	public CrewPropertyEvent() {
	}

	/**
	 * Pseudo-constructor for an integer property change.
	 */
	public void init( int crewRefId, int propertyType, int action, String propertyKey, int intValue ) {
		this.crewRefId = crewRefId;
		this.propertyType = propertyType;
		this.action = action;
		this.propertyKey = propertyKey;
		this.intValue = intValue;
	}

	/**
	 * Pseudo-constructor for a boolean property change.
	 */
	public void init( int crewRefId, int propertyType, int action, String propertyKey, boolean boolValue ) {
		this.crewRefId = crewRefId;
		this.propertyType = propertyType;
		this.action = action;
		this.propertyKey = propertyKey;
		this.boolValue = boolValue;
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
