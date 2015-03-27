package com.ftloverdrive.blueprint;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.ftloverdrive.model.OVDModel;


public class OVDBlueprintManager {

	private Map<String, OVDBlueprint> idBlueprintMap = new HashMap<String, OVDBlueprint>();
	private Map<String, Class<? extends OVDModel>> idModelMap =
			new HashMap<String, Class<? extends OVDModel>>();


	public OVDBlueprintManager() {
	}

	/**
	 * Stores the blueprint under the specified name in the manager. The blueprint can be later
	 * retrieved using getBlueprint()
	 * 
	 * @param blueprintId
	 *            string identifier of the blueprint, aka blueprint name
	 * @param blueprint
	 *            the blueprint to store
	 */
	public void storeBlueprint( String blueprintId, OVDBlueprint blueprint ) {
		idBlueprintMap.put( blueprintId, blueprint );
	}

	public OVDBlueprint getBlueprint( String blueprintId ) {
		return idBlueprintMap.get( blueprintId );
	}

	/**
	 * Associates the model class with the specified blueprint id.
	 * 
	 * During construction, the blueprint can optionally check whether a model has been
	 * associated with it, and create an instance of that model's class instead of the
	 * default one.
	 * 
	 * @param blueprintId
	 *            string identifier of the blueprint, aka blueprint name
	 * @param modelClass
	 *            the custom model's class
	 */
	public void associateModel( String blueprintId, Class<? extends OVDModel> modelClass ) {
		idModelMap.put( blueprintId, modelClass );
	}

	public Class<? extends OVDModel> getModelClass( String blueprintId ) {
		return idModelMap.get( blueprintId );
	}

	/**
	 * Creates an instance of the model class that has been associated with the specified
	 * blueprint, using the constructor matching the passed arguments.
	 * 
	 * Returns null if the blueprint had no model class associated with it.
	 * 
	 * @param blueprintId
	 *            string identifier of the blueprint, aka blueprint name
	 * @param args
	 *            the arguments to pass to the model's constructor
	 */
	public <T extends OVDModel> T createModel( String blueprintId, Object... args ) {
		if ( idModelMap.containsKey( blueprintId ) ) {
			try {
				Class<T> type = (Class<T>)idModelMap.get( blueprintId );
				T result = null;
				if ( args != null && args.length > 0 ) {
					Class[] argTypes = new Class[args.length];
					for ( int i = 0; i < args.length; ++i )
						argTypes[i] = args[i].getClass();
					Constructor constructor = ClassReflection.getConstructor( type, argTypes );
					result = type.cast( constructor.newInstance( args ) );
				}
				else {
					Constructor constructor = ClassReflection.getConstructor( type );
					result = type.cast( constructor.newInstance() );
				}
				return result;
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
