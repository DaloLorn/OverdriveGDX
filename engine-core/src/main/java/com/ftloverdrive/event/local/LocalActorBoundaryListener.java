package com.ftloverdrive.event.local;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;

public interface LocalActorBoundaryListener extends OVDEventListener {

	/**
	 * Local player's cursor has crossed an actor's bounds (either exit or enter)
	 */
	public void actorBoundaryCrossed( OverdriveContext context, LocalActorBoundaryEvent e );
}
