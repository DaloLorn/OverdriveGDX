package com.ftloverdrive.event.game;

import com.badlogic.gdx.utils.Pool.Poolable;

import com.ftloverdrive.event.AbstractOVDEvent;


public class GamePlayerShipChangeEvent extends AbstractOVDEvent implements Poolable {
	protected int gameRefId = -1;
	protected int shipRefId = -1;
	protected int playerRefId = -1;

	public GamePlayerShipChangeEvent() {
	}

	/**
	 * Pseudo-constructor.
	 *
	 * @param gameRefId  a reserved reference id for the GameModel
	 * @param playerRefId	a reserved reference id for the PlayerModel
	 * @param shipRefId  a reserved reference id for the ShipModel
	 */
	public void init( int gameRefId, int playerRefId, int shipRefId ) {
		this.gameRefId = gameRefId;
		this.playerRefId = playerRefId;
		this.shipRefId = shipRefId;
	}


	public int getGameRefId() {
		return gameRefId;
	}

	public int getShipRefId() {
		return shipRefId;
	}

	public int getPlayerRefId() {
		return playerRefId;
	}


	@Override
	public void reset() {
		super.reset();
		gameRefId = -1;
		shipRefId = -1;
	}
}
