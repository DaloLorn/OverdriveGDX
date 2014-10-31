package com.ftloverdrive.util;


public class OVDConstants {

	/*
	 * ========================================
	 * Orientation / direction / facing.
	 */

	/** A constant indicating northern orientation, ie. facing upwards. */
	public static final int ORIENT_NORTH = 0;

	/** A constant indicating southern orientation, ie. facing downwards. */
	public static final int ORIENT_SOUTH = 1;

	/** A contant indicating eastern orientation, ie. facing rightwards. */
	public static final int ORIENT_EAST = 2;

	/** A constant indicating western orientation, ie. facing leftwards. */
	public static final int ORIENT_WEST = 3;

	/*
	 * ========================================
	 * Ship properties.
	 */

	/** Ship property. */
	public static final String HULL_MAX = "HullMax";

	/** Ship property. */
	public static final String HULL = "Hull";

	/** Ship property. */
	public static final String POWER_MAX = "PowerMax";

	/** Ship property. */
	public static final String POWER = "Power";

	/** Ship property. */
	public static final String SCRAP = "Scrap"; // TODO player property instead?

	/** Ship property. */
	public static final String FUEL = "Fuel";

	/** Ship property. */
	public static final String MISSILES = "Missiles";

	/** Ship property. */
	public static final String DRONE_PARTS = "DroneParts";

	/** Ship property. */
	public static final String WEAPON_SLOTS = "WeaponSlots";

	/** Ship property. */
	public static final String DRONE_SLOTS = "DroneSlots";

	/** Ship property. */
	public static final String AUGMENT_SLOTS = "AugmentSlots";

	/** In enemy ships, indicates the minimum sector in which the ship can start appearing. */
	public static final String SECTOR_MIN = "SectorMin";

	/** In enemy ships, indicates the maximum sector in which the ship can still appear. */
	public static final String SECTOR_MAX = "SectorMax";

	/** Strength of the ship's doors. Determines which sprite to use to represent the door.
	 * -1 - hacked (pink-ish sprite)
	 * 0 - default sprite tinted red
	 * 1 - default (orange) sprite
	 * 2 - gray sprite
	 * 3 - gray sprite with marks
	 * 4 - gray sprite with more marks
	 */
	public static final String DOOR_LEVEL = "DoorUpgradeLevel";

	/*
	 * ========================================
	 * Door properties.
	 */

	public static final String DOOR_HEALTH_MAX = "DoorHeathMax";
	public static final String DOOR_HEALTH = "DoorHeath";
	public static final String DOOR_OPEN = "DoorOpen";
	
	/*
	 * ========================================
	 * Atlas paths.
	 */

	public static final String ROOT_ATLAS = "img/.atlas.atlas";
	public static final String BUTTONS_FTL_ATLAS = "img/buttons/FTL/.atlas.atlas";
	public static final String COMBATUI_ATLAS = "img/combatUI/.atlas.atlas";
	public static final String EFFECTS_ATLAS = "img/effects/.atlas.atlas";
	public static final String ICONS_ATLAS = "img/icons/.atlas.atlas";
	public static final String MISC_ATLAS = "img/misc/.atlas.atlas";
	public static final String PEOPLE_ATLAS = "img/people/.atlas.atlas";
	public static final String SHIP_ATLAS = "img/ship/.atlas.atlas";
	public static final String SHIP_INTERIOR_ATLAS = "img/ship/interior/.atlas.atlas";
	public static final String STATUSUI_ATLAS = "img/statusUI/.atlas.atlas";
	public static final String SYSTEMUI_ATLAS = "img/systemUI/.atlas.atlas";
	public static final String WEAPONS_ATLAS = "img/weapons/.atlas.atlas";
	public static final String BKG_ATLAS = "img/stars/.atlas.atlas";
	public static final String MENU_ATLAS = "img/main_menus/.atlas.atlas";

	public static final String FLOORPLAN_ATLAS = "internal://overdrive-assets/images/floorplan.atlas";
}
