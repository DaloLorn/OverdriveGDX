package com.ftloverdrive.event.ship;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;


public interface ShipPropertySentinel extends OVDEventListener {

	public void shipPropertyChanging( OverdriveContext context, ShipPropertyEvent e );
}
