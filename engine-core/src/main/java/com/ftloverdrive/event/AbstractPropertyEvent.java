package com.ftloverdrive.event;

import com.badlogic.gdx.utils.Pool.Poolable;


public abstract class AbstractPropertyEvent extends AbstractOVDEvent implements Poolable {
	public static final int INT_TYPE = 0;
	public static final int BOOL_TYPE = 1;

	public static final int SET_ACTION = 0;
	public static final int INCREMENT_ACTION = 1;
	/** Only applies to BOOL_TYPE. Negates the property's boolean value. boolValue field is ignored. */
	public static final int TOGGLE_ACTION = 2;

	protected int propertyType = -1;
	protected int action = -1;
	protected String propertyKey = null;
	protected int intValue = 0;
	protected boolean boolValue = false;

	public AbstractPropertyEvent() {
	}

	public int getPropertyType() {
		return propertyType;
	}

	public int getAction() {
		return action;
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public int getIntValue() {
		if ( propertyType != INT_TYPE )
			throw new IllegalStateException( "Not an integer property event: " + propertyType );
		return intValue;
	}

	public boolean getBoolValue() {
		if ( propertyType != BOOL_TYPE )
			throw new IllegalStateException( "Not a boolean property event: " + propertyType );
		return boolValue;
	}

	public void reset() {
		super.reset();
		propertyType = -1;
		action = -1;
		propertyKey = null;
		intValue = 0;
	}
}
