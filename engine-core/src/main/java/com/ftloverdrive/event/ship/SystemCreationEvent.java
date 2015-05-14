package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;
import com.ftloverdrive.model.NamedProperties;


public class SystemCreationEvent extends AbstractOVDEvent implements Poolable {

	protected int systemRefId = -1;
	protected String systemBlueprintId = null;
	protected NamedProperties properties = null;


	public SystemCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param systemRefId
	 *            a reserved reference id for the new system
	 * @param blueprintId
	 *            id of the system's blueprint
	 */
	public void init( int systemRefId, String blueprintId, NamedProperties properties ) {
		this.systemRefId = systemRefId;
		systemBlueprintId = blueprintId;
		this.properties = properties;
	}

	public int getSystemRefId() {
		return systemRefId;
	}

	public String getSystemBlueprintId() {
		return systemBlueprintId;
	}

	public NamedProperties getProperties() {
		return properties;
	}

	@Override
	public void reset() {
		super.reset();
		systemRefId = -1;
		systemBlueprintId = null;
		properties = null;
	}
}
