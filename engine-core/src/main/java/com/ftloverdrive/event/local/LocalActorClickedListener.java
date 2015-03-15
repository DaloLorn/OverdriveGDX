package com.ftloverdrive.event.local;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;

public interface LocalActorClickedListener extends OVDEventListener {

	/**
	 * Local player has clicked on an actor.
	 */
	public void actorClicked( OverdriveContext context, LocalActorClickedEvent e );
}
