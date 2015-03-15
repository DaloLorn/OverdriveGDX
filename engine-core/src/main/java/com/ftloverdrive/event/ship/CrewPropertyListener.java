package com.ftloverdrive.event.ship;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface CrewPropertyListener extends OVDEventListener {

	public void crewPropertyChanged( OverdriveContext context, CrewPropertyEvent e );
}
