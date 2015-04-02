package com.ftloverdrive.model;

import com.ftloverdrive.core.OverdriveContext;


public interface Damagable {

	// TODO: Add DamageType and pass it through here
	// Or maybe create a Damage class that carries info about damage, shield piercing, type, etc.
	public void damage( OverdriveContext context, int value );
}
