package com.ftloverdrive.event;


/**
 * An event wrapper that allows posting of significantly delayed events.
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
