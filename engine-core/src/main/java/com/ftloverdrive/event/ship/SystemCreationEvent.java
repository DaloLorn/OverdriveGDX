package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class SystemCreationEvent extends AbstractOVDEvent implements Poolable {

	protected int systemRefId = -1;
	protected String systemBlueprintId = null;
	protected Object[] constructorArgs = null;


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
	public void init( int systemRefId, String blueprintId, Object... constructorArgs ) {
		this.systemRefId = systemRefId;
		systemBlueprintId = blueprintId;
		this.constructorArgs = constructorArgs;
	}

	public int getSystemRefId() {
		return systemRefId;
	}

	public String getSystemBlueprintId() {
		return systemBlueprintId;
	}

	public Object[] getConstructorArgs() {
		return constructorArgs;
	}

	@Override
	public void reset() {
		super.reset();
		systemRefId = -1;
		systemBlueprintId = null;
	}
}
