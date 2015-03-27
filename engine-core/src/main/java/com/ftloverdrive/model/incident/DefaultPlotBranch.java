package com.ftloverdrive.model.incident;

import com.badlogic.gdx.utils.Array;
import com.ftloverdrive.model.AbstractOVDModel;


public class DefaultPlotBranch extends AbstractOVDModel implements PlotBranch {

	private static final PlotBranchRequirement[] nullReqs = new PlotBranchRequirement[0];

	protected int incRefId = -1;
	protected String choiceText = null;
	protected boolean spoilerVisible = true;
	protected Array<PlotBranchRequirement> requirements = null;


	public DefaultPlotBranch() {
		choiceText = "Continue...";
	}

	public DefaultPlotBranch( int incidentRefId, String text ) {
		incRefId = incidentRefId;
		choiceText = text;
	}

	public DefaultPlotBranch( int incidentRefId, String text, boolean showSpoiler ) {
		this( incidentRefId, text );
		spoilerVisible = showSpoiler;
	}

	public DefaultPlotBranch( int incidentRefId, String text, PlotBranchRequirement... reqs ) {
		this( incidentRefId, text );
		requirements = new Array<PlotBranchRequirement>( reqs == null ? nullReqs : reqs );
	}

	public DefaultPlotBranch( int incidentRefId, String text, boolean showSpoiler, PlotBranchRequirement... reqs ) {
		this( incidentRefId, text, showSpoiler );
		requirements = new Array<PlotBranchRequirement>( reqs == null ? nullReqs : reqs );
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

	@Override
	public PlotBranchRequirement[] getRequirements() {
		return requirements == null ? nullReqs : requirements.toArray();
	}

	@Override
	public void addRequirement( PlotBranchRequirement req ) {
		if ( req == null )
			throw new IllegalArgumentException( "Argument is null" );
		requirements.add( req );
	}
}
