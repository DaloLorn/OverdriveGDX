package com.ftloverdrive.event.engine;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface DisposeListener extends OVDEventListener {

	public void objectDisposed( OverdriveContext context, Object o );
}
