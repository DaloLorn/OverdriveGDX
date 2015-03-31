package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;


/**
 * Default implementation returns the most restrictive classification found
 * in the branch's requirements.
 * 
 * Eg. if all requirements return NORMAL, but one returns BLUE, then the branch
 * will be classified as BLUE.
 */
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
