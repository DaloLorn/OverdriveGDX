package com.ftloverdrive.event.player;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface OrderListener extends OVDEventListener {

	public void orderIssued( OverdriveContext context, AbstractOrderEvent e );
}
