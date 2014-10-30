package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.model.OVDModel;

public interface DoorModel extends OVDModel {

	/**
	 * current hp, level, and the like?
	 */
	public NamedProperties getProperties();

	public void setAnimSpec( ImageSpec spec );
	public ImageSpec getAnimSpec();
}
