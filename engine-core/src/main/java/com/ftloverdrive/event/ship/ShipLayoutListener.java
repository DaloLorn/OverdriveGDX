package com.ftloverdrive.event.ship;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface ShipLayoutListener extends OVDEventListener {

	public void shipLayoutRoomAdded( OverdriveContext context, ShipLayoutRoomAddEvent e );

	public void shipLayoutDoorAdded( OverdriveContext context, ShipLayoutDoorAddEvent e );
	
	public void shipLayoutTeleportPadAdded(OverdriveContext context, ShipLayoutTeleportPadAddEvent e );
}
