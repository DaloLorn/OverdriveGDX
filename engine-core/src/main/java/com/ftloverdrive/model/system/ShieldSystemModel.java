package com.ftloverdrive.model.system;

import com.ftloverdrive.util.OVDConstants;


public class ShieldSystemModel extends AbstractSystemModel {

	public ShieldSystemModel() {
		super();
		systemProperties.setInt( OVDConstants.HEALTH_MAX, 100 );
		systemProperties.setInt( OVDConstants.HEALTH, 100 );
		systemProperties.setInt( OVDConstants.POWER_MAX, 8 );
		systemProperties.setInt( OVDConstants.POWER, 4 );
	}

	@Override
	public String getIconName() {
		return "s-shields";
	}

	@Override
	public int getPowerIncrement() {
		return 2;
	}

	@Override
	public boolean isSelfPowered() {
		return false;
	}
}
