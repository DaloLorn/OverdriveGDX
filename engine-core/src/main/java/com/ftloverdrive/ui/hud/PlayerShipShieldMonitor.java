package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
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


public class PlayerShipShieldMonitor extends Actor implements Disposable, GamePlayerShipChangeListener, ShipPropertyListener {

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/shield-hud.json";

	protected final Sprite bgSpriteOn;
	protected final Sprite bgSpriteOff;
	protected final Sprite shieldEmptySprite;
	protected final Sprite shieldFullSprite;
	protected final Sprite shieldRechargeBgSprite;
	protected final Sprite shieldRechargeBarSprite;

	protected final int offsetX;
	protected final int offsetY;
	protected final int spacingX;
	protected final int shieldRechargeBgAlignX;
	protected final int shieldRechargeBgAlignY;

	protected final Color colorRechargeBar;

	private final AssetManager assetManager;

	protected int shipModelRefId = -1;

	protected int shieldMax = 0;
	protected int shield = 0;
	protected float recharge = 0;
	protected float rechargePrev = 0;


	public PlayerShipShieldMonitor( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );

		bgSpriteOn = skin.getSprite( "shield-bg-on" );
		bgSpriteOff = skin.getSprite( "shield-bg-off" );
		shieldEmptySprite = skin.getSprite( "shield-empty" );
		shieldFullSprite = skin.getSprite( "shield-full" );
		shieldRechargeBgSprite = skin.getSprite( "shield-recharge-bg" );
		shieldRechargeBarSprite = skin.getSprite( "shield-recharge-bar" );

		colorRechargeBar = skin.getColor( "recharge-bar-color" );

		offsetX = skin.getInt( "shield-offset-x" );
		offsetY = skin.getInt( "shield-offset-y" );
		spacingX = skin.getInt( "shield-spacing-x" );
		shieldRechargeBgAlignX = skin.getInt( "shield-recharge-bg-align-x" );
		shieldRechargeBgAlignY = skin.getInt( "shield-recharge-bg-align-y" );

		setWidth( bgSpriteOn.getWidth() );
		setHeight( bgSpriteOn.getHeight() );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		super.draw( batch, parentAlpha );

		if ( shieldMax == 0 ) {
			bgSpriteOff.setPosition( getX(), getY() );
			bgSpriteOff.draw( batch, parentAlpha );
		}
		else {
			bgSpriteOn.setPosition( getX(), getY() );
			bgSpriteOn.draw( batch, parentAlpha );

			// Interpolate the percentage to make it more smooth.
			recharge = Interpolation.linear.apply( recharge, rechargePrev, 0.25f );
			shieldRechargeBarSprite.setSize( recharge * ( shieldRechargeBgSprite.getWidth() - 6 ), 6 );
			shieldRechargeBarSprite.setPosition( getX() + shieldRechargeBgAlignX + 3, getY() + shieldRechargeBgAlignY + 3 );
			shieldRechargeBarSprite.draw( batch, parentAlpha );

			for ( int shieldIndex = 0; shieldIndex < shieldMax; ++shieldIndex ) {
				if ( shieldIndex < shield ) {
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

		shieldRechargeBgSprite.setPosition( getX() + shieldRechargeBgAlignX, getY() + shieldRechargeBgAlignY );
		shieldRechargeBgSprite.draw( batch, parentAlpha );

		// TODO: Zoltan supershield
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
			if ( OVDConstants.SHIELD.equals( e.getPropertyKey() ) ||
					OVDConstants.SHIELD_MAX.equals( e.getPropertyKey() ) ||
					OVDConstants.SHIELD_FRACTION_MAX.equals( e.getPropertyKey() ) ||
					OVDConstants.SHIELD_FRACTION.equals( e.getPropertyKey() ) ) {
				updateShipInfo( context );
			}
		}
	}

	/**
	 * Updates the bar to match the player ship's Shield/ShieldMax.
	 */
	private void updateShipInfo( OverdriveContext context ) {
		if ( shipModelRefId == -1 ) {
			shieldMax = -1;
			shield = -1;
			recharge = -1;
			rechargePrev = -1;
		}
		else {
			ShipModel shipModel = context.getReferenceManager().getObject( shipModelRefId, ShipModel.class );
			int prevShield = shield;
			shield = shipModel.getProperties().getInt( OVDConstants.SHIELD );
			shieldMax = shipModel.getProperties().getInt( OVDConstants.SHIELD_MAX );

			if ( shield != prevShield || shield == shieldMax ) {
				recharge = 0;
				rechargePrev = 0;
			}

			float shieldFraction = shipModel.getProperties().getInt( OVDConstants.SHIELD_FRACTION );
			float shieldFractionMax = shipModel.getProperties().getInt( OVDConstants.SHIELD_FRACTION_MAX );
			rechargePrev = shieldFraction / shieldFractionMax;
			shieldRechargeBarSprite.setColor( colorRechargeBar );
		}
	}

	@Override
	public void dispose() {
		assetManager.unload( SKIN_PATH );
	}
}
