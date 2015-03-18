package com.ftloverdrive.blueprint.incident;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.incident.DamageConsequenceCreationEvent;


public class DamageConsequenceBlueprint extends ConsequenceBlueprint {

	private int damage = 0;


	public DamageConsequenceBlueprint( int damageValue ) {
		if ( damageValue < 0 )
			throw new IllegalArgumentException( "Damage value is negative." );
		damage = damageValue;
	}

	@Override
	public String getSpoilerText() {
		return damage + " damage to your hull";
	}

	@Override
	public int construct( OverdriveContext context ) {
		int consequenceRefId = context.getNetManager().requestNewRefId();

		DamageConsequenceCreationEvent createE = Pools.get( DamageConsequenceCreationEvent.class ).obtain();
		createE.init( consequenceRefId, damage );
		context.getScreenEventManager().postDelayedEvent( createE );

		return consequenceRefId;
	}
}
