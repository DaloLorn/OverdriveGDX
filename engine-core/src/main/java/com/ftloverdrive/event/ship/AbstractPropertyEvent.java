package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;

public abstract class AbstractPropertyEvent extends AbstractOVDEvent implements Poolable {
	public static final int INT_TYPE = 0;
	public static final int BOOL_TYPE = 1;

	public static final int SET_ACTION = 0;
	public static final int INCREMENT_ACTION = 1;
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
		propertyType = -1;
		action = -1;
		propertyKey = null;
		intValue = 0;
	}
}
