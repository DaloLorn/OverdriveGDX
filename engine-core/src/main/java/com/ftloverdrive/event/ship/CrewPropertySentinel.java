package com.ftloverdrive.event.ship;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface CrewPropertySentinel extends OVDEventListener {

	public void crewPropertyChanging( OverdriveContext context, CrewPropertyEvent e );
}
