package com.ftloverdrive.model.incident;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.engine.ModelDestructionEvent;
import com.ftloverdrive.model.AbstractOVDModel;
import com.ftloverdrive.ui.incident.IncidentDialog;
import com.ftloverdrive.ui.screen.BaseScreen;


/**
 * IncidentModel that gets constructed as it is needed.
 */
public class DeferredIncidentModel extends AbstractOVDModel implements IncidentModel {

	private Array<Integer> consequenceRefIds = new Array<Integer>( true, 2 );
	private Array<Integer> branchRefIds = new Array<Integer>( true, 3 );

	private String incId = null;
	private String incText = null;
	private int targetRefId = -1;


	protected DeferredIncidentModel() {
	}

	public DeferredIncidentModel( String incidentId, int playerRefId ) {
		incId = incidentId;
		targetRefId = playerRefId;
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
		if ( targetRefId == context.getNetManager().getLocalPlayerRefId() ) {
			for ( int cseqRefId : consequenceRefIds ) {
				Consequence consequence = context.getReferenceManager().getObject( cseqRefId, Consequence.class );
				consequence.execute( context );
			}
		}

		final Stage popupStage = context.getScreen().getStageManager().getStage( BaseScreen.POPUP_STAGE_ID );

		// If there already is an IncidentDialog present in the stage, use that.
		// Otherwise create a new one.
		IncidentDialog dialog = popupStage.getRoot().findActor( IncidentDialog.ACTOR_NAME );
		if ( dialog == null )
			dialog = new IncidentDialog( context );

		if ( targetRefId == context.getNetManager().getLocalPlayerRefId() ) {
			dialog.setCurrentIncident( this );
		}
		else {
			dialog.setCurrentIncident( null );
		}

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

	private Array<Integer> dispose( OverdriveContext context, Array<Integer> ids ) {
		ids.add( context.getReferenceManager().getId( this ) );

		for ( int consequenceRefId : consequenceRefIds )
			ids.add( consequenceRefId );

		for ( int branchRefId : branchRefIds )
			ids.add( branchRefId );

		consequenceRefIds.clear();
		branchRefIds.clear();
		return ids;
	}

	private Array<Integer> disposeRecursiveExcept( OverdriveContext context, int ignore, Array<Integer> ids ) {
		for ( int branchRefId : branchRefIds ) {
			PlotBranch branch = context.getReferenceManager().getObject( branchRefId, PlotBranch.class );
			int incRefId = branch.getIncidentRefId();
			if ( incRefId != ignore && incRefId != -1 ) {
				DeferredIncidentModel inc = context.getReferenceManager().getObject( incRefId, DeferredIncidentModel.class );
				inc.disposeRecursiveExcept( context, ignore, ids );
			}
		}
		return dispose( context, ids );
	}

	/**
	 * Disposes this incident and its own consequences and branches, as well as incidents referenced by branches,
	 * except if the referenced incident's id matches the one passed in argument.
	 * 
	 * @param ignoredRefId
	 *            refId of the child incident that's not to be disposed
	 */
	public void disposeExcept( OverdriveContext context, int ignoredRefId ) {
		Array<Integer> ids = new Array<Integer>();
		disposeRecursiveExcept( context, ignoredRefId, ids );

		int[] refIds = new int[ids.size];
		for ( int i = 0; i < ids.size; ++i )
			refIds[i] = ids.get( i );

		ModelDestructionEvent destroyE = Pools.get( ModelDestructionEvent.class ).obtain();
		destroyE.init( refIds );
		context.getScreenEventManager().postDelayedEvent( destroyE );
	}

	/**
	 * Disposes this incident and its own consequences and branches, as well as incidents referenced by branches.
	 */
	public void dispose( OverdriveContext context ) {
		disposeExcept( context, -1 );
	}
}
