package com.ftloverdrive.model.incident;

import com.ftloverdrive.model.AbstractOVDModel;


public class PlotBranchModel extends AbstractOVDModel implements PlotBranch {

	private int incRefId = -1;
	private String choiceText = null;
	private boolean spoilerVisible = true;


	public PlotBranchModel() {
		choiceText = "Continue...";
	}

	public PlotBranchModel( int incidentRefId, String text, boolean showSpoiler ) {
		incRefId = incidentRefId;
		choiceText = text;
		spoilerVisible = showSpoiler;
	}

	@Override
	public String getText() {
		return choiceText;
	}

	@Override
	public boolean isSpoilerVisible() {
		return spoilerVisible;
	}

	@Override
	public int getIncidentRefId() {
		return incRefId;
	}
}
