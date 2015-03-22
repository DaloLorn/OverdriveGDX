package com.ftloverdrive.event.incident;

import com.ftloverdrive.event.AbstractOVDEvent;


public class BranchCreationEvent extends AbstractOVDEvent {

	protected int bRefId = -1;
	protected int incRefId = -1;
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
	public void init( int branchRefId, int incidentRefId, String choiceText, boolean spoilerVisible ) {
		bRefId = branchRefId;
		incRefId = incidentRefId;
		text = choiceText;
		spoiler = spoilerVisible;
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

	@Override
	public void reset() {
		bRefId = -1;
	}
}
