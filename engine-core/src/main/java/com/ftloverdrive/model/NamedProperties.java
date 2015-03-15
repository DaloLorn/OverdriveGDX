package com.ftloverdrive.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ftloverdrive.util.MutableBool;
import com.ftloverdrive.util.MutableFloat;
import com.ftloverdrive.util.MutableInt;
import com.ftloverdrive.util.MutableString;


/**
 * A collection of arbitrarily named values.
 */
public class NamedProperties {

	public static final int INT_TYPE = 0;
	public static final int BOOL_TYPE = 1;
	public static final int FLOAT_TYPE = 2;
	public static final int STRING_TYPE = 3;

	/** Lazily initialized map of named properties. */
	protected Map<String,MutableInt> namedIntMap = null;
	protected Map<String,MutableBool> namedBoolMap = null;
	protected Map<String,MutableFloat> namedFloatMap = null;
	protected Map<String,MutableString> namedStringMap = null;

	/** Lazily initialized map of named properties' keys. */
	protected Set<String> namedIntKeysView = null;
	protected Set<String> namedBoolKeysView = null;
	protected Set<String> namedFloatKeysView = null;
	protected Set<String> namedStringKeysView = null;

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
	 * Sets a named float variable.
	 */
	public void setFloat( String key, float f ) {
		MutableFloat value = null;
		if ( namedFloatMap == null ) {
			namedFloatMap = new HashMap<String,MutableFloat>();
		} else {
			value = namedFloatMap.get( key );
		}
		if ( value == null ) {
			value = new MutableFloat();
			namedFloatMap.put( key, value );
		}

		value.set( f );
	}

	/**
	 * Sets a named string variable.
	 */
	public void setString( String key, String s ) {
		MutableString value = null;
		if ( namedStringMap == null ) {
			namedStringMap = new HashMap<String,MutableString>();
		} else {
			value = namedStringMap.get( key );
		}
		if ( value == null ) {
			value = new MutableString();
			namedStringMap.put( key, value );
		}

		value.set( s );
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
	 * Adds an amount to a named float variable.
	 */
	public void incrementFloat( String key, float f ) {
		MutableFloat value = null;
		if ( namedFloatMap == null ) {
			namedFloatMap = new HashMap<String,MutableFloat>();
		} else {
			value = namedFloatMap.get( key );
		}
		if ( value == null ) {
			value = new MutableFloat();
			namedFloatMap.put( key, value );
		}

		value.increment( f );
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
	 * Returns the value of a named float variable, or 0.0 if not set.
	 */
	public float getFloat( String key ) {
		if ( namedFloatMap == null ) return 0;
		MutableFloat value = namedFloatMap.get( key );
		if ( value == null ) return 0;
		return value.get();
	}

	/**
	 * Returns the value of a named string variable, or empty string if not set.
	 */
	public String getString( String key ) {
		if ( namedStringMap == null ) return "";
		MutableString value = namedStringMap.get( key );
		if ( value == null ) return "";
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
	 * Returns true if a named float variable has been set.
	 */
	public boolean hasFloat( String key ) {
		if ( namedFloatMap == null ) return false;
		return namedFloatMap.containsKey( key );
	}

	/**
	 * Returns true if a named string variable has been set.
	 */
	public boolean hasString( String key ) {
		if ( namedStringMap == null ) return false;
		return namedStringMap.containsKey( key );
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

	/**
	 * Returns a read-only view of named float variables' keys.
	 */
	public Set<String> getFloatKeys() {
		if ( namedFloatKeysView == null ) {
			namedFloatKeysView = Collections.unmodifiableSet( namedFloatMap.keySet() );
		}
		return namedFloatKeysView;
	}

	/**
	 * Returns a read-only view of named string variables' keys.
	 */
	public Set<String> getStringKeys() {
		if ( namedStringKeysView == null ) {
			namedStringKeysView = Collections.unmodifiableSet( namedStringMap.keySet() );
		}
		return namedStringKeysView;
	}
}
