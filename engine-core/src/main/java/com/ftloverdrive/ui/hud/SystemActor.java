package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyListener;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.system.SystemModel;
import com.ftloverdrive.ui.ModelActor;
import com.ftloverdrive.util.OVDConstants;


/**
 * Actor representing a system in the player's HUD.
 * 
 */
public class SystemActor extends ModelActor
		implements SystemPropertyListener, Disposable {

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/reactor-ui/system-ui.json";
	public static final String ATLAS_PATH = "overdrive-assets/skins/player-hud/reactor-ui/reactor-ui.atlas";

	protected final NinePatch barEmpty;
	protected final NinePatch barFull;
	protected final NinePatch diagonal;
	protected final NinePatch ionFrame;
	protected final NinePatchDrawable barDrawable;

	protected final Color colorHasPower;
	protected final Color colorNoPower;
	protected final Color colorIon;
	protected final Color colorDisabled;
	protected final Color colorSelfPower;
	protected final Color colorDestroyed;

	protected final int sysBarWidth;
	protected final int sysBarHeight;
	protected final int clickableIconWidth;
	protected final int clickableIconHeight;
	protected final int ionFrameGapX;
	protected final int ionFrameGapY;
	protected final int barOffsetY;
	protected final int iconOffsetY;
	protected final int barPaddingY;
	protected final int barGroupPaddingY;

	private final AssetManager assetManager;
	private final Image icon;

	private float systemOffset = 0;

	private int powerCap = 0;
	private int powerCurrent = 0;
	private int powerIncrement = 0;
	private int powerIoned = 0;
	private int powerDisabled = 0;
	private int powerDestroyed = 0;

	private float barsHeight = 0;


	public SystemActor( OverdriveContext context ) {
		super( context );
		assetManager = context.getAssetManager();

		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.load( ATLAS_PATH, TextureAtlas.class );
		assetManager.load( OVDConstants.ICONS_ATLAS, TextureAtlas.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );
		TextureAtlas wireAtlas = assetManager.get( ATLAS_PATH, TextureAtlas.class );

		colorHasPower = skin.getColor( "power-available" );
		colorNoPower = skin.getColor( "power-none" );
		colorDisabled = skin.getColor( "power-disabled" );
		colorSelfPower = skin.getColor( "power-self" );
		colorIon = skin.getColor( "power-ion" );
		colorDestroyed = skin.getColor( "power-destroyed" );

		barDrawable = new NinePatchDrawable();
		barEmpty = new NinePatch( wireAtlas.findRegion( "bar-empty" ), 1, 1, 1, 1 );
		barEmpty.setColor( colorNoPower );
		barFull = new NinePatch( wireAtlas.findRegion( "bar-full" ), 1, 1, 1, 1 );
		barFull.setColor( colorHasPower );
		diagonal = new NinePatch( wireAtlas.findRegion( "diagonal" ) );
		diagonal.setColor( colorDisabled );
		ionFrame = new NinePatch( wireAtlas.findRegion( "ion-frame" ), 2, 2, 2, 2 );
		ionFrame.setColor( colorIon );

		sysBarWidth = skin.getInt( "system-bar-width" );
		sysBarHeight = skin.getInt( "system-bar-height" );
		clickableIconWidth = skin.getInt( "clickable-icon-width" );
		clickableIconHeight = skin.getInt( "clickable-icon-height" );
		barOffsetY = skin.getInt( "bar-offset-y" );
		iconOffsetY = skin.getInt( "icon-offset-y" );
		ionFrameGapX = skin.getInt( "ion-frame-gap-x" ) + (int)ionFrame.getLeftWidth();
		ionFrameGapY = skin.getInt( "ion-frame-gap-y" ) + (int)ionFrame.getTopHeight();
		barPaddingY = skin.getInt( "bar-padding-y" );
		barGroupPaddingY = skin.getInt( "bar-group-padding-y" );

		icon = new Image();
		addActor( icon );

		systemOffset = 0;
		setSize( 0, 0 );

		setTouchable( Touchable.childrenOnly );
	}


	public void drawChildren( Batch batch, float parentAlpha ) {
		super.drawChildren( batch, parentAlpha );

		// Draw power bars
		float x = icon.getX() + ( clickableIconWidth - sysBarWidth ) * 0.5f;
		float y = icon.getY() + clickableIconHeight + barOffsetY;

		if ( powerIoned > 0 ) {
			barDrawable.setPatch( ionFrame );
			barDrawable.draw( batch, x - ionFrameGapX, y - ionFrameGapY,
					2 * ionFrameGapX + sysBarWidth, 2 * ionFrameGapY + barsHeight );
			barFull.setColor( colorIon );
		}
		else {
			barFull.setColor( colorHasPower );
		}

		barDrawable.setPatch( barFull );
		for ( int i = 0; i < powerCap; ++i ) {
			if ( i >= powerCap - powerDestroyed ) {
				barDrawable.setPatch( diagonal );
				diagonal.setColor( colorDestroyed );
				barDrawable.draw( batch, x, y, sysBarWidth, sysBarHeight );

				barDrawable.setPatch( barEmpty );
				barEmpty.setColor( colorDestroyed );
			}
			else if ( i >= powerCap - ( powerDisabled + powerDestroyed ) ) {
				barDrawable.setPatch( diagonal );
				diagonal.setColor( colorDisabled );
				barDrawable.draw( batch, x, y, sysBarWidth, sysBarHeight );

				barDrawable.setPatch( barEmpty );
				barEmpty.setColor( colorDisabled );
			}
			else if ( i >= powerCurrent && barDrawable.getPatch() != barEmpty ) {
				barDrawable.setPatch( barEmpty );
				barEmpty.setColor( colorNoPower );
			}
			barDrawable.draw( batch, x, y, sysBarWidth, sysBarHeight );
			y += sysBarHeight;
			if ( powerIncrement > 1 && i % powerIncrement == powerIncrement - 1 )
				y += barGroupPaddingY;
			else
				y += barPaddingY;
		}
	}

	/**
	 * X offset of the system actor. The actor will be moved to the right by that amount (also extending the wire)
	 * Useful when you want to place something to the left of the system icon.
	 */
	public float getSystemOffset() {
		return systemOffset;
	}

	@Override
	protected void updateInfo( OverdriveContext context ) {
		if ( modelRefId == -1 ) {
			systemOffset = -1;
			powerCap = -1;
			powerCurrent = -1;
			powerIncrement = -1;
			powerIoned = -1;
			powerDisabled = -1;
			powerDestroyed = -1;
		}
		else {
			SystemModel systemModel = context.getReferenceManager().getObject( modelRefId, SystemModel.class );
			updateIcon( context, systemModel );

			powerCap = systemModel.getPowerCapacity();
			powerCurrent = systemModel.getCurrentPower();
			powerIncrement = systemModel.getPowerIncrement();
			powerIoned = systemModel.getProperties().getInt( OVDConstants.POWER_IONED );
			powerDisabled = systemModel.getProperties().getInt( OVDConstants.POWER_DISABLED );
			powerDestroyed = systemModel.getProperties().getInt( OVDConstants.POWER_DESTROYED );

			barsHeight = 0;
			for ( int i = 0; i < powerCap; ++i ) {
				barsHeight += sysBarHeight;
				if ( i < powerCap - 1 ) {
					if ( powerIncrement > 1 && i % powerIncrement == powerIncrement - 1 )
						barsHeight += barGroupPaddingY;
					else
						barsHeight += barPaddingY;
				}
			}

			setSize( icon.getWidth(), icon.getHeight() + barsHeight );
		}
	}

	private void updateIcon( final OverdriveContext context, final SystemModel model ) {
		String textureName = model.getIconName();
		if ( powerDestroyed == 0 )
			textureName += "-green1";
		else if ( powerDestroyed < powerCap )
			textureName += "-orange1";
		else
			textureName += "-red1";

		TextureAtlas iconAtlas = context.getAssetManager().get( OVDConstants.ICONS_ATLAS, TextureAtlas.class );
		TextureRegion iconRegion = iconAtlas.findRegion( textureName );
		icon.setDrawable( new TextureRegionDrawable( iconRegion ) );
		icon.setScaling( Scaling.none );
		icon.setY( -( icon.getPrefHeight() - clickableIconHeight ) * 0.5f + iconOffsetY );
		icon.setSize( clickableIconWidth, clickableIconHeight );

		if ( icon.getListeners().size > 0 )
			icon.removeListener( icon.getListeners().get( 0 ) );
		icon.addListener( new ClickListener() {
			@Override
			public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
				if ( model.isSelfPowered() ) {
					// TODO: sounds + info
					return true;
				}

				if ( powerIoned > 0 ) {
					// TODO: sounds + info
					return true;
				}

				if ( button == Buttons.LEFT ) {
					int result = model.getCurrentPower() + model.getPowerIncrement();
					if ( result <= model.getPowerCapacity() - ( powerDisabled + powerDestroyed ) )
						model.setCurrentPower( context, result );
					// TODO: sounds
					return true;
				}
				else if ( button == Buttons.RIGHT ) {
					int result = model.getCurrentPower() - model.getPowerIncrement();
					if ( result >= 0 )
						model.setCurrentPower( context, result );
					// TODO: sounds
					return true;
				}
				return false;
			}
		} );
	}

	@Override
	public void systemPropertyChanged( OverdriveContext context, SystemPropertyEvent e ) {
		if ( e.getModelRefId() != modelRefId ) return;

		updateInfo( context );
	}

	@Override
	public void dispose() {
		assetManager.unload( SKIN_PATH );
		assetManager.unload( ATLAS_PATH );
		assetManager.unload( OVDConstants.ICONS_ATLAS );
	}
}
