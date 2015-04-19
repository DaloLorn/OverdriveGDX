package com.ftloverdrive.event.engine;

import com.ftloverdrive.event.QueryEvent;
import com.ftloverdrive.model.DefaultGameModel;


/**
 * This sends the gameModel over the network, assuming that the remote
 * client connects late. Not very smart, but gotta sync the clients somehow.
 * 
 * TODO: Improve this.
 */
public class RequestGameStateEvent extends QueryEvent {

	private int gameModelRefId = -1;
	private DefaultGameModel gameModel = null;


	public void setGameModelRefId( int refId ) {
		gameModelRefId = refId;
	}

	public int getGameModelRefId() {
		return gameModelRefId;
	}

	public void setGameModel( DefaultGameModel model ) {
		gameModel = model;
	}

	public DefaultGameModel getGameModel() {
		return gameModel;
	}
}
