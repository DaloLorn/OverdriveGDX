package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;
import com.ftloverdrive.model.NamedProperties;


public class ShipCreationEvent extends AbstractOVDEvent implements Poolable {

	protected int shipRefId = -1;
	protected String shipBlueprintId = null;
	protected NamedProperties properties = null;


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
	public void init( int shipRefId, String shipBlueprintId, NamedProperties properties ) {
		this.shipRefId = shipRefId;
		this.shipBlueprintId = shipBlueprintId;
		this.properties = properties;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public String getShipBlueprintId() {
		return shipBlueprintId;
	}

	public NamedProperties getProperties() {
		return properties;
	}

	@Override
	public void reset() {
		super.reset();
		shipRefId = -1;
		shipBlueprintId = null;
		properties = null;
	}
}
