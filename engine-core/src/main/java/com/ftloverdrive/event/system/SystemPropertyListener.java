package com.ftloverdrive.event.system;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface SystemPropertyListener extends OVDEventListener {

	public void systemPropertyChanged( OverdriveContext context, SystemPropertyEvent e );
}
