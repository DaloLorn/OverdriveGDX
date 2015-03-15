package com.ftloverdrive.model.status;


/**
 * A common interface for effects that affect other entities -- for example
 * fire, breach, stun, ion, power limit/div/set
 *
 * Effects could be applied as a result of incidents, or by an entity within a room
 * (FireEntity, IonDummyEntity, etc)
 * 
 * TODO: Stub
 */
public interface StatusModel {

	// stacks: multiple fires deal more damage, multiple breaches suck out oxygen faster, etc...

	public boolean isStackable();
	public void setStackable(boolean stacks);

	public int getStackCount();
	public void setStackCount(int stacks);

	
}
