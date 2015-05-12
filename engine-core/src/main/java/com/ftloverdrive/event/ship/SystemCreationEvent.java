package com.ftloverdrive.event.ship;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class SystemCreationEvent extends AbstractOVDEvent implements Poolable {

	protected int systemRefId = -1;


	public SystemCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param systemRefId
	 *            a reserved reference id for the new system
	 */
	public void init( int systemRefId ) {
		this.systemRefId = systemRefId;
	}

	public int getSystemRefId() {
		return systemRefId;
	}

	@Override
	public void reset() {
		super.reset();
		systemRefId = -1;
	}
}
