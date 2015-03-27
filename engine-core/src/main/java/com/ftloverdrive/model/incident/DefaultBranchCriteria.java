package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;


public class DefaultBranchCriteria implements PlotBranchCriteria {

	@Override
	public CriteriaResult classify( OverdriveContext context, PlotBranch branch ) {
		CriteriaResult result = CriteriaResult.NORMAL;

		for ( PlotBranchRequirement req : branch.getRequirements() ) {
			CriteriaResult t = req.evaluate( context );
			if ( result.compareTo( t ) < 0 )
				result = t;
		}

		return result;
	}
}
