package com.ftloverdrive.event.incident;

import com.ftloverdrive.event.AbstractOVDEvent;


public class ConsequenceResourceCreationEvent extends AbstractOVDEvent {

	protected int cseqRefId = -1;
	protected String resourceId = null;
	protected int amount = 0;
	protected boolean requires = true;


	public ConsequenceResourceCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param consequenceRefId
	 *            a reserved reference id for the new consequence
	 * @param resourceId
	 *            ID of the target resource
	 * @param amount
	 *            amount resource to add / subtract
	 * @param req
	 *            whether this Consequence requires the player to have the specified
	 *            amount of resources to be able to select the plot branch (if amount
	 *            is negative)
	 */
	public void init( int consequenceRefId, String resourceId, int amount, boolean req ) {
		cseqRefId = consequenceRefId;
		this.resourceId = resourceId;
		this.amount = amount;
		requires = req;
	}

	public int getConsequenceRefId() {
		return cseqRefId;
	}

	public String getResource() {
		return resourceId;
	}

	public int getAmount() {
		return amount;
	}

	public boolean getRequires() {
		return requires;
	}

	@Override
	public void reset() {
		cseqRefId = -1;
		amount = 0;
		resourceId = null;
		requires = true;
	}
}
