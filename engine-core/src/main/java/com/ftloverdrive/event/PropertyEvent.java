package com.ftloverdrive.event;

import com.badlogic.gdx.utils.Pool.Poolable;


/**
 * Classes extending this are only used by the handlers to figure out whether they should
 * handle the event or not.
 * 
 * TODO: Maybe there's a smarter way to go about this, and remove thsoe classes completely?
 */
public abstract class PropertyEvent extends AbstractOVDEvent implements Poolable {

	public static final int INT_TYPE = 0;
	public static final int BOOL_TYPE = 1;
	public static final int FLOAT_TYPE = 2;
	public static final int STRING_TYPE = 3;

	public static final int SET_ACTION = 0;
	/** Only applied to INT_TYPE and FLOAT_TYPE. */
	public static final int INCREMENT_ACTION = 1;
	/** Only applies to BOOL_TYPE. Negates the property's boolean value. boolValue field is ignored. */
	public static final int TOGGLE_ACTION = 2;

	protected int propertyType = -1;
	protected int action = -1;
	protected String propertyKey = null;

	protected int intValue = 0;
	protected float floatValue = 0;
	protected boolean boolValue = false;
	protected String stringValue = null;

	/** The refId of the model whose property this event modifies. */
	protected int modelRefId = -1;


	public PropertyEvent() {
	}

	private void checkTypeAndAction() {
		if ( action == INCREMENT_ACTION ) {
			if ( propertyType != INT_TYPE && propertyType != FLOAT_TYPE )
				throw new IllegalArgumentException( "Increment action only applicable for int or float PropertyEvent." );
		}
		else if ( action == TOGGLE_ACTION ) {
			if ( propertyType != BOOL_TYPE )
				throw new IllegalArgumentException( "Toggle action only applicable for boolean PropertyEvent" );
		}
	}

	private void init( int modelRefId, int propertyType, int action, String propertyKey ) {
		this.modelRefId = modelRefId;
		this.propertyType = propertyType;
		this.action = action;
		this.propertyKey = propertyKey;
		checkTypeAndAction();
	}

	/**
	 * Pseudo-constructor for an integer property change.
	 */
	public void init( int modelRefId, int propertyType, int action, String propertyKey, int intValue ) {
		init( modelRefId, propertyType, action, propertyKey );
		this.intValue = intValue;
	}

	/**
	 * Pseudo-constructor for a float property change.
	 */
	public void init( int modelRefId, int propertyType, int action, String propertyKey, float floatValue ) {
		init( modelRefId, propertyType, action, propertyKey );
		this.floatValue = floatValue;
	}

	/**
	 * Pseudo-constructor for a boolean property change.
	 */
	public void init( int modelRefId, int propertyType, int action, String propertyKey, boolean boolValue ) {
		init( modelRefId, propertyType, action, propertyKey );
		this.boolValue = boolValue;
	}

	/**
	 * Pseudo-constructor for a string property change.
	 */
	public void init( int modelRefId, int propertyType, int action, String propertyKey, String stringValue ) {
		init( modelRefId, propertyType, action, propertyKey );
		this.stringValue = stringValue;
	}

	public int getPropertyType() {
		return propertyType;
	}

	public int getAction() {
		return action;
	}

	public int getModelRefId() {
		return modelRefId;
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public int getIntValue() {
		if ( propertyType != INT_TYPE )
			throw new IllegalStateException( "Not an integer property event: " + propertyType );
		return intValue;
	}

	public float getFloatValue() {
		if ( propertyType != FLOAT_TYPE )
			throw new IllegalStateException( "Not a float property event: " + propertyType );
		return floatValue;
	}

	public boolean getBoolValue() {
		if ( propertyType != BOOL_TYPE )
			throw new IllegalStateException( "Not a boolean property event: " + propertyType );
		return boolValue;
	}

	public String getStringValue() {
		if ( propertyType != STRING_TYPE )
			throw new IllegalStateException( "Not a string property event: " + propertyType );
		return stringValue;
	}

	public void reset() {
		super.reset();
		modelRefId = -1;
		propertyType = -1;
		action = -1;
		propertyKey = null;
		intValue = 0;
	}
}
