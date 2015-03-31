package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.Damagable;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.model.OVDModel;


public interface ShipModel extends OVDModel, Damagable {

	public NamedProperties getProperties();


	/**
	 * Returns this ship's layout.
	 */
	public ShipLayout getLayout();

	/**
	 * Returns an array of ref ids of the ship's crew.
	 */
	public Integer[] crewRefIds();

	/**
	 * Adds the CrewModel specified by the ref id to the ship, essentially making them crew of the ship,
	 * and changing the owner of the crew to the player owning the ship.
	 * 
	 * To just place the crew in the layout space of another ship, use {@link ShipLayout#placeCrew(int, ShipCoordinate)}
	 * 
	 * Returns true if crew was successfully added; false otherwise (eg. already at max crew capacity)
	 */
	public boolean addCrew( int crewRefId );

	/**
	 * Shifts *all* graphics (hull images, shield and rooms).
	 *
	 * In Overdrive: +X is right, +Y is up.
	 *
	 * The original FTL set this in shipname.txt.
	 * X_OFFSET*35 + HORIZONTAL
	 * Y_OFFSET*35 + VERTICAL
	 * There, +X was right, +Y was down.
	 */
	public void setShipOffset( float x, float y );

	public float getShipOffsetX();

	public float getShipOffsetY();

	/**
	 * Shifts the hull images only, but not shield and rooms.
	 *
	 * In Overdrive: +X is right, +Y is up.
	 *
	 * The original FTL set this in snipname.xml.
	 * The img tag's x and y attributes behaved similarly.
	 * There, +X was right, +Y was down.
	 */
	public void setHullOffset( float x, float y );

	public float getHullOffsetX();

	public float getHullOffsetY();

	/**
	 * Sets the size of hull image.
	 *
	 * The base image will be scaled to WxH.
	 * The floor, cloak and shield images will not be scaled.
	 *
	 * The original FTL set this in snipname.xml.
	 * The img tag's w and h attributes behaved similarly.
	 * 
	 * TODO: since AE, hull size is no longer really used for anything (both
	 * floor and cloak are drawn as they are, without any scaling), remove?
	 */
	public void setHullSize( float width, float height );

	public float getHullWidth();

	public float getHullHeight();

	/**
	 * Shifts the floor image only.
	 * 
	 * In Overdrive: +X is right, +Y is up.
	 * 
	 * The original FTL set this in snipname.xml.
	 * The floor tag's x and y attributes behaved similarly.
	 * There, +X was right, +Y was down.
	 */
	public void setFloorOffset( float x, float y );

	public float getFloorOffsetX();

	public float getFloorOffsetY();

	/**
	 * Shifts the cloak image only.
	 * 
	 * In Overdrive: +X is right, +Y is up.
	 * 
	 * The original FTL set this in snipname.xml.
	 * The cloak tag's x and y attributes behaved similarly.
	 * There, +X was right, +Y was down.
	 */
	public void setCloakOffset( float x, float y );

	public float getCloakOffsetX();

	public float getCloakOffsetY();


	/**
	 * Sets a number of things about shields.
	 *
	 * Sets an elliptical region that absorbs incoming projectiles.
	 * Sets an elliptical path for orbiting satellites.
	 * The shield image will be centered on the ellipse's center.
	 * The shield image will not be scaled.
	 *
	 * The x and y args are offsets relative to the hull offset.
	 * When x=0 and y=0, the lower-left corner of the ellipse bounds is at
	 * the hull offset.
	 *
	 * The axes are half the total width/height of the ellipse.
	 *
	 * In Overdrive: +X is right, +Y is up.
	 *
	 * The original FTL set this in shipname.txt.
	 * ELLIPSE
	 * There, +X was right, +Y was down.
	 */
	public void setShieldEllipse( float x, float y, float semiMajorAxis, float semiMinorAxis );

	public float getShieldEllipseOffsetX();

	public float getShieldEllipseOffsetY();

	public float getShieldEllipseSemiMajorAxis();

	public float getShieldEllipseSemiMinorAxis();


	/**
	 * Sets the ship's base image.
	 */
	public void setBaseImageSpec( ImageSpec imageSpec );

	public ImageSpec getBaseImageSpec();

	/**
	 * Sets the ship's cloak image.
	 */
	public void setCloakImageSpec( ImageSpec imageSpec );

	public ImageSpec getCloakImageSpec();

	/**
	 * Sets the ship's floor image.
	 */
	public void setFloorImageSpec( ImageSpec imageSpec );

	public ImageSpec getFloorImageSpec();

	/**
	 * Sets the ship's shield image.
	 */
	public void setShieldImageSpec( ImageSpec imageSpec );

	public ImageSpec getShieldImageSpec();
}
