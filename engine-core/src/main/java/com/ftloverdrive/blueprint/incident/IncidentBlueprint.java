package com.ftloverdrive.blueprint.incident;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.blueprint.AbstractOVDBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.incident.IncidentAddConsequenceEvent;
import com.ftloverdrive.event.incident.IncidentCreationEvent;
import com.ftloverdrive.model.GameModel;


public class IncidentBlueprint extends AbstractOVDBlueprint {

	private String uId = null;
	// TODO: Later this will become a ContextAwareTemplate or something.
	private String text = null;
	private TargetPlayers target = TargetPlayers.PLAYER;
	private List<ConsequenceBlueprint> consequences = new ArrayList<ConsequenceBlueprint>();
	private List<PlotBranchBlueprint> branches = new ArrayList<PlotBranchBlueprint>();


	public IncidentBlueprint() {
		// Incidents cannot be extended
		super( null );
	}

	public IncidentBlueprint( String uId ) {
		// Incidents cannot be extended
		super( null );
		setUniqueId( uId );
	}

	public void setUniqueId( String uId ) {
		this.uId = uId;
	}

	public void setTargetPlayer( TargetPlayers target ) {
		this.target = target;
	}

	public void setTextTemplate( String text ) {
		this.text = text;
	}

	public void addConsequence( ConsequenceBlueprint consequence ) {
		consequences.add( consequence );
	}

	public ConsequenceBlueprint[] getConsequences() {
		return consequences.toArray( new ConsequenceBlueprint[0] );
	}

	public void addPlotBranch( PlotBranchBlueprint branch ) {
		branches.add( branch );
	}

	public PlotBranchBlueprint[] getPlotBranches() {
		return branches.toArray( new PlotBranchBlueprint[0] );
	}

	@Override
	public int construct( OverdriveContext context ) {
		int incRefId = context.getNetManager().requestNewRefId();

		int targetRefId = -1;
		if ( target == TargetPlayers.PLAYER )
			targetRefId = context.getNetManager().getLocalPlayerRefId();
		else if ( target == TargetPlayers.ENEMY )
			targetRefId = context.getNetManager().getEnemyPlayerRefId();

		if ( targetRefId == -1 ) {
			// TODO: Actual error handling
			throw new IllegalArgumentException( "TargetRefId is -1" );
		}

		IncidentCreationEvent createE = Pools.get( IncidentCreationEvent.class ).obtain();
		createE.init( incRefId, targetRefId, uId, text );
		context.getScreenEventManager().postDelayedEvent( createE );

		for ( ConsequenceBlueprint cseq : consequences ) {
			int cseqRefId = cseq.construct( context );

			IncidentAddConsequenceEvent cseqE = Pools.get( IncidentAddConsequenceEvent.class ).obtain();
			cseqE.init( incRefId, cseqRefId );
			context.getScreenEventManager().postDelayedEvent( cseqE );
		}

		return incRefId;
	}
}
