package com.ftloverdrive.event.incident;

import com.ftloverdrive.event.AbstractOVDEvent;


public class BranchCreationEvent extends AbstractOVDEvent {

	protected int bRefId = -1;
	protected String incId = null;
	protected String text = null;
	protected boolean spoiler = true;


	public BranchCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param branchRefId
	 *            a reserved reference id for the new branch
	 */
	public void init( int branchRefId, String incidentId, String choiceText, boolean spoilerVisible ) {
		bRefId = branchRefId;
		incId = incidentId;
		text = choiceText;
		spoiler = spoilerVisible;
	}

	public int getBranchRefId() {
		return bRefId;
	}

	public String getIncidentId() {
		return incId;
	}

	public String getChoiceText() {
		return text;
	}

	public boolean isSpoilerVisible() {
		return spoiler;
	}

	@Override
	public void reset() {
		bRefId = -1;
	}
}
