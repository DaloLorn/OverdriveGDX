package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.util.OVDConstants;


public class PlayerShipHullMonitor extends Actor implements Disposable, GamePlayerShipChangeListener, ShipPropertyListener {

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/hull-hud.json";

	protected AssetManager assetManager;
	protected int shipModelRefId = -1;

	protected Sprite bgSprite;
	protected Sprite barSprite;
	protected int offsetX = 0;
	protected int offsetY = 0;
	protected Color cHigh;
	protected Color cMedium;
	protected Color cLow;

	protected Color cCurrent;
	protected float barClipWidth = 0;


	public PlayerShipHullMonitor( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );

		bgSprite = skin.getSprite( "hull-bg" );
		barSprite = skin.getSprite( "hull-bar-mask" );

		offsetX = skin.getInt( "bar-offset-x" );
		offsetY = skin.getInt( "bar-offset-y" );

		cHigh = skin.getColor( "bar-color-high" );
		cMedium = skin.getColor( "bar-color-med" );
		cLow = skin.getColor( "bar-color-low" );

		setWidth( bgSprite.getWidth() );
		setHeight( bgSprite.getHeight() );

		cCurrent = cHigh;
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		super.draw( batch, parentAlpha );

		bgSprite.setPosition( getX(), getY() );
		bgSprite.draw( batch, parentAlpha );

		batch.flush();
		if ( clipBegin( getX() + offsetX, getY() + offsetY, barClipWidth, bgSprite.getHeight() ) ) {
			barSprite.setPosition( getX() + offsetX, getY() + bgSprite.getHeight() / 2 - barSprite.getHeight() / 2 + offsetY );
			barSprite.setColor( cCurrent );
			barSprite.draw( batch, parentAlpha );
			batch.flush();
			clipEnd();
		}
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

		if ( e.getPropertyType() == PropertyEvent.INT_TYPE ) {
			if ( OVDConstants.HULL.equals( e.getPropertyKey() ) || OVDConstants.HULL_MAX.equals( e.getPropertyKey() ) ) {
				updateShipInfo( context );
			}
		}
	}

	/**
	 * Updates the bar to match the player ship's Hull/HullMax.
	 */
	private void updateShipInfo( OverdriveContext context ) {
		if ( shipModelRefId == -1 ) {
			barClipWidth = 0;
		}
		else {
			ShipModel shipModel = context.getReferenceManager().getObject( shipModelRefId, ShipModel.class );
			int hullAmt = shipModel.getProperties().getInt( OVDConstants.HULL );
			int hullMax = shipModel.getProperties().getInt( OVDConstants.HULL_MAX );
			if ( hullMax != 0 ) {
				barClipWidth = Math.min( ( (float)hullAmt / hullMax ) * barSprite.getWidth(), barSprite.getWidth() );

				float p = ( (float)hullAmt ) / hullMax;
				if ( p > 0.66f )
					cCurrent = cHigh;
				else if ( p > 0.33f )
					cCurrent = cMedium;
				else
					cCurrent = cLow;
			}
			else {
				barClipWidth = 0;
			}
		}
	}

	@Override
	public void dispose() {
		assetManager.unload( SKIN_PATH );
	}
}
