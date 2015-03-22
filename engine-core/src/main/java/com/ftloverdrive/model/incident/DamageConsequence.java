package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.AbstractOVDModel;
import com.ftloverdrive.model.Damagable;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.ui.ShaderLabel;
import com.ftloverdrive.ui.incident.ConsequenceBox;


public class DamageConsequence extends AbstractOVDModel implements Consequence {

	private int damageValue = 0;


	public DamageConsequence( int value ) {
		damageValue = value;
	}

	public void placeConsequenceActor( ConsequenceBox box ) {
		ShaderLabel lblConseq = new ShaderLabel( damageValue + " damage to your hull", box.getStyleNegative() );
		// lblConseq.setShader( new DistanceFieldFontShader( 1.0f / 4.0f ) );
		box.row();
		box.add( lblConseq ).pad( 5 ).padLeft( 10 ).padRight( 10 );
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
