package com.ftloverdrive.model.incident;


/**
 * What the original game referred to as an event choice.
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

	/**
	 * Adds a new requirement to this branch. Needed mostly for Consequences that can optionally
	 * add requirements, eg. negative ResourceConsequence
	 */
	public void addRequirement( PlotBranchRequirement req );
}
