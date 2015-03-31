package com.ftloverdrive.model.incident;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.engine.DisposeListener;
import com.ftloverdrive.event.engine.ModelDestructionEvent;
import com.ftloverdrive.model.AbstractOVDModel;
import com.ftloverdrive.ui.incident.IncidentDialog;
import com.ftloverdrive.ui.screen.BaseScreen;


/**
 * What the original game referred to as an event.
 */
public class DeferredIncidentModel extends AbstractOVDModel implements IncidentModel, DisposeListener {

	private Array<Integer> consequenceRefIds = new Array<Integer>( true, 2 );
	private Array<Integer> branchRefIds = new Array<Integer>( true, 3 );

	private String incId = null;
	private String incText = null;


	protected DeferredIncidentModel() {
	}

	public DeferredIncidentModel( String incidentId ) {
		incId = incidentId;
	}

	@Override
	public String getIncidentId() {
		return incId;
	}

	@Override
	public String getText() {
		return incText;
	}

	@Override
	public void setText( String text ) {
		incText = text;
	}

	@Override
	public void execute( OverdriveContext context ) {
		for ( int cseqRefId : consequenceRefIds ) {
			Consequence consequence = context.getReferenceManager().getObject( cseqRefId, Consequence.class );
			consequence.execute( context );
		}

		final Stage popupStage = context.getScreen().getStageManager().getStage( BaseScreen.POPUP_STAGE_ID );

		// If there already is an IncidentDialog present in the stage, use that.
		// Otherwise create a new one.
		IncidentDialog dialog = popupStage.getRoot().findActor( IncidentDialog.ACTOR_NAME );
		if ( dialog == null )
			dialog = new IncidentDialog( context );

		dialog.setCaptureInput( true );
		dialog.addDisposeListener( this );
		dialog.setIncidentText( getText() );

		if ( consequenceRefIds.size == 0 ) {
			dialog.showConseequenceBox( false );
		}
		else {
			dialog.showConseequenceBox( true );
			for ( int conseqRefId : consequenceRefIds ) {
				Consequence conseq = context.getReferenceManager().getObject( conseqRefId, Consequence.class );
				dialog.addConsequence( conseq );
			}
		}

		for ( int branchRefId : branchRefIds ) {
			PlotBranch branch = context.getReferenceManager().getObject( branchRefId, PlotBranch.class );
			dialog.addChoice( branch );
		}
		// If the incident defines no branches, or none of the added branches are available / visible, then
		// add the default "Continue..." choice.
		if ( dialog.countAvailableChoices() == 0 )
			dialog.addChoice( new DefaultPlotBranch() );

		// Height depends on width and number of plot branches, so calculate it after everything's been added
		dialog.setHeight( dialog.computePreferredHeight() );

		if ( !popupStage.getActors().contains( dialog, true ) ) {
			// setKeepWithinStage() only applies if added to the stage root. :/
			popupStage.addActor( dialog );
			popupStage.addListener( dialog );
		}

		// Position depends on size and stage, so set it last
		dialog.usePreferredPosition();
	}

	@Override
	public int[] consequenceRefIds() {
		int[] result = new int[consequenceRefIds.size];
		for ( int i = 0; i < result.length; ++i )
			result[i] = consequenceRefIds.get( i );
		return result;
	}

	@Override
	public int[] branchRefIds() {
		int[] result = new int[branchRefIds.size];
		for ( int i = 0; i < result.length; ++i )
			result[i] = branchRefIds.get( i );
		return result;
	}

	@Override
	public void addConsequence( int conseqRefId ) {
		consequenceRefIds.add( conseqRefId );
	}

	@Override
	public void addPlotBranch( int branchRefId ) {
		branchRefIds.add( branchRefId );
	}

	@Override
	public void objectDisposed( OverdriveContext context, Object o ) {
		ModelDestructionEvent destroyE = Pools.get( ModelDestructionEvent.class ).obtain();
		destroyE.init( context.getReferenceManager().getId( this ) );
		context.getScreenEventManager().postDelayedEvent( destroyE );

		for ( int consequenceRefId : consequenceRefIds ) {
			destroyE = Pools.get( ModelDestructionEvent.class ).obtain();
			destroyE.init( consequenceRefId );
			context.getScreenEventManager().postDelayedEvent( destroyE );
		}

		for ( int branchRefId : branchRefIds ) {
			destroyE = Pools.get( ModelDestructionEvent.class ).obtain();
			destroyE.init( branchRefId );
			context.getScreenEventManager().postDelayedEvent( destroyE );
		}

		consequenceRefIds.clear();
		branchRefIds.clear();
	}
}
