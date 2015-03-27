package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;


public interface PlotBranchCriteria {

	public CriteriaResult classify( OverdriveContext context, PlotBranch branch );


	public static enum CriteriaResult {
		NORMAL, BLUE,
		// In FTL:
		// - a blue choice whose requirements are not fulfilled is hidden entirely, even with hidden="false"
		// - a normal choice that has an <item_modify> tag (and maybe others?) will be grayed out if the
		//   player doesn't have enough resources, but will remain visible
		// Hence this distinction.
		VISIBLE_UNAVAILABLE, INVISIBLE_UNAVAILABLE
	}
}
