package com.ftloverdrive.event.incident;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.QueryEvent;


// This event is basically a method execution that has to be delayed
// due to the way model creation in Overdrive works. However,
// if other clients were to receive this event, they'd enqueue model
// creation events of their own, essentially duplicating them.
// For this reason, this event should be a "query" event, so that it
// is only sent back to the client it originated from.
// This also has the nice property that it allows the server to
// scrutinize the selection event, possibly disallowing the selection.
public class IncidentSelectionEvent extends QueryEvent {

	protected int incidentRefId = -1;


	public IncidentSelectionEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param incRefId
	 *            a reserved reference id for the triggered incident
	 */
	public void init( OverdriveContext context, int incRefId ) {
		super.init( context );
		incidentRefId = incRefId;
	}

	public int getIncidentRefId() {
		return incidentRefId;
	}
}
