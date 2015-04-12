package com.ftloverdrive.blueprint.incident;

import com.ftloverdrive.blueprint.AbstractOVDBlueprint;


/**
 * Base abstract class for consequence blueprints.
 *
 */
public abstract class ConsequenceBlueprint extends AbstractOVDBlueprint {

	protected TargetPlayers target = TargetPlayers.PLAYER;


	public ConsequenceBlueprint() {
		super( null );
	}

	public void setTargetPlayer( TargetPlayers target ) {
		this.target = target;
	}
}
