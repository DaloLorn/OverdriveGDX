package com.ftloverdrive.event.incident;

import com.ftloverdrive.event.AbstractOVDEvent;
import com.ftloverdrive.model.incident.PlotBranchRequirement;


public class BranchCreationEvent extends AbstractOVDEvent {

	protected int bRefId = -1;
	protected int incRefId = -1;
	protected String text = null;
	protected boolean spoiler = true;
	protected PlotBranchRequirement[] reqs = null;


	public BranchCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param branchRefId
	 *            a reserved reference id for the new branch
	 */
	public void init( int branchRefId, int incidentRefId, String choiceText, boolean spoilerVisible, PlotBranchRequirement[] reqs ) {
		bRefId = branchRefId;
		incRefId = incidentRefId;
		text = choiceText;
		spoiler = spoilerVisible;
		this.reqs = reqs;
	}

	public int getBranchRefId() {
		return bRefId;
	}

	public int getIncidentRefId() {
		return incRefId;
	}

	public String getChoiceText() {
		return text;
	}

	public boolean isSpoilerVisible() {
		return spoiler;
	}

	public PlotBranchRequirement[] getRequirements() {
		return reqs;
	}

	@Override
	public void reset() {
		bRefId = -1;
	}
}
