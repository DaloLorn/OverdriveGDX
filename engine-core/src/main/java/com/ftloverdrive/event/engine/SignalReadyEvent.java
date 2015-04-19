package com.ftloverdrive.event.engine;

import com.ftloverdrive.event.QueryEvent;


/**
 * Signal the server that this client is ready to proceed.
 */
public class SignalReadyEvent extends QueryEvent {

	private boolean abort = false;


	public void setAbort( boolean abort ) {
		this.abort = abort;
	}

	/**
	 * If true, proceed normally. If false, means that an error has ocurred
	 * and client should go back. (eg. the other client timed out while we were waiting)
	 */
	public boolean isAborted() {
		return abort;
	}
}
