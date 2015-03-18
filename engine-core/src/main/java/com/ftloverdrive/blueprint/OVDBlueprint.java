package com.ftloverdrive.blueprint;

import com.ftloverdrive.core.OverdriveContext;


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
	 * Constructs the model from this blueprint and returns its reference id.
	 */
	public int construct( OverdriveContext context );
}
