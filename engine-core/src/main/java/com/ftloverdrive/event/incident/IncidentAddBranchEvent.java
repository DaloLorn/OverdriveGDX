package com.ftloverdrive.event.incident;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class IncidentAddBranchEvent extends AbstractOVDEvent implements Poolable {

	protected int incidentRefId = -1;
	protected int branchRefId = -1;


	public IncidentAddBranchEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param incRefId
	 *            ref id of the target incident
	 * @param bRefId
	 *            ref id of the target plot branch
	 */
	public void init( int incRefId, int bRefId ) {
		incidentRefId = incRefId;
		branchRefId = bRefId;
	}

	public int getIncidentRefId() {
		return incidentRefId;
	}

	public int getBranchRefId() {
		return branchRefId;
	}

	@Override
	public void reset() {
		super.reset();
		incidentRefId = -1;
		branchRefId = -1;
	}
}
