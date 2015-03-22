package com.ftloverdrive.event.incident;

import com.ftloverdrive.event.AbstractOVDEvent;


public class IncidentSelectionEvent extends AbstractOVDEvent {

	protected int incidentRefId = -1;


	public IncidentSelectionEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param incRefId
	 *            a reserved reference id for the triggered incident
	 */
	public void init( int incRefId ) {
		incidentRefId = incRefId;
	}

	public int getIncidentRefId() {
		return incidentRefId;
	}

	@Override
	public void reset() {
		incidentRefId = -1;
	}
}
