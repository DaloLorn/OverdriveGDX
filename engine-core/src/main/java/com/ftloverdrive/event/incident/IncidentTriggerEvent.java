package com.ftloverdrive.event.incident;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class IncidentTriggerEvent extends AbstractOVDEvent implements Poolable {

	// TODO: add playerRefId to indicate to which player the incident pertains?
	protected int incidentRefId = -1;


	public IncidentTriggerEvent() {
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
		super.reset();
		incidentRefId = -1;
	}
}
