package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;

import com.ftloverdrive.event.AbstractOVDEvent;


public class ShipCreationEvent extends AbstractOVDEvent implements Poolable {

	protected int shipRefId = -1;
	protected String shipBlueprintId = null;
	// TODO: This would mean sending an array of objects over net, probably not very smart...?
	protected Object[] constructorArgs = null;


	public ShipCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * TODO: Decide on a convention for shipType values.
	 * Have blueprints create the model without events?
	 *
	 * @param shipRefId
	 *            a reserved reference id for the new ship
	 * @param shipBlueprintId
	 *            a string identifying what ship to make
	 */
	public void init( int shipRefId, String shipBlueprintId, Object... constructorArgs ) {
		this.shipRefId = shipRefId;
		this.shipBlueprintId = shipBlueprintId;
		this.constructorArgs = constructorArgs;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public String getShipBlueprintId() {
		return shipBlueprintId;
	}

	public Object[] getConstructorArgs() {
		return constructorArgs;
	}

	@Override
	public void reset() {
		shipRefId = -1;
		shipBlueprintId = null;
		constructorArgs = null;
	}
}
