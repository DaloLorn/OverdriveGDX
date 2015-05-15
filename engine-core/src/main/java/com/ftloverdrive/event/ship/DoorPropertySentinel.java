package com.ftloverdrive.event.ship;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface DoorPropertySentinel extends OVDEventListener {

	public void doorPropertyChanging( OverdriveContext context, DoorPropertyEvent e );
}
