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
import com.ftloverdrive.ui.screen.OVDScreen;


/**
 * What the original game referred to as an event.
 */
public class DeferredIncidentModel extends AbstractOVDModel implements IncidentModel, DisposeListener {

	private Array<Integer> consequenceRefIds = new Array<Integer>( true, 2 );
	private Array<Integer> branchRefIds = new Array<Integer>( true, 3 );

	private String incId = null;
	private String incText = null;


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
		// TODO: Trigger or enqueue to be triggered after the incident chain is complete?
		// Or maybe "immediate" flag on consequences?
		for ( int cseqRefId : consequenceRefIds ) {
			Consequence consequence = context.getReferenceManager().getObject( cseqRefId, Consequence.class );
			consequence.execute( context );
		}

		OVDScreen screen = context.getScreen();
		final Stage popupStage = screen.getStageManager().getStage( BaseScreen.POPUP_STAGE_ID );

		IncidentDialog dialog = new IncidentDialog( context );
		dialog.setCaptureInput( true );
		dialog.setIncidentText( getText() );

		if ( branchRefIds.size == 0 ) {
			// If the incident defines no choices, add the default "Continue..." choice.
			dialog.addChoice( new PlotBranchModel() );
		}
		else {
			for ( int branchRefId : branchRefIds ) {
				PlotBranch branch = context.getReferenceManager().getObject( branchRefId, PlotBranch.class );
				dialog.addChoice( branch );
			}
		}

		dialog.addFiller();

		dialog.setSize( dialog.computePreferredWidth( 500 ), dialog.computePreferredHeight() );
		dialog.setPosition( ( screen.getScreenWidth() - dialog.getWidth() ) / 2, ( screen.getScreenHeight() - dialog.getHeight() ) / 2 );

		// setKeepWithinStage() only applies if added to the stage root. :/
		popupStage.addActor( dialog );
		popupStage.addListener( dialog );
		dialog.addDisposeListener( this );
	}

	@Override
	public Integer[] consequenceRefIds() {
		return consequenceRefIds.toArray();
	}

	@Override
	public Integer[] branchRefIds() {
		return branchRefIds.toArray();
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
