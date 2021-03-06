package com.ftloverdrive.event;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.core.OverdriveGame;


/**
 * A marker class to distinguish between query and non-query events.
 * 
 * Query events are used to ask the server for something, without
 * having the event needlessly redistributed to other connected clients,
 * for example when requesting a new range of refIds for the NetworkManager.
 */
public abstract class QueryEvent extends AbstractOVDEvent {

	protected int recipientId = -1;


	public void init( OverdriveContext c ) {
		recipientId = c.getGame().getConnectionId();
	}

	public void init( OverdriveGame g ) {
		recipientId = g.getConnectionId();
	}

	public int getConnectionId() {
		return recipientId;
	}
}
