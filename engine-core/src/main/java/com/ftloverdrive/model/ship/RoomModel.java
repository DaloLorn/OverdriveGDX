package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.model.OVDModel;


public interface RoomModel extends OVDModel {

	public NamedProperties getProperties();


	/**
	 * Sets the room's background decorations.
	 *
	 * Do not set this for non-rectangular rooms.
	 */
	public void setRoomDecorImageSpec( ImageSpec imageSpec );
	public ImageSpec getRoomDecorImageSpec();
}
