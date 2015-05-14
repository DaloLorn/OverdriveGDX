package com.ftloverdrive.model.system;

import com.ftloverdrive.util.OVDConstants;


public class EngineSystemModel extends AbstractSystemModel {

	public EngineSystemModel() {
		super();
		systemProperties.setInt( OVDConstants.HEALTH_MAX, 100 );
		systemProperties.setInt( OVDConstants.HEALTH, 100 );
		systemProperties.setInt( OVDConstants.POWER_MAX, 8 );
		systemProperties.setInt( OVDConstants.POWER, 0 );
	}

	@Override
	public String getIconName() {
		return "s-engines";
	}

	@Override
	public int getPowerIncrement() {
		return 1;
	}

	@Override
	public boolean isSelfPowered() {
		return false;
	}
}
