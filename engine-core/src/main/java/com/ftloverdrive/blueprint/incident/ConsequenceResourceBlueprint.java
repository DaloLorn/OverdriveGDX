package com.ftloverdrive.blueprint.incident;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.incident.ConsequenceResourceCreationEvent;


public class ConsequenceResourceBlueprint extends ConsequenceBlueprint {

	private String resourceId;
	private int amountMin;
	private int amountMax;
	private boolean requires = true;


	public ConsequenceResourceBlueprint( String resId, int amountMin, int amountMax ) {
		this.resourceId = resId;
		if ( amountMin > amountMax ) {
			this.amountMin = amountMax;
			this.amountMax = amountMin;
		}
		else {
			this.amountMin = amountMin;
			this.amountMax = amountMax;
		}
	}

	public ConsequenceResourceBlueprint( String resId, int amountMin ) {
		this( resId, amountMin, amountMin );
	}

	/**
	 * 
	 * @param resId
	 *            Id of the affected resource
	 * @param amountMin
	 *            minimum amount (order doesn't matter)
	 * @param amountMax
	 *            maximum amount (order doesn't matter)
	 * @param req
	 *            if true, and the amount is negative, then this consequence will also
	 *            require the player to have the specified amount of resources in order
	 *            to be able to select this consequence's plot branch.
	 *            If false, or the amount is non-negative, no requirement is placed.
	 */
	public ConsequenceResourceBlueprint( String resId, int amountMin, int amountMax, boolean req ) {
		this( resId, amountMin, amountMax );
		requires = req;
	}

	public void setAmount( int min, int max ) {
		amountMin = min;
		amountMax = max;
	}

	public void setRequires( boolean req ) {
		requires = req;
	}

	@Override
	public int construct( OverdriveContext context ) {
		int consequenceRefId = context.getNetManager().requestNewRefId();

		int targetRefId = -1;
		if ( target == TargetPlayers.PLAYER )
			targetRefId = context.getNetManager().getLocalPlayerRefId();
		else if ( target == TargetPlayers.ENEMY )
			targetRefId = context.getNetManager().getEnemyPlayerRefId();

		if ( targetRefId == -1 ) {
			// TODO: Actual error handling
			throw new IllegalArgumentException( "TargetRefId is -1" );
		}

		int amount = amountMin + (int)Math.round( Math.random() * ( amountMax - amountMin ) );
		ConsequenceResourceCreationEvent createE = Pools.get( ConsequenceResourceCreationEvent.class ).obtain();
		createE.init( consequenceRefId, targetRefId, resourceId, amount, requires && amount < 0 );
		context.getScreenEventManager().postDelayedEvent( createE );

		return consequenceRefId;
	}
}
