package com.ftloverdrive.event;


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
