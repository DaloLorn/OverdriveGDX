package com.ftloverdrive.blueprint;



/**
 * Instructions and defaults to construct OVDModels.
 * Chain would look like this:
 * data files -> blueprints -> models -> actors
 *
 * Follows:
 * http://gameprogrammingpatterns.com/prototype.html#prototypes-for-data-modeling
 * 
 * TODO: Define base classes for each entity in FTL, eg
 * - WeaponBlueprint
 * - ShipBlueprint
 * - EventBlueprint
 * ...
 */
public interface OVDBlueprint {

	/**
	 * Returns the prototype (parent) that this blueprint inherits from, or null if it has no prototype.
	 */
	public OVDBlueprint getPrototype();

	/**
	 * Scans the blueprint, looking for the specified property name.
	 * If not found, looks it up in the prototype.
	 * If it's not found anywhere, returns null. // TODO or throws an exception?
	 */
	public <T> T getProperty( String propertyName, Class<T> type );
}
