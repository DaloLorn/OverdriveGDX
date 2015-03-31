package com.ftloverdrive.model.incident;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.model.AbstractOVDModel;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.ui.ShaderLabel;
import com.ftloverdrive.ui.incident.ConsequenceBox;
import com.ftloverdrive.util.OVDConstants;


public class ConsequenceResource extends AbstractOVDModel implements Consequence, Disposable {

	private OverdriveContext context;

	private String resourceId = null;
	private int amount = 0;
	private boolean requires = true;


	public ConsequenceResource( OverdriveContext context, String resourceId, int value, boolean requires ) {
		this.context = context;
		this.resourceId = resourceId;
		amount = value;
		this.requires = requires;
	}

	@Override
	public void placeConsequenceActor( OverdriveContext context, ConsequenceBox box ) {
		if ( amount == 0 )
			return;

		context.getAssetManager().load( OVDConstants.RESOURCE_ATLAS, TextureAtlas.class );
		context.getAssetManager().finishLoading();
		TextureAtlas atlas = context.getAssetManager().get( OVDConstants.RESOURCE_ATLAS );

		HorizontalGroup grp = new HorizontalGroup();
		Image rIcon = new Image( atlas.findRegion( "icon-" + resourceId.toLowerCase() ) );
		rIcon.setColor( amount > 0 ? box.getStylePositive().fontColor : box.getStyleNegative().fontColor );
		grp.addActor( rIcon );

		ShaderLabel lblConseq = new ShaderLabel( "" + amount, box.getStyleNormal() );
		grp.addActor( lblConseq );

		float h = Math.max( rIcon.getHeight(), lblConseq.getMinHeight() );
		Cell c = box.add( grp ).size( rIcon.getWidth() + lblConseq.getMinWidth(), h );

		// Find the first and last cell in this row to expand them. This centers all cells in the row.
		Cell first = null, last = c;
		for ( Cell cl : box.getCells() ) {
			if ( cl.getRow() == c.getRow() ) {
				cl.expand( 0, 0 ).pad( 0 ); // Reset to defaults
				if ( first == null )
					first = cl;
				last = cl;
			}
		}
		// If first == last, then we only have one cell, and it is already centered - don't expand it.
		if ( first != last ) {
			first.expandX().right();
			last.expandX().left();
		}
		first.padLeft( 5 );
		last.padRight( 10 );
	}

	@Override
	public boolean isSpoilable() {
		return true;
	}

	@Override
	public void execute( OverdriveContext context ) {
		if ( amount == 0 )
			return;

		// TODO Consequences need a target player ref id

		// TODO: Resources in player model or ship model? Consider ship as in-game avatar of the player,
		// and the player model only holds data immediately pertaining to the player (name, etc)??

		int gameRefId = context.getGameModelRefId();
		GameModel game = context.getReferenceManager().getObject( gameRefId, GameModel.class );
		int shipRefId = game.getPlayerShip( context.getNetManager().getLocalPlayerRefId() );

		ShipPropertyEvent event = Pools.get( ShipPropertyEvent.class ).obtain();
		event.init( shipRefId, ShipPropertyEvent.INT_TYPE, ShipPropertyEvent.INCREMENT_ACTION, resourceId, amount );
		context.getScreenEventManager().postDelayedEvent( event );
	}

	@Override
	public void addRequirements( PlotBranch branch ) {
		if ( requires )
			branch.addRequirement( new RequirementResource( resourceId, -amount ) );
	}

	@Override
	public void dispose() {
		context.getAssetManager().unload( OVDConstants.RESOURCE_ATLAS );
	}
}
