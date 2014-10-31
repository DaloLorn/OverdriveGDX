package com.ftloverdrive.event;

import com.badlogic.gdx.utils.Pool.Poolable;


public abstract class AbstractOVDEvent implements OVDEvent, Poolable {
	protected int sourceRefId = -1;
	protected boolean cancelled = false;


	public void setSource( int srcRefId ) {
		this.sourceRefId = srcRefId;
	}

	public int getSource() {
		return sourceRefId;
	}

	public void cancel() {
		cancelled = true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void reset() {
		sourceRefId = -1;
		cancelled = false;
	}
}
