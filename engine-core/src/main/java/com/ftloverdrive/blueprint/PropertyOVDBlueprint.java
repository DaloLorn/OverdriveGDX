package com.ftloverdrive.blueprint;

import java.util.HashMap;
import java.util.Map;


public abstract class PropertyOVDBlueprint extends AbstractOVDBlueprint {

	protected Map<String, Object> propertyMap;


	public PropertyOVDBlueprint( OVDBlueprint prototype ) {
		super( prototype );
		propertyMap = new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty( String propertyName, Class<T> type ) {
		T result = null;
		result = (T)propertyMap.get( propertyName );
		if ( result == null && prototype instanceof PropertyOVDBlueprint )
			result = ( (PropertyOVDBlueprint)prototype ).getProperty( propertyName, type );

		return result;
	}
}
