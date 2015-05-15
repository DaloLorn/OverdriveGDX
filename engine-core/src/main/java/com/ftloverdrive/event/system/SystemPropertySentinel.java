package com.ftloverdrive.event.system;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;


public interface SystemPropertySentinel extends OVDEventListener {

	public void systemPropertyChanging( OverdriveContext context, SystemPropertyEvent e );
}
