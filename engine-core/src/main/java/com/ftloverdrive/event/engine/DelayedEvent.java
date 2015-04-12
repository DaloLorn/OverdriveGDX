package com.ftloverdrive.event.engine;

import com.ftloverdrive.event.AbstractOVDEvent;
import com.ftloverdrive.event.OVDEvent;


/**
 * An event wrapper that allows posting of significantly delayed events.
 * 
 * TODO: Problem: if the event is enqueued right before a tick event happens,
 * the delay will be shorter, because the required tick count will be reached
 * sooner. Changing to real-time delay would fix it.
 */
public class DelayedEvent extends AbstractOVDEvent {

	protected OVDEvent event;
	protected int tickDelay = -1;


	protected DelayedEvent() {
	}

	public DelayedEvent( int tickDelay, OVDEvent event ) {
		if ( tickDelay < 1 )
			throw new IllegalArgumentException( "Tick delay must be greater than 0." );
		this.tickDelay = tickDelay;
		this.event = event;
	}

	public OVDEvent getEvent() {
		return event;
	}

	public int getTickDelay() {
		return tickDelay;
	}
}
