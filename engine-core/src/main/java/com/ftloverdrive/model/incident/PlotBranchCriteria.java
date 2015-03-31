package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;


/**
 * Allows to classify PlotBranches as normal, blue, or unavailable.
 */
public interface PlotBranchCriteria {

	/**
	 * Evaluates all of the specified branch's requirements, and returns the final result.
	 */
	public CriteriaResult classify( OverdriveContext context, PlotBranch branch );


	public static enum CriteriaResult {
		NORMAL, BLUE, // TODO: Rename to SPECIAL ?
		// In FTL:
		// - a blue choice whose requirements are not fulfilled is hidden entirely, even with hidden="false"
		// - a normal choice that has an <item_modify> tag (and maybe others?) will be grayed out if the
		//   player doesn't have enough resources, but will remain visible
		// Hence this distinction.
		VISIBLE_UNAVAILABLE, INVISIBLE_UNAVAILABLE
	}
}
