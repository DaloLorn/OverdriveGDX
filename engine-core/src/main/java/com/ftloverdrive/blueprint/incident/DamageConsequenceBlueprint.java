package com.ftloverdrive.blueprint.incident;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.incident.DamageConsequenceCreationEvent;


public class DamageConsequenceBlueprint extends ConsequenceBlueprint {

	private int damageMin = 0;
	private int damageMax = 0;


	public DamageConsequenceBlueprint( int damageMin, int damageMax ) {
		if ( damageMin < 0 || damageMax < 0 )
			throw new IllegalArgumentException( "Damage value is negative." );
		this.damageMin = damageMin;
		this.damageMax = damageMax;
	}

	@Override
	public int construct( OverdriveContext context ) {
		int consequenceRefId = context.getNetManager().requestNewRefId();

		DamageConsequenceCreationEvent createE = Pools.get( DamageConsequenceCreationEvent.class ).obtain();
		int damageValue = damageMin + (int)Math.round( Math.random() * ( damageMax - damageMin ) );
		createE.init( consequenceRefId, damageValue );
		context.getScreenEventManager().postDelayedEvent( createE );

		return consequenceRefId;
	}
}
