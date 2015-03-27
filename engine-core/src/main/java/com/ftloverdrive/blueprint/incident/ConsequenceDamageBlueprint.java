package com.ftloverdrive.blueprint.incident;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.incident.ConsequenceDamageCreationEvent;


public class ConsequenceDamageBlueprint extends ConsequenceBlueprint {

	private int damageMin = 0;
	private int damageMax = 0;


	public ConsequenceDamageBlueprint( int damageMin, int damageMax ) {
		if ( damageMin < 0 || damageMax < 0 )
			throw new IllegalArgumentException( "Damage value is negative." );
		this.damageMin = damageMin;
		this.damageMax = damageMax;
	}

	@Override
	public int construct( OverdriveContext context ) {
		int consequenceRefId = context.getNetManager().requestNewRefId();

		ConsequenceDamageCreationEvent createE = Pools.get( ConsequenceDamageCreationEvent.class ).obtain();
		int damageValue = damageMin + (int)Math.round( Math.random() * ( damageMax - damageMin ) );
		createE.init( consequenceRefId, damageValue );
		context.getScreenEventManager().postDelayedEvent( createE );

		return consequenceRefId;
	}
}
