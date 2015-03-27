package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.incident.IncidentBlueprint;
import com.ftloverdrive.blueprint.incident.PlotBranchBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.incident.BranchCreationEvent;
import com.ftloverdrive.event.incident.ConsequenceDamageCreationEvent;
import com.ftloverdrive.event.incident.ConsequenceResourceCreationEvent;
import com.ftloverdrive.event.incident.IncidentAddBranchEvent;
import com.ftloverdrive.event.incident.IncidentAddConsequenceEvent;
import com.ftloverdrive.event.incident.IncidentCreationEvent;
import com.ftloverdrive.event.incident.IncidentSelectionEvent;
import com.ftloverdrive.event.incident.IncidentTriggerEvent;
import com.ftloverdrive.model.incident.Consequence;
import com.ftloverdrive.model.incident.ConsequenceDamage;
import com.ftloverdrive.model.incident.ConsequenceResource;
import com.ftloverdrive.model.incident.DefaultPlotBranch;
import com.ftloverdrive.model.incident.DeferredIncidentModel;
import com.ftloverdrive.model.incident.IncidentModel;
import com.ftloverdrive.model.incident.PlotBranch;


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

				ConsequenceDamageCreationEvent.class,
				ConsequenceResourceCreationEvent.class
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
			IncidentModel incModel = new DeferredIncidentModel( incUID );
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
			BranchCreationEvent ev = (BranchCreationEvent)e;

			int bRefId = ev.getBranchRefId();
			PlotBranch branch = new DefaultPlotBranch( ev.getIncidentRefId(), ev.getChoiceText(),
					ev.isSpoilerVisible(), ev.getRequirements() );
			context.getReferenceManager().addObject( branch, bRefId );

			int incRefId = ev.getIncidentRefId();
			if ( incRefId != -1 ) {
				IncidentModel incModel = context.getReferenceManager().getObject( incRefId, IncidentModel.class );
				for ( int cseqRefId : incModel.consequenceRefIds() ) {
					Consequence conseq = context.getReferenceManager().getObject( cseqRefId, Consequence.class );
					conseq.addRequirements( branch );
				}
			}
		}

		// TODO: each type of consequence has a separate creation event -- ugly.
		// Think of a better way of doing this (tricky / need more info)

		// Could just use the data directly from the incident blueprint, look at it
		// and retrieve a list of consequence blueprints that have been added to it
		// and use them as though they were models. But this assumes both clients
		// have the exact same set of blueprints
		else if ( e instanceof ConsequenceDamageCreationEvent ) {
			ConsequenceDamageCreationEvent ev = (ConsequenceDamageCreationEvent)e;

			int cRefId = ev.getConsequenceRefId();
			ConsequenceDamage consequence = new ConsequenceDamage( ev.getDamage() );
			context.getReferenceManager().addObject( consequence, cRefId );
		}
		else if ( e instanceof ConsequenceResourceCreationEvent ) {
			ConsequenceResourceCreationEvent ev = (ConsequenceResourceCreationEvent)e;

			int cRefId = ev.getConsequenceRefId();
			ConsequenceResource consequence = new ConsequenceResource( ev.getResource(), ev.getAmount(), ev.getRequires() );
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

		else if ( e.getClass() == ConsequenceDamageCreationEvent.class ) {
			Pools.get( ConsequenceDamageCreationEvent.class ).free( (ConsequenceDamageCreationEvent)e );
		}
		else if ( e.getClass() == ConsequenceResourceCreationEvent.class ) {
			Pools.get( ConsequenceResourceCreationEvent.class ).free( (ConsequenceResourceCreationEvent)e );
		}
	}
}
