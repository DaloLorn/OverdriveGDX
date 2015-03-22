package com.ftloverdrive.model.incident;

import com.ftloverdrive.model.AbstractOVDModel;


public class PlotBranchModel extends AbstractOVDModel implements PlotBranch {

	private int incRefId = -1;
	private String choiceText = null;
	private boolean spoilerVisible = true;


	public PlotBranchModel() {
		setText( "Continue..." );
	}

	public PlotBranchModel( int incidentRefId ) {
		setIncidentRefId( incidentRefId );
	}

	public PlotBranchModel( int incidentRefId, String text ) {
		setIncidentRefId( incidentRefId );
		setText( text );
	}

	public void setText( String text ) {
		choiceText = text;
	}

	@Override
	public String getText() {
		return choiceText;
	}

	@Override
	public void setSpoilerVisible( boolean b ) {
		spoilerVisible = b;
	}

	@Override
	public boolean isSpoilerVisible() {
		return spoilerVisible;
	}

	public void setIncidentRefId( int incidentRefId ) {
		incRefId = incidentRefId;
	}

	@Override
	public int getIncidentRefId() {
		return incRefId;
	}
}
