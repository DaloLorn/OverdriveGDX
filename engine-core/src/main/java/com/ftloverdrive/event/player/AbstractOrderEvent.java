package com.ftloverdrive.event.player;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public abstract class AbstractOrderEvent extends AbstractOVDEvent implements Poolable {

	protected int playerRefId = -1;
	protected int[] targetRefIds = null;


	public int getPlayerRefId() {
		return playerRefId;
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param playerRefId
	 *            playerId of the player who issued the order
	 */
	public void init( int playerRefId, int[] targetRefIds ) {
		this.playerRefId = playerRefId;
		this.targetRefIds = targetRefIds;
	}

	public boolean pertainsTo( int refId ) {
		if ( targetRefIds == null ) {
			// TODO: error?
		}
		else {
			for ( int i = 0; i < targetRefIds.length; ++i ) {
				if ( targetRefIds[i] == refId )
					return true;
			}
		}
		return false;
	}

	public void reset() {
		super.reset();
		playerRefId = -1;
		targetRefIds = null;
	}
}
