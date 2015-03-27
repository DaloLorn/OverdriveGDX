package com.ftloverdrive.model.incident;


/**
 * What the original game referred to as an event choice.
 *
 * TODO: A PlotBranchCriteria class to decide whether this branch is a
 * normal choice, a blue option, or unavailable (if the Overdrive context
 * doesn't meet arbitrary reqs).
 */
public interface PlotBranch {

	/** Returns the clickable text for this branch. */
	public String getText();

	/** Returns true if this branch *wants* its spoiler to be shown, false otherwise. */
	public boolean isSpoilerVisible();

	/** Returns ref id of the Incident to trigger if this branch is chosen, or null. */
	public int getIncidentRefId();

	/** Returns the requirements that have to be satisfied in order to make this branch clickable */
	public PlotBranchRequirement[] getRequirements();

	public void addRequirement( PlotBranchRequirement req );
}
