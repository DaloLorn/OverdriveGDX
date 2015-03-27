package com.ftloverdrive.blueprint.incident;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.AbstractOVDBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.incident.BranchCreationEvent;
import com.ftloverdrive.model.incident.PlotBranchRequirement;


public class PlotBranchBlueprint extends AbstractOVDBlueprint {

	private String incidentId = null;
	private String choiceText = null;
	private boolean spoilerVisible = true;
	private Array<PlotBranchRequirement> requirements = new Array<PlotBranchRequirement>();


	/**
	 * Default constructor creates a "Continue..." branch that concludes the incident chain.
	 */
	public PlotBranchBlueprint() {
		// Plot branches cannot be extended
		super( null );
		choiceText = "Continue...";
	}

	public PlotBranchBlueprint( String incidentId, String choiceText ) {
		// Plot branches cannot be extended
		super( null );
		this.incidentId = incidentId;
		this.choiceText = choiceText;
	}

	public void setIncidenetId( String id ) {
		incidentId = id;
	}

	public void setChoiceText( String text ) {
		choiceText = text;
	}

	public void setSpoilerVisible( boolean spoil ) {
		spoilerVisible = spoil;
	}

	public void addRequirement( PlotBranchRequirement req ) {
		if ( !requirements.contains( req, true ) )
			requirements.add( req );
	}

	@Override
	public int construct( OverdriveContext context ) {
		int branchRefId = context.getNetManager().requestNewRefId();

		int incRefId = -1;

		// Default "Continue..." branches have no incidentId
		if ( incidentId != null )
			incRefId = context.getBlueprintManager().getBlueprint( incidentId ).construct( context );

		PlotBranchRequirement[] reqs = new PlotBranchRequirement[requirements.size];
		for ( int i = 0; i < reqs.length; ++i )
			reqs[i] = requirements.get( i );

		BranchCreationEvent createE = Pools.get( BranchCreationEvent.class ).obtain();
		createE.init( branchRefId, incRefId, choiceText, spoilerVisible, reqs );
		context.getScreenEventManager().postDelayedEvent( createE );

		return branchRefId;
	}
}
