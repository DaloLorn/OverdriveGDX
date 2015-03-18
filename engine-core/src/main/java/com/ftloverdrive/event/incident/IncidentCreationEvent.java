package com.ftloverdrive.event.incident;

import com.ftloverdrive.event.AbstractOVDEvent;


public class IncidentCreationEvent extends AbstractOVDEvent {

	protected int incRefId = -1;
	protected String incUniqueId = null;
	protected String incText = null;


	public IncidentCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param incidentRefId
	 *            a reserved reference id for the new incident
	 * @param incidentUniqueId
	 *            unique identifier of the new incident
	 * @param incidentText
	 *            text to be displayed
	 */
	public void init( int incidentRefId, String incidentUniqueId, String incidentText ) {
		incRefId = incidentRefId;
		incUniqueId = incidentUniqueId;
		incText = incidentText;
	}

	public int getIncidentRefId() {
		return incRefId;
	}

	public String getIncidentUniqueId() {
		return incUniqueId;
	}

	public String getIncidentText() {
		return incText;
	}

	@Override
	public void reset() {
		incRefId = -1;
		incUniqueId = null;
		incText = null;
	}
}
