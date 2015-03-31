package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.ui.ShaderLabel;
import com.ftloverdrive.ui.ShaderLabel.ShaderLabelStyle;
import com.ftloverdrive.util.OVDConstants;


public class PlayerScrapMonitor extends Group implements Disposable, GamePlayerShipChangeListener, ShipPropertyListener {

	public static final String SKIN_PATH = "internal://overdrive-assets/skins/player-hud/hud.json";

	protected AssetManager assetManager;
	protected int shipModelRefId = -1;

	protected Sprite scrapNormal;
	protected Sprite scrapRed;

	protected Sprite bgSprite;
	protected ShaderLabel lblScrap;


	public PlayerScrapMonitor( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( OVDConstants.STATUSUI_ATLAS, TextureAtlas.class );
		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.finishLoading();
		TextureAtlas statusUIAtlas = assetManager.get( OVDConstants.STATUSUI_ATLAS, TextureAtlas.class );
		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );

		scrapNormal = statusUIAtlas.createSprite( "top-scrap" );
		scrapRed = statusUIAtlas.createSprite( "top-scrap-red" );

		setWidth( scrapNormal.getWidth() );
		setHeight( scrapNormal.getHeight() );

		ShaderLabelStyle stlScrap = skin.get( "scrap-text", ShaderLabelStyle.class );

		lblScrap = new ShaderLabel( "####", stlScrap );
		lblScrap.setAlignment( Align.center );
		lblScrap.setPosition( 85, 27, Align.center );
		addActor( lblScrap );

		bgSprite = scrapRed;
	}


	@Override
	public void draw( Batch batch, float parentAlpha ) {
		bgSprite.setPosition( this.getX(), this.getY() );
		bgSprite.draw( batch, parentAlpha );
		super.draw( batch, parentAlpha );
	}


	public void setShipModel( OverdriveContext context, int shipModelRefId ) {
		this.shipModelRefId = shipModelRefId;
		updateShipInfo( context );
	}


	@Override
	public void gamePlayerShipChanged( OverdriveContext context, GamePlayerShipChangeEvent e ) {
		if ( e.getPlayerRefId() != context.getNetManager().getLocalPlayerRefId() ) {
			return;
		}

		setShipModel( context, e.getShipRefId() );
	}

	@Override
	public void shipPropertyChanged( OverdriveContext context, ShipPropertyEvent e ) {
		if ( e.getShipRefId() != shipModelRefId ) return;

		if ( e.getPropertyType() == ShipPropertyEvent.INT_TYPE ) {
			if ( OVDConstants.SCRAP.equals( e.getPropertyKey() ) ) {
				updateShipInfo( context );
			}
		}
	}


	/**
	 * Updates the bar to match the player ship's Hull/HullMax.
	 */
	private void updateShipInfo( OverdriveContext context ) {
		if ( shipModelRefId == -1 ) {
			bgSprite = scrapRed;
		}
		else {
			ShipModel shipModel = context.getReferenceManager().getObject( shipModelRefId, ShipModel.class );
			int scrapAmount = shipModel.getProperties().getInt( OVDConstants.SCRAP );
			bgSprite = scrapAmount > 0 ? scrapNormal : scrapRed;
			lblScrap.setText( "" + scrapAmount );
		}
	}


	// Actors don't normally have a dispose().
	@Override
	public void dispose() {
		assetManager.unload( OVDConstants.STATUSUI_ATLAS );
		assetManager.unload( SKIN_PATH );
	}
}
