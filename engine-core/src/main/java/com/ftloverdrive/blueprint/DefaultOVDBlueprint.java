package com.ftloverdrive.blueprint;

import java.util.HashMap;
import java.util.Map;

public class DefaultOVDBlueprint implements OVDBlueprint {

	protected Map<String, Object> propertyMap;
	protected OVDBlueprint prototype;


	public DefaultOVDBlueprint( OVDBlueprint prototype ) {
		propertyMap = new HashMap<String, Object>();
		this.prototype = prototype;
	}

	@Override
	public OVDBlueprint getPrototype() {
		return prototype;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty( String propertyName, Class<T> type ) {
		T result = null;
		result = (T) propertyMap.get( propertyName );
		if ( result == null && prototype != null )
			result = prototype.getProperty( propertyName, type );

		return result;
	}
}
