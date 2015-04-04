package com.ftloverdrive.model;

/**
 * This class encapsulates an instance of damage.
 * For now the plan is to have each damage type be a separate damage instance
 * that would be applied sequentially in the order defined by the weapon.
 * 
 * So, eg.
 * <damage>2</damage>
 * <ion>3</ion>
 * Would deal 2 physical damage first, then 3 ion damage.
 * 
 * Maybe have flags that apply on per-tag basis, so that eg. only ion damage
 * has shield piercing?
 * 
 * TODO: Special effects, eg. fire, branch etc chance, lockdown, ion ?
 * TODO: Shield piercing -- but make it easy to extend, so that eg. armor plating
 * can be implemented in a similar fashion to mitigate physical damage, etc.
 */
public class DamageInstance {

	public static final int DAMAGE_FLAG_HULL_BUST = 1 << 0;

	protected DamageTypes type;
	protected int flags;
	protected int damageValue;


	public void setFlags( int flag ) {
		flags |= flag;
	}

	public void clearFlags( int flag ) {
		flags &= ~flag;
	}

	public void clearAllFlags() {
		flags = 0;
	}

	public boolean isFlagSetAll( int flag ) {
		return ( flags & flag ) == flag;
	}

	public boolean isFlagSetAny( int flag ) {
		return ( flags & flag ) != 0;
	}


	public static enum DamageTypes {
		PHYSICAL, IONIC, BIOLOGICAL, SYSTEM
	}
}
