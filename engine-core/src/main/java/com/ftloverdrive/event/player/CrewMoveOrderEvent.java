package com.ftloverdrive.event.player;

import com.ftloverdrive.model.ship.ShipCoordinate;


public class CrewMoveOrderEvent extends AbstractOrderEvent {

	private ShipCoordinate targetCoord = null;


	public CrewMoveOrderEvent() {
	}

	public ShipCoordinate getTargetCoordinate() {
		return targetCoord;
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param playerRefId
	 *            playerId of the player who issued the order
	 * @param targetRefIds
	 *            Ids of models who should act upon this order
	 * @param targetCoord
	 *            ShipCoordinate to which the currently selected crew should move
	 */
	public void init( int playerRefId, int[] targetRefIds, ShipCoordinate targetCoord ) {
		super.init( playerRefId, targetRefIds );
		this.targetCoord = targetCoord;
	}

	public void reset() {
		super.reset();
		targetCoord = null;
	}
}
