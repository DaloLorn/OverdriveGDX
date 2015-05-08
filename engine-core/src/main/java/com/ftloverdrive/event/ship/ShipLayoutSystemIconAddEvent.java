package com.ftloverdrive.event.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class ShipLayoutSystemIconAddEvent extends AbstractOVDEvent implements Poolable {

	protected int shipRefId = -1;
	protected int roomRefId = -1;
	protected Vector2 iconOffset;


	public ShipLayoutSystemIconAddEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param shipRefId
	 *            a reserved reference id for the ShipModel
	 * @param roomRefId
	 *            a reserved reference id for the RoomModel
	 * @param iconX
	 *            X location of the icon image from the ship's origin, in tiles.
	 *            Fractional values specify position between tiles.
	 * @param iconY
	 *            Y location of the icon image from the ship's origin, in tiles.
	 *            Fractional values specify position between tiles.
	 */
	public void init( int shipRefId, int roomRefId, float iconX, float iconY ) {
		this.shipRefId = shipRefId;
		this.roomRefId = roomRefId;
		iconOffset = new Vector2( iconX, iconY );
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param shipRefId
	 *            a reserved reference id for the ShipModel
	 * @param roomRefId
	 *            a reserved reference id for the RoomModel
	 * @param iconOffset
	 *            Location of the icon image from the ship's origin, in tiles.
	 *            Fractional values specify position between tiles.
	 */
	public void init( int shipRefId, int roomRefId, Vector2 iconOffset ) {
		this.shipRefId = shipRefId;
		this.roomRefId = roomRefId;
		this.iconOffset = iconOffset;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public int getRoomRefId() {
		return roomRefId;
	}

	public float getIconOffsetX() {
		return iconOffset.x;
	}

	public float getIconOffsetY() {
		return iconOffset.y;
	}

	public Vector2 getIconOffset() {
		return iconOffset;
	}

	@Override
	public void reset() {
		super.reset();
		shipRefId = -1;
		roomRefId = -1;
		iconOffset = null;
	}
}
