package com.ftloverdrive.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ftloverdrive.util.MutableBool;
import com.ftloverdrive.util.MutableInt;


/**
 * A collection of arbitrarily named values.
 */
public class NamedProperties {

	public static final int INT_TYPE = 0;
	public static final int BOOL_TYPE = 1;

	/** Lazily initialized map of named integers. */
	protected Map<String,MutableInt> namedIntMap = null;
	protected Map<String,MutableBool> namedBoolMap = null;

	/** Lazily initialized map of named integers' keys. */
	protected Set<String> namedIntKeysView = null;
	protected Set<String> namedBoolKeysView = null;

	public NamedProperties() {
	}

	/**
	 * Sets a named integer variable.
	 */
	public void setInt( String key, int n ) {
		MutableInt value = null;
		if ( namedIntMap == null ) {
			namedIntMap = new HashMap<String,MutableInt>();
		} else {
			value = namedIntMap.get( key );
		}
		if ( value == null ) {
			value = new MutableInt();
			namedIntMap.put( key, value );
		}

		value.set( n );
	}

	/**
	 * Sets a named boolean variable.
	 */
	public void setBool( String key, boolean b ) {
		MutableBool value = null;
		if ( namedBoolMap == null ) {
			namedBoolMap = new HashMap<String,MutableBool>();
		} else {
			value = namedBoolMap.get( key );
		}
		if ( value == null ) {
			value = new MutableBool();
			namedBoolMap.put( key, value );
		}

		value.set( b );
	}

	/**
	 * Adds an amount to a named integer variable.
	 */
	public void incrementInt( String key, int n ) {
		MutableInt value = null;
		if ( namedIntMap == null ) {
			namedIntMap = new HashMap<String,MutableInt>();
		} else {
			value = namedIntMap.get( key );
		}
		if ( value == null ) {
			value = new MutableInt();
			namedIntMap.put( key, value );
		}

		value.increment( n );
	}

	/**
	 * Negates a named boolean variable.
	 */
	public void toggleBool( String key ) {
		MutableBool value = null;
		if ( namedBoolMap == null ) {
			namedBoolMap = new HashMap<String,MutableBool>();
		} else {
			value = namedBoolMap.get( key );
		}
		if ( value == null ) {
			value = new MutableBool();
			namedBoolMap.put( key, value );
		}

		value.toggle();
	}

	/**
	 * Returns the value of a named integer variable, or 0 if not set.
	 */
	public int getInt( String key ) {
		if ( namedIntMap == null ) return 0;
		MutableInt value = namedIntMap.get( key );
		if ( value == null ) return 0;
		return value.get();
	}

	/**
	 * Returns the value of a named boolean variable, or false if not set.
	 */
	public boolean getBool( String key ) {
		if ( namedBoolMap == null ) return false;
		MutableBool value = namedBoolMap.get( key );
		if ( value == null ) return false;
		return value.get();
	}

	/**
	 * Returns true if a named integer variable has been set.
	 */
	public boolean hasInt( String key ) {
		if ( namedIntMap == null ) return false;
		return namedIntMap.containsKey( key );
	}

	/**
	 * Returns true if a named boolean variable has been set.
	 */
	public boolean hasBool( String key ) {
		if ( namedBoolMap == null ) return false;
		return namedBoolMap.containsKey( key );
	}

	/**
	 * Returns a read-only view of named integer variables' keys.
	 */
	public Set<String> getIntKeys() {
		if ( namedIntKeysView == null ) {
			namedIntKeysView = Collections.unmodifiableSet( namedIntMap.keySet() );
		}
		return namedIntKeysView;
	}

	/**
	 * Returns a read-only view of named boolean variables' keys.
	 */
	public Set<String> getBoolKeys() {
		if ( namedBoolKeysView == null ) {
			namedBoolKeysView = Collections.unmodifiableSet( namedBoolMap.keySet() );
		}
		return namedBoolKeysView;
	}
}
