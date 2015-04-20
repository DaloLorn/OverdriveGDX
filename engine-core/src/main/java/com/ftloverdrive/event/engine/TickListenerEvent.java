package com.ftloverdrive.event.engine;

import com.ftloverdrive.event.AbstractOVDEvent;


/**
 * This event instructs the server that it should in/decrement its
 * internal ref count of listeners interested in TickEvents with
 * the specified amount of ticks.
 */
public class TickListenerEvent extends AbstractOVDEvent {

	protected int tickCount = 0;
	protected boolean increment = true;


	protected TickListenerEvent() {
	}

	public TickListenerEvent( int tickCount, boolean increment ) {
		this.tickCount = tickCount;
		this.increment = increment;
	}

	public boolean isIncrement() {
		return increment;
	}

	public int getTickCount() {
		return tickCount;
	}
}
