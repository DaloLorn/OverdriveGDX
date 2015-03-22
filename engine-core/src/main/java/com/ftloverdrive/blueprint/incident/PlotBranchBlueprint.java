package com.ftloverdrive.blueprint.incident;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.AbstractOVDBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.incident.BranchCreationEvent;


public class PlotBranchBlueprint extends AbstractOVDBlueprint {

	private String incidentId = null;
	private String choiceText = null;
	private boolean spoilerVisible = true;


	/**
	 * Default constructor creates a "Continue..." branch.
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

	@Override
	public int construct( OverdriveContext context ) {
		int branchRefId = context.getNetManager().requestNewRefId();

		int incRefId = -1;

		// Default "Continue..." branches have no incidentId
		if ( incidentId != null )
			incRefId = context.getBlueprintManager().getBlueprint( incidentId ).construct( context );

		BranchCreationEvent createE = Pools.get( BranchCreationEvent.class ).obtain();
		createE.init( branchRefId, incRefId, choiceText, spoilerVisible );
		context.getScreenEventManager().postDelayedEvent( createE );

		return branchRefId;
	}
}
