package com.ftloverdrive.model.system;

import com.ftloverdrive.util.OVDConstants;


public class ShieldSystemModel extends AbstractSystemModel {

	public ShieldSystemModel() {
		super();
		systemProperties.setInt( OVDConstants.HULL_MAX, 100 );
		systemProperties.setInt( OVDConstants.HULL, 100 );
		systemProperties.setInt( OVDConstants.POWER_MAX, 8 );
		systemProperties.setInt( OVDConstants.POWER, 0 );
	}

	@Override
	public String getIconName() {
		return "s-shields";
	}

	@Override
	public int getPowerIncrement( int n ) {
		return 2;
	}

	@Override
	public boolean isSelfPowered() {
		return false;
	}
}
