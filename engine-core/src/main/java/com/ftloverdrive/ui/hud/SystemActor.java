package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyListener;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.system.SystemModel;
import com.ftloverdrive.ui.ModelActor;
import com.ftloverdrive.ui.OVDTooltip;
import com.ftloverdrive.ui.OVDTooltipListener;
import com.ftloverdrive.util.OVDConstants;


/**
 * Actor representing a system in the player's HUD.
 * 
 * TODO: This will need to be replaced by an interface + separate implementations for PC and Android.
 * 
 * @author kartoFlane
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

	protected final Sprite lock;
	protected final Sprite lockWhite;
	protected final Sprite glow;

	protected final Color colorHasPower;
	protected final Color colorNoPower;
	protected final Color colorIon;
	protected final Color colorDisabled;
	protected final Color colorSelfPower;
	protected final Color colorDestroyed;

	protected final Image icon;
	protected final Image iconHover;
	protected final OVDTooltip tooltip;
	protected final Actor tooltipTrigger;
	protected final IonLockActor ionLock;
	protected final SystemButtonsActor systemButtons;

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
	protected final int buttonOffsetX;
	protected final int buttonOffsetY;

	private final AssetManager assetManager;

	private float systemOffset = 0;
	private float barsHeight = 0;

	private int powerCap = 0;
	private int powerCurrent = 0;
	private int powerIncrement = 0;
	private int powerIoned = 0;
	private int powerDisabled = 0;
	private int powerDestroyed = 0;


	public SystemActor( OverdriveContext context ) {
		super( context );
		assetManager = context.getAssetManager();

		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.load( ATLAS_PATH, TextureAtlas.class );
		assetManager.load( OVDConstants.ION_LOCK_ATLAS, TextureAtlas.class );
		assetManager.load( OVDConstants.ICONS_ATLAS, TextureAtlas.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );
		TextureAtlas reactorAtlas = assetManager.get( ATLAS_PATH, TextureAtlas.class );
		TextureAtlas lockAtlas = assetManager.get( OVDConstants.ION_LOCK_ATLAS, TextureAtlas.class );

		colorHasPower = skin.getColor( "power-available" );
		colorNoPower = skin.getColor( "power-none" );
		colorDisabled = skin.getColor( "power-disabled" );
		colorSelfPower = skin.getColor( "power-self" );
		colorIon = skin.getColor( "power-ion" );
		colorDestroyed = skin.getColor( "power-destroyed" );

		barDrawable = new NinePatchDrawable();
		barEmpty = new NinePatch( reactorAtlas.findRegion( "bar-empty" ), 1, 1, 1, 1 );
		barEmpty.setColor( colorNoPower );
		barFull = new NinePatch( reactorAtlas.findRegion( "bar-full" ), 1, 1, 1, 1 );
		barFull.setColor( colorHasPower );
		diagonal = new NinePatch( reactorAtlas.findRegion( "diagonal" ) );
		diagonal.setColor( colorDisabled );
		ionFrame = new NinePatch( reactorAtlas.findRegion( "ion-frame" ), 2, 2, 2, 2 );
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
		buttonOffsetX = skin.getInt( "button-base-offset-x" );
		buttonOffsetY = skin.getInt( "button-base-offset-y" );

		tooltipTrigger = new Actor();
		addActor( tooltipTrigger );

		icon = new Image();
		icon.setScaling( Scaling.none );
		addActor( icon );

		iconHover = new Image();
		iconHover.setScaling( Scaling.none );
		iconHover.setTouchable( Touchable.disabled );
		iconHover.setVisible( false );
		addActor( iconHover );

		tooltip = new OVDTooltip( context );
		tooltip.addTo( tooltipTrigger );

		lock = lockAtlas.createSprite( "s-lock" );
		lockWhite = lockAtlas.createSprite( "s-lock-white" );
		glow = reactorAtlas.createSprite( "glow" );
		glow.setSize( sysBarWidth * 2, sysBarHeight * 2 );

		ionLock = new IonLockActor( context );
		addActor( ionLock );

		systemButtons = new SystemButtonsActor( context );
		addActor( systemButtons );

		systemOffset = 0;
		setSize( 0, 0 );

		setTouchable( Touchable.childrenOnly );
	}


	public void drawChildren( Batch batch, float parentAlpha ) {
		float x = icon.getX() + ( clickableIconWidth - sysBarWidth ) * 0.5f;
		float y = icon.getY() + clickableIconHeight + barOffsetY;

		// Draw the ion frame around power bars
		if ( powerIoned > 0 ) {
			barDrawable.setPatch( ionFrame );
			barDrawable.draw( batch, x - ionFrameGapX, y - ionFrameGapY,
					2 * ionFrameGapX + sysBarWidth, 2 * ionFrameGapY + barsHeight );
			barFull.setColor( colorIon );
			glow.setColor( colorIon );
		}
		else {
			barFull.setColor( colorHasPower );
			glow.setColor( colorHasPower );
		}

		// Draw power bars
		barDrawable.setPatch( barFull );
		for ( int i = 0; i < powerCap; ++i ) {
			if ( i >= powerCap - powerDestroyed ) {
				barDrawable.setPatch( diagonal );
				diagonal.setColor( colorDestroyed );
				barDrawable.draw( batch, x, y, sysBarWidth, sysBarHeight );

				barDrawable.setPatch( barEmpty );
				barEmpty.setColor( colorDestroyed );
				glow.setColor( colorDestroyed );
			}
			else if ( i >= powerCap - ( powerDisabled + powerDestroyed ) ) {
				barDrawable.setPatch( diagonal );
				diagonal.setColor( colorDisabled );
				barDrawable.draw( batch, x, y, sysBarWidth, sysBarHeight );

				barDrawable.setPatch( barEmpty );
				barEmpty.setColor( colorDisabled );
				glow.setColor( colorDisabled );
			}
			else if ( i >= powerCurrent && barDrawable.getPatch() != barEmpty ) {
				barDrawable.setPatch( barEmpty );
				barEmpty.setColor( colorNoPower );
				glow.setColor( colorNoPower );
			}

			if ( iconHover.isVisible() ) {
				glow.setPosition( x - sysBarWidth * 0.5f, y - sysBarHeight * 0.5f );
				glow.draw( batch, 0.66f * parentAlpha );
			}

			barDrawable.draw( batch, x, y, sysBarWidth, sysBarHeight );

			y += sysBarHeight;
			if ( powerIncrement > 1 && i % powerIncrement == powerIncrement - 1 )
				y += barGroupPaddingY;
			else
				y += barPaddingY;
		}

		// Draw the ion padlock
		if ( powerIoned > 0 ) {
			lockWhite.setPosition( ( icon.getWidth() - lockWhite.getWidth() ) * 0.5f, y );
			lock.setPosition( ( icon.getWidth() - lock.getWidth() ) * 0.5f, y );
			lockWhite.draw( batch, parentAlpha );
			lock.draw( batch, parentAlpha );
		}

		super.drawChildren( batch, parentAlpha );
	}

	/**
	 * X offset of the system actor. The actor will be moved to the right by that amount (also extending the wire)
	 * Useful when you want to place something to the left of the system icon.
	 */
	public float getSystemOffset() {
		return systemOffset;
	}

	public void setModelRefId( int modelRefId ) {
		super.setModelRefId( modelRefId );
		ionLock.setModelRefId( modelRefId );
		systemButtons.setModelRefId( modelRefId );
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

			powerCap = systemModel.getPowerCapacity();
			powerCurrent = systemModel.getCurrentPower();
			powerIncrement = systemModel.getPowerIncrement();
			powerIoned = systemModel.getProperties().getInt( OVDConstants.POWER_IONED );
			powerDisabled = systemModel.getProperties().getInt( OVDConstants.POWER_DISABLED );
			powerDestroyed = systemModel.getProperties().getInt( OVDConstants.POWER_DESTROYED );

			updateIcon( context, systemModel );
			ionLock.setPosition( ( icon.getWidth() - ionLock.getWidth() ) * 0.5f, icon.getY() + ( icon.getWidth() - ionLock.getHeight() ) * 0.5f );

			ionLock.setVisible( powerIoned > 0 );
			// icon.setVisible( powerIoned == 0 ); // TODO: Commented out so that the actor remains clickable for testing purposes. Uncomment

			updateTooltip( systemModel );

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

			systemButtons.setPosition( icon.getX() + icon.getWidth() + buttonOffsetX, icon.getY() + buttonOffsetY );
			systemButtons.updateInfo( context );

			float w = icon.getWidth() + systemButtons.getWidth();
			float h = icon.getHeight() + iconOffsetY + barsHeight + barOffsetY + ionFrameGapY;
			setSize( w, h );
			tooltipTrigger.setPosition( icon.getX(), icon.getY() );
			tooltipTrigger.setSize( w, h );
		}
	}

	private void updateTooltip( final SystemModel systemModel ) {
		StringBuilder buf = new StringBuilder();

		buf.append( systemModel.getTooltipSystemDescription() );
		buf.append( "\n\n" );
		String t = systemModel.getTooltipPowerDescription();
		if ( t != null && !t.equals( "" ) ) {
			buf.append( t );
			buf.append( "\n\n" );
		}
		if ( systemModel.isManned() ) {
			buf.append( "Manned: " ).append( systemModel.getTooltipManningDescription() );
			buf.append( "\n\n" );
		}
		buf.append( "Status:\n" );
		buf.append( systemModel.getTooltipStatusDescription() );

		if ( !systemModel.isSelfPowered() ) {
			buf.append( "\n\n" );
			buf.append( "Add Power: " ).append( systemModel.getAddPowerKey() );
			buf.append( "\n" );
			buf.append( "Remove Power: " ).append( systemModel.getRemovePowerKey() );
		}

		tooltip.setText( buf.toString() );
	}

	private void updateIcon( final OverdriveContext context, final SystemModel model ) {
		// TODO: Rewrite using GDX Button?

		String textureName = model.getIconName();
		if ( powerDestroyed == 0 )
			if ( powerCurrent == 0 )
				textureName += "-grey";
			else
				textureName += "-green";
		else if ( powerDestroyed < powerCap )
			textureName += "-orange";
		else
			textureName += "-red";

		TextureAtlas iconAtlas = context.getAssetManager().get( OVDConstants.ICONS_ATLAS, TextureAtlas.class );
		TextureRegion iconRegion = iconAtlas.findRegion( textureName + "1" );
		icon.setDrawable( new TextureRegionDrawable( iconRegion ) );
		icon.setY( -( icon.getPrefHeight() - clickableIconHeight ) * 0.5f + iconOffsetY );
		icon.setSize( clickableIconWidth, clickableIconHeight );

		iconRegion = iconAtlas.findRegion( textureName + "2" );
		iconHover.setDrawable( new TextureRegionDrawable( iconRegion ) );
		iconHover.setY( -( icon.getPrefHeight() - clickableIconHeight ) * 0.5f + iconOffsetY );
		iconHover.setSize( clickableIconWidth, clickableIconHeight );

		if ( icon.getListeners().size > 0 )
			icon.removeListener( icon.getListeners().get( 0 ) );
		icon.addListener( new InputListener() {
			@Override
			public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
				// TODO: For testing purposes only, remove.
				{
					if ( button == Buttons.FORWARD ) {
						SystemPropertyEvent e = Pools.get( SystemPropertyEvent.class ).obtain();
						e.init( modelRefId, PropertyEvent.INCREMENT_ACTION, OVDConstants.POWER_IONED, 1 );
						context.getScreenEventManager().postDelayedEvent( e );
						return true;
					}
					else if ( button == Buttons.BACK ) {
						SystemPropertyEvent e = Pools.get( SystemPropertyEvent.class ).obtain();
						e.init( modelRefId, PropertyEvent.INCREMENT_ACTION, OVDConstants.POWER_IONED, -1 );
						context.getScreenEventManager().postDelayedEvent( e );
						return true;
					}
				}

				if ( model.isSelfPowered() ) {
					// TODO: sounds
					return true;
				}

				if ( powerIoned > 0 ) {
					// TODO: sounds
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

			public boolean mouseMoved( InputEvent event, float x, float y ) {
				return ( (InputListener)tooltipTrigger.getListeners().get( 0 ) ).mouseMoved( event, x, y );
			}

			public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor ) {
				if ( fromActor != icon )
					iconHover.setVisible( true );

				OVDTooltipListener l = (OVDTooltipListener)tooltipTrigger.getListeners().get( 0 );
				float temp = l.getShowDelay();
				if ( fromActor == tooltipTrigger )
					l.setShowDelay( 0 );
				l.enter( event, x, y, pointer, fromActor );
				if ( fromActor == tooltipTrigger )
					l.setShowDelay( temp );
			}

			public void exit( InputEvent event, float x, float y, int pointer, Actor toActor ) {
				if ( toActor != icon )
					iconHover.setVisible( false );

				OVDTooltipListener l = (OVDTooltipListener)tooltipTrigger.getListeners().get( 0 );
				if ( toActor != tooltipTrigger )
					l.exit( event, x, y, pointer, toActor );
			}
		} );
	}

	@Override
	public void systemPropertyChanged( OverdriveContext context, SystemPropertyEvent e ) {
		if ( e.getModelRefId() != modelRefId ) return;

		ionLock.updateInfo( context );
		updateInfo( context );
	}

	@Override
	public void dispose() {
		ionLock.dispose();

		assetManager.unload( SKIN_PATH );
		assetManager.unload( ATLAS_PATH );
		assetManager.unload( OVDConstants.ION_LOCK_ATLAS );
		assetManager.unload( OVDConstants.ICONS_ATLAS );
	}
}
