package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.AbstractOVDModel;
import com.ftloverdrive.model.Damagable;
import com.ftloverdrive.model.GameModel;


public class DamageConsequence extends AbstractOVDModel implements Consequence {

	private int damageValue = 0;


	public DamageConsequence( int value ) {
		damageValue = value;
	}

	public String getSpoilerText() {
		return damageValue + " damage to your hull";
	}

	@Override
	public void execute( OverdriveContext context ) {
		int gameRefId = context.getGameModelRefId();
		GameModel game = context.getReferenceManager().getObject( gameRefId, GameModel.class );
		int shipRefId = game.getPlayerShip( context.getNetManager().getLocalPlayerRefId() );

		Damagable target = context.getReferenceManager().getObject( shipRefId, Damagable.class );
		target.damage( context, damageValue );
	}
}
