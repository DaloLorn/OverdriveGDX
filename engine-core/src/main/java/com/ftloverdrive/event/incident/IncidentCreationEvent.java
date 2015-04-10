package com.ftloverdrive.event.incident;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class IncidentCreationEvent extends AbstractOVDEvent implements Poolable {

	protected int incRefId = -1;
	protected int targetRefId = -1;
	protected String incId = null;
	protected String incText = null;


	public IncidentCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param incidentRefId
	 *            a reserved reference id for the new incident
	 * @param playerRefId
	 *            to which player this event will be displayed
	 * @param incidentId
	 *            blueprint name of the IncidentBlueprint to use
	 * @param incidentText
	 *            text to be displayed
	 */
	public void init( int incidentRefId, int playerRefId, String incidentId, String incidentText ) {
		incRefId = incidentRefId;
		targetRefId = playerRefId;
		incId = incidentId;
		incText = incidentText;
	}

	public int getIncidentRefId() {
		return incRefId;
	}

	public int getTargetPlayerRefId() {
		return targetRefId;
	}

	public String getIncidentId() {
		return incId;
	}

	public String getIncidentText() {
		return incText;
	}

	@Override
	public void reset() {
		super.reset();
		incRefId = -1;
		incId = null;
		incText = null;
	}
}
