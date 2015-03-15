package com.ftloverdrive.model.ship;

import com.ftloverdrive.io.AnimSpec;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.model.OVDModel;


public interface CrewModel extends OVDModel, Ambulator {

	public NamedProperties getProperties();

	public AnimSpec getIdleAnimSpec();

	public AnimSpec getWalkFrontAnimSpec();

	public AnimSpec getWalkBackAnimSpec();

	public AnimSpec getWalkLeftAnimSpec();

	public AnimSpec getWalkRightAnimSpec();
}
