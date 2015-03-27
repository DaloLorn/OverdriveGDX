package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.incident.PlotBranchCriteria.CriteriaResult;


public interface PlotBranchRequirement {

	// TODO: Pass some models that events commonly check, eg ShipModel, PlayerModel, GameModel...?
	public CriteriaResult evaluate( OverdriveContext context );

}
