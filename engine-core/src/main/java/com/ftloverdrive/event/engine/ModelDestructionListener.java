package com.ftloverdrive.event.engine;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface ModelDestructionListener extends OVDEventListener {

	/**
	 * A model has been destroyed, and its refId removed from ReferenceManager.
	 */
	public void modelDestroyed( OverdriveContext context, ModelDestructionEvent e );
}
