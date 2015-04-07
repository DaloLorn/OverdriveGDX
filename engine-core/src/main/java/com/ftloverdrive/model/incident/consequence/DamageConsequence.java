package com.ftloverdrive.model.incident.consequence;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.AbstractOVDModel;
import com.ftloverdrive.model.Damagable;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.model.incident.Consequence;
import com.ftloverdrive.model.incident.PlotBranch;
import com.ftloverdrive.ui.ShaderLabel;
import com.ftloverdrive.ui.incident.ConsequenceBox;


public class DamageConsequence extends AbstractOVDModel implements Consequence {

	private int damageValue = 0;


	public DamageConsequence( int value ) {
		damageValue = value;
	}

	@Override
	public void placeConsequenceActor( OverdriveContext context, ConsequenceBox box ) {
		ShaderLabel lblConseq = new ShaderLabel( damageValue + " damage to your hull", box.getStyleNegative() );
		// lblConseq.setShader( new DistanceFieldFontShader( 1.0f / 4.0f ) );
		boolean isFirst = box.getCells().size == 0;
		box.row();
		Cell c = box.add( lblConseq ).colspan( box.getColumns() ).pad( 5 ).padLeft( 20 ).padRight( 20 );
		if ( !isFirst )
			c.padTop( 0 );
	}

	@Override
	public boolean isSpoilable() {
		return false;
	}

	@Override
	public void execute( OverdriveContext context ) {
		int gameRefId = context.getGameModelRefId();
		GameModel game = context.getReferenceManager().getObject( gameRefId, GameModel.class );
		int shipRefId = game.getPlayerShip( context.getNetManager().getLocalPlayerRefId() );

		Damagable target = context.getReferenceManager().getObject( shipRefId, Damagable.class );
		target.damage( context, damageValue );
	}

	@Override
	public void addRequirements( PlotBranch branch ) {
		// None.
	}
}
