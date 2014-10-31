package com.ftloverdrive.event.ship;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface DoorPropertyListener extends OVDEventListener {

	public void doorPropertyChanged( OverdriveContext context, DoorPropertyEvent e );
}
