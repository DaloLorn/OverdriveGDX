package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.incident.IncidentBlueprint;
import com.ftloverdrive.blueprint.incident.PlotBranchBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.incident.BranchCreationEvent;
import com.ftloverdrive.event.incident.DamageConsequenceCreationEvent;
import com.ftloverdrive.event.incident.IncidentAddBranchEvent;
import com.ftloverdrive.event.incident.IncidentAddConsequenceEvent;
import com.ftloverdrive.event.incident.IncidentCreationEvent;
import com.ftloverdrive.event.incident.IncidentSelectionEvent;
import com.ftloverdrive.event.incident.IncidentTriggerEvent;
import com.ftloverdrive.model.incident.DamageConsequence;
import com.ftloverdrive.model.incident.IncidentModel;
import com.ftloverdrive.model.incident.PlotBranch;
import com.ftloverdrive.model.incident.PlotBranchModel;


public class IncidentEventHandler implements OVDEventHandler {

	private Class[] eventClasses;
	private Class[] listenerClasses;


	public IncidentEventHandler() {
		eventClasses = new Class[] {
				IncidentCreationEvent.class,
				IncidentAddBranchEvent.class,
				IncidentAddConsequenceEvent.class,
				IncidentSelectionEvent.class,
				IncidentTriggerEvent.class,

				BranchCreationEvent.class,

				DamageConsequenceCreationEvent.class
		};
		listenerClasses = new Class[] {
				};
	}

	@Override
	public Class[] getEventClasses() {
		return eventClasses;
	}

	@Override
	public Class[] getListenerClasses() {
		return listenerClasses;
	}

	@Override
	public void handle( OverdriveContext context, OVDEvent e, Object[] listeners ) {
		if ( e instanceof IncidentCreationEvent ) {
			IncidentCreationEvent event = (IncidentCreationEvent)e;

			String incUID = event.getIncidentUniqueId();
			IncidentModel incModel = context.getBlueprintManager().createModel( incUID, incUID );
			incModel.setText( event.getIncidentText() );
			int incRefId = event.getIncidentRefId();
			context.getReferenceManager().addObject( incModel, incRefId );
		}
		else if ( e instanceof IncidentAddBranchEvent ) {
			IncidentAddBranchEvent event = (IncidentAddBranchEvent)e;

			int incRefId = event.getIncidentRefId();
			IncidentModel incModel = context.getReferenceManager().getObject( incRefId, IncidentModel.class );
			incModel.addPlotBranch( event.getBranchRefId() );
		}
		else if ( e instanceof IncidentAddConsequenceEvent ) {
			IncidentAddConsequenceEvent event = (IncidentAddConsequenceEvent)e;

			int incRefId = event.getIncidentRefId();
			IncidentModel incModel = context.getReferenceManager().getObject( incRefId, IncidentModel.class );
			incModel.addConsequence( event.getConsequenceRefId() );
		}
		else if ( e instanceof IncidentSelectionEvent ) {
			IncidentSelectionEvent event = (IncidentSelectionEvent)e;

			int incRefId = event.getIncidentRefId();
			IncidentModel incModel = context.getReferenceManager().getObject( incRefId, IncidentModel.class );
			IncidentBlueprint incBlueprint = (IncidentBlueprint)context.getBlueprintManager().getBlueprint( incModel.getIncidentId() );

			for ( PlotBranchBlueprint branchBlueprint : incBlueprint.getPlotBranches() ) {
				int branchRefId = branchBlueprint.construct( context );
				IncidentAddBranchEvent branchE = Pools.get( IncidentAddBranchEvent.class ).obtain();
				branchE.init( incRefId, branchRefId );
				context.getScreenEventManager().postDelayedEvent( branchE );
			}

			IncidentTriggerEvent triggerE = Pools.get( IncidentTriggerEvent.class ).obtain();
			triggerE.init( incRefId );
			context.getScreenEventManager().postDelayedEvent( triggerE );
		}
		else if ( e instanceof IncidentTriggerEvent ) {
			IncidentTriggerEvent event = (IncidentTriggerEvent)e;

			int incRefId = event.getIncidentRefId();
			IncidentModel incModel = context.getReferenceManager().getObject( incRefId, IncidentModel.class );
			incModel.execute( context );
		}

		else if ( e instanceof BranchCreationEvent ) {
			BranchCreationEvent event = (BranchCreationEvent)e;

			int bRefId = event.getBranchRefId();
			PlotBranch branch = new PlotBranchModel();
			branch.setIncidentRefId( event.getIncidentRefId() );
			branch.setText( event.getChoiceText() );
			branch.setSpoilerVisible( event.isSpoilerVisible() );
			context.getReferenceManager().addObject( branch, bRefId );
		}

		// TODO: each type of consequence has a separate creation event -- ugly.
		// Think of a better way of doing this (tricky / need more info)
		else if ( e instanceof DamageConsequenceCreationEvent ) {
			DamageConsequenceCreationEvent event = (DamageConsequenceCreationEvent)e;

			int cRefId = event.getConsequenceRefId();
			DamageConsequence consequence = new DamageConsequence( event.getDamage() );
			context.getReferenceManager().addObject( consequence, cRefId );
		}
	}

	@Override
	public void disposeEvent( OVDEvent e ) {
		if ( e.getClass() == IncidentCreationEvent.class ) {
			Pools.get( IncidentCreationEvent.class ).free( (IncidentCreationEvent)e );
		}
		else if ( e.getClass() == IncidentAddBranchEvent.class ) {
			Pools.get( IncidentAddBranchEvent.class ).free( (IncidentAddBranchEvent)e );
		}
		else if ( e.getClass() == IncidentAddConsequenceEvent.class ) {
			Pools.get( IncidentAddConsequenceEvent.class ).free( (IncidentAddConsequenceEvent)e );
		}
		else if ( e.getClass() == IncidentSelectionEvent.class ) {
			Pools.get( IncidentSelectionEvent.class ).free( (IncidentSelectionEvent)e );
		}
		else if ( e.getClass() == IncidentTriggerEvent.class ) {
			Pools.get( IncidentTriggerEvent.class ).free( (IncidentTriggerEvent)e );
		}

		else if ( e.getClass() == BranchCreationEvent.class ) {
			Pools.get( BranchCreationEvent.class ).free( (BranchCreationEvent)e );
		}

		else if ( e.getClass() == DamageConsequenceCreationEvent.class ) {
			Pools.get( DamageConsequenceCreationEvent.class ).free( (DamageConsequenceCreationEvent)e );
		}
	}
}
