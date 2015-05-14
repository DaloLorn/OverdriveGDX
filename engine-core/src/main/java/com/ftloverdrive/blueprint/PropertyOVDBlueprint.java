package com.ftloverdrive.blueprint;

import com.ftloverdrive.model.NamedProperties;


public abstract class PropertyOVDBlueprint extends AbstractOVDBlueprint {

	protected NamedProperties properties;


	public PropertyOVDBlueprint( OVDBlueprint prototype ) {
		super( prototype );
		properties = new NamedProperties();
	}

	public NamedProperties getProperties() {
		return properties;
	}

	public Integer getInt( String key ) {
		Integer result = null;
		if ( properties.hasInt( key ) )
			result = properties.getInt( key );
		else if ( prototype instanceof PropertyOVDBlueprint )
			result = ( (PropertyOVDBlueprint)prototype ).getInt( key );
		return result;
	}

	public Float getFloat( String key ) {
		Float result = null;
		if ( properties.hasFloat( key ) )
			result = properties.getFloat( key );
		else if ( prototype instanceof PropertyOVDBlueprint )
			result = ( (PropertyOVDBlueprint)prototype ).getFloat( key );
		return result;
	}

	public Boolean getBool( String key ) {
		Boolean result = null;
		if ( properties.hasBool( key ) )
			result = properties.getBool( key );
		else if ( prototype instanceof PropertyOVDBlueprint )
			result = ( (PropertyOVDBlueprint)prototype ).getBool( key );
		return result;
	}

	public String getString( String key ) {
		String result = null;
		if ( properties.hasString( key ) )
			result = properties.getString( key );
		else if ( prototype instanceof PropertyOVDBlueprint )
			result = ( (PropertyOVDBlueprint)prototype ).getString( key );
		return result;
	}
}
