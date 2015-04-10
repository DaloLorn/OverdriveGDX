package com.ftloverdrive.event.incident;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractOVDEvent;


public class ConsequenceDamageCreationEvent extends AbstractOVDEvent implements Poolable {

	protected int cseqRefId = -1;
	protected int damage = -1;


	public ConsequenceDamageCreationEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param consequenceRefId
	 *            a reserved reference id for the new consequence
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
		super.reset();
		cseqRefId = -1;
		damage = -1;
	}
}
