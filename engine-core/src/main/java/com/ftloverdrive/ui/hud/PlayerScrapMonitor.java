package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/scrap-hud.json";

	protected AssetManager assetManager;
	protected int shipModelRefId = -1;

	protected Sprite normalSprite;
	protected Sprite redSprite;

	protected Sprite bgSprite;
	protected ShaderLabel lblScrap;


	public PlayerScrapMonitor( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );

		normalSprite = skin.getSprite( "scrap-bg-normal" );
		redSprite = skin.getSprite( "scrap-bg-red" );

		int textOffsetX = skin.getInt( "scrap-text-offset-x" );
		int textOffsetY = skin.getInt( "scrap-text-offset-y" );

		ShaderLabelStyle stlScrap = skin.get( "scrap-text-style", ShaderLabelStyle.class );
		lblScrap = new ShaderLabel( "####", stlScrap );
		lblScrap.setAlignment( Align.center );
		lblScrap.setPosition( textOffsetX, textOffsetY, Align.center );
		addActor( lblScrap );

		setWidth( normalSprite.getWidth() );
		setHeight( normalSprite.getHeight() );
		bgSprite = redSprite;
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
		if ( e.getModelRefId() != shipModelRefId ) return;

		if ( e.getPropertyType() == ShipPropertyEvent.INT_TYPE ) {
			if ( OVDConstants.SCRAP.equals( e.getPropertyKey() ) ) {
				updateShipInfo( context );
			}
		}
	}

	private void updateShipInfo( OverdriveContext context ) {
		if ( shipModelRefId == -1 ) {
			bgSprite = redSprite;
		}
		else {
			ShipModel shipModel = context.getReferenceManager().getObject( shipModelRefId, ShipModel.class );
			int scrapAmount = shipModel.getProperties().getInt( OVDConstants.SCRAP );
			bgSprite = scrapAmount > 0 ? normalSprite : redSprite;
			lblScrap.setText( "" + scrapAmount );
		}
	}

	@Override
	public void dispose() {
		assetManager.unload( SKIN_PATH );
	}
}
