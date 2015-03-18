package com.ftloverdrive.event.incident;

import com.ftloverdrive.event.AbstractOVDEvent;


public class DamageConsequenceCreationEvent extends AbstractOVDEvent {

	protected int cseqRefId = -1;
	protected int damage = -1;


	public DamageConsequenceCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param consequenceRefId
	 *            a reserved reference id for the new branch
	 * @param damage
	 *            amount of damage to deal
	 */
	public void init( int consequenceRefId, int damage ) {
		cseqRefId = consequenceRefId;
		this.damage = damage;
	}

	public int getConsequenceRefId() {
		return cseqRefId;
	}

	public int getDamage() {
		return damage;
	}

	@Override
	public void reset() {
		cseqRefId = -1;
		damage = -1;
	}
}
