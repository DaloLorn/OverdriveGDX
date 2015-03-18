package com.ftloverdrive.event.incident;

import com.ftloverdrive.event.AbstractOVDEvent;


public class IncidentAddConsequenceEvent extends AbstractOVDEvent {

	protected int incidentRefId = -1;
	protected int consequenceRefId = -1;


	public IncidentAddConsequenceEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param incRefId
	 *            ref id of the target incident
	 * @param cRefId
	 *            ref id of the target plot branch
	 */
	public void init( int incRefId, int cRefId ) {
		incidentRefId = incRefId;
		consequenceRefId = cRefId;
	}

	public int getIncidentRefId() {
		return incidentRefId;
	}

	public int getConsequenceRefId() {
		return consequenceRefId;
	}

	@Override
	public void reset() {
		incidentRefId = -1;
		consequenceRefId = -1;
	}
}
