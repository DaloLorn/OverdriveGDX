package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.util.OVDConstants;


public class PlayerShipShieldMonitor extends Actor implements Disposable, GamePlayerShipChangeListener, ShipPropertyListener {

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/shield-hud.json";

	protected AssetManager assetManager;
	protected int shipModelRefId = -1;

	protected int shieldMax = 0;
	protected int shieldFull = 0;

	protected Sprite bgSprite;
	protected Sprite shieldEmptySprite;
	protected Sprite shieldFullSprite;

	protected int offsetX = 0;
	protected int offsetY = 0;
	protected int spacingX = 0;


	public PlayerShipShieldMonitor( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );

		bgSprite = skin.getSprite( "shield-bg-on" );
		shieldEmptySprite = skin.getSprite( "shield-empty" );
		shieldFullSprite = skin.getSprite( "shield-full" );

		offsetX = skin.getInt( "shield-offset-x" );
		offsetY = skin.getInt( "shield-offset-y" );
		spacingX = skin.getInt( "shield-spacing-x" );

		setWidth( bgSprite.getWidth() );
		setHeight( bgSprite.getHeight() );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		super.draw( batch, parentAlpha );

		bgSprite.setPosition( getX(), getY() );
		bgSprite.draw( batch, parentAlpha );

		for ( int shieldIndex = 0; shieldIndex < shieldMax; ++shieldIndex ) {
			if ( shieldIndex < shieldFull ) {
				shieldFullSprite.setPosition( getX() + offsetX + shieldIndex * spacingX,
						getY() + offsetY );
				shieldFullSprite.draw( batch, parentAlpha );
			}
			else {
				shieldEmptySprite.setPosition( getX() + offsetX + shieldIndex * spacingX,
						getY() + offsetY );
				shieldEmptySprite.draw( batch, parentAlpha );
			}
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
		if ( e.getShipRefId() != shipModelRefId ) return;

		if ( e.getPropertyType() == ShipPropertyEvent.INT_TYPE ) {
			if ( OVDConstants.SHIELD.equals( e.getPropertyKey() ) || OVDConstants.SHIELD_MAX.equals( e.getPropertyKey() ) ) {
				updateShipInfo( context );
			}
		}
	}

	/**
	 * Updates the bar to match the player ship's Shield/ShieldMax.
	 */
	private void updateShipInfo( OverdriveContext context ) {
		ShipModel shipModel = context.getReferenceManager().getObject( shipModelRefId, ShipModel.class );
		if ( shipModel == null ) {
			shieldMax = 0;
			shieldFull = 0;
		}
		else {
			shieldFull = shipModel.getProperties().getInt( OVDConstants.SHIELD );
			shieldMax = shipModel.getProperties().getInt( OVDConstants.SHIELD_MAX );
		}
	}

	@Override
	public void dispose() {
		assetManager.unload( SKIN_PATH );
	}
}
