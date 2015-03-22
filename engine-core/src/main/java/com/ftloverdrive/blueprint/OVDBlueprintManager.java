package com.ftloverdrive.blueprint;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.ftloverdrive.model.OVDModel;


public class OVDBlueprintManager {

	private Map<String, OVDBlueprint> idBlueprintMap = new HashMap<String, OVDBlueprint>();
	private Map<String, Class<? extends OVDModel>> idModelMap =
			new HashMap<String, Class<? extends OVDModel>>();


	public OVDBlueprintManager() {
	}

	public void storeBlueprint( String blueprintId, OVDBlueprint blueprint, Class<? extends OVDModel> modelType ) {
		idBlueprintMap.put( blueprintId, blueprint );
		idModelMap.put( blueprintId, modelType );
	}

	public OVDBlueprint getBlueprint( String blueprintId ) {
		return idBlueprintMap.get( blueprintId );
	}

	public Class<? extends OVDModel> getModelClass( String blueprintId ) {
		return idModelMap.get( blueprintId );
	}

	public <T extends OVDModel> T createModel( String blueprintId, Object... args ) {
		if ( idModelMap.containsKey( blueprintId ) ) {
			try {
				Class type = idModelMap.get( blueprintId );
				T result = null;
				if ( args.length > 0 ) {
					Class[] argTypes = new Class[args.length];
					for ( int i = 0; i < args.length; ++i )
						argTypes[i] = args[i].getClass();
					Constructor<T> constructor = (Constructor<T>)type.getConstructor( argTypes );
					result = constructor.newInstance( args );
				}
				else {
					Constructor<T> constructor = (Constructor<T>)type.getConstructor();
					result = constructor.newInstance();
				}

				return result;
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		else {
			// TODO: Throw an error
		}

		return null;
	}
}
