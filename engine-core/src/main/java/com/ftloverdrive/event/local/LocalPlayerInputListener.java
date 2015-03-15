package com.ftloverdrive.event.local;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;

public interface LocalPlayerInputListener extends OVDEventListener {

	/**
	 * Local player has issued an input event (keyboard, mouse or touch)
	 */
	public void handleInput( OverdriveContext context, LocalPlayerInputEvent e );
}
