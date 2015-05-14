package com.ftloverdrive.ui.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap.Keys;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyListener;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.ui.ShaderLabel;
import com.ftloverdrive.ui.ShaderLabel.ShaderLabelStyle;
import com.ftloverdrive.util.OVDConstants;


/**
 * This class handles the drawing of power lines in the player's HUD,
 * system icons, and reactor and system power bars.
 * 
 * @author kartoFlane
 *
 */
public class PlayerShipReactorUI extends Group
		implements Disposable, GamePlayerShipChangeListener, ShipPropertyListener, SystemPropertyListener {

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/reactor-ui.json";
	public static final String ATLAS_PATH = "overdrive-assets/images/wire-ui/wire.atlas";

	protected final Sprite wireTile;
	protected final Sprite connector;

	protected final NinePatch barEmpty;
	protected final NinePatch barDisabled;
	protected final NinePatch barFull;
	protected final NinePatchDrawable barDrawable;

	protected final Color colorHasPower;
	protected final Color colorNoPower;
	protected final Color colorIon;
	protected final Color colorDisabled;
	protected final Color colorSelfPower;

	protected final Sprite systemNoneCap;
	protected final Sprite systemTile;
	protected final Sprite systemCap;

	/** Drawn when there are no power bars */
	protected final Sprite reactorNoneCap;
	/** Drawn when there's only one power bar */
	protected final Sprite reactorFirstCap;
	protected final Sprite reactorTile;
	protected final Sprite reactorFirst;
	protected final Sprite reactorCap;

	protected final ShaderLabel lblPower;

	protected final int reactorMaxBarsToShow;
	protected final int reactorTileAlignY;
	protected final int reactorTileOverlapOffsetY;
	protected final int barSpacingV;
	protected final int barOffsetX;
	protected final int barOffsetY;
	protected final int powerTextOffsetX;
	protected final int powerTextOffsetY;
	protected final int connectorSystemOffsetX;
	protected final int systemTileAlignX;
	protected final int systemTileAlignY;
	protected final int reactorBarWidth;
	protected final int reactorBarHeight;
	protected final float unpoweredAlpha;

	protected final Map<String, Object> resourceMap = new HashMap<String, Object>();
	protected final List<Vector2> systemOffsetsList = new ArrayList<Vector2>();

	private final AssetManager assetManager;

	protected int shipModelRefId = -1;

	protected int barDisabledCount = 0;
	protected int barCount = 0;
	protected int barMax = 0;


	public PlayerShipReactorUI( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.load( ATLAS_PATH, TextureAtlas.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );
		TextureAtlas wireAtlas = assetManager.get( ATLAS_PATH, TextureAtlas.class );

		wireTile = wireAtlas.createSprite( "wire-tile" );
		connector = wireAtlas.createSprite( "connector" );

		colorHasPower = skin.getColor( "power-available" );
		colorNoPower = skin.getColor( "power-none" );
		colorIon = skin.getColor( "power-ion" );
		colorDisabled = skin.getColor( "power-disabled" );
		colorSelfPower = skin.getColor( "power-self" );
		resourceMap.put( "power-available", colorHasPower );
		resourceMap.put( "power-none", colorNoPower );
		resourceMap.put( "power-ion", colorIon );
		resourceMap.put( "power-disabled", colorDisabled );
		resourceMap.put( "power-self", colorSelfPower );

		barEmpty = new NinePatch( wireAtlas.findRegion( "bar-empty" ), 1, 1, 1, 1 );
		barEmpty.setColor( colorNoPower );
		barFull = new NinePatch( wireAtlas.findRegion( "bar-full" ), 1, 1, 1, 1 );
		barFull.setColor( colorHasPower );
		barDisabled = new NinePatch( wireAtlas.findRegion( "bar-disabled" ), 1, 1, 1, 1 );
		barDisabled.setColor( colorDisabled );
		resourceMap.put( "bar-empty", barEmpty );
		resourceMap.put( "bar-full", barFull );
		resourceMap.put( "bar-disabled", barDisabled );

		systemNoneCap = wireAtlas.createSprite( "system-none-cap" );
		systemTile = wireAtlas.createSprite( "system-tile" );
		systemCap = wireAtlas.createSprite( "system-cap" );

		reactorNoneCap = wireAtlas.createSprite( "reactor-none-cap" );
		reactorFirstCap = wireAtlas.createSprite( "reactor-first-cap" );
		reactorTile = wireAtlas.createSprite( "reactor-tile" );
		reactorFirst = wireAtlas.createSprite( "reactor-first" );
		reactorCap = wireAtlas.createSprite( "reactor-cap" );

		reactorMaxBarsToShow = skin.getInt( "reactor-max-bars-to-show" );
		powerTextOffsetX = skin.getInt( "power-text-offset-x" );
		powerTextOffsetY = skin.getInt( "power-text-offset-y" );
		reactorTileAlignY = skin.getInt( "reactor-tile-align-y" );
		reactorTileOverlapOffsetY = skin.getInt( "reactor-tile-overlap-offset-y" );
		barSpacingV = skin.getInt( "bar-spacing-v" );
		barOffsetX = skin.getInt( "bar-offset-x" );
		barOffsetY = skin.getInt( "bar-offset-y" );
		connectorSystemOffsetX = skin.getInt( "connector-system-offset-x" );
		systemTileAlignX = skin.getInt( "system-tile-align-x" );
		systemTileAlignY = skin.getInt( "system-tile-align-y" );

		reactorBarWidth = skin.getInt( "reactor-bar-width" );
		reactorBarHeight = skin.getInt( "reactor-bar-height" );

		resourceMap.put( "system-bar-width", skin.getInt( "system-bar-width" ) );
		resourceMap.put( "system-bar-height", skin.getInt( "system-bar-height" ) );

		unpoweredAlpha = skin.getFloat( "unpowered-alpha" );

		barDrawable = new NinePatchDrawable();
		resourceMap.put( "bar-drawable", barDrawable );

		ShaderLabelStyle stlScrap = skin.get( "power-text-style", ShaderLabelStyle.class );
		lblPower = new ShaderLabel( "####", stlScrap );
		lblPower.setAlignment( Align.left );
		addActor( lblPower );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		final float unpowAlpha = unpoweredAlpha * parentAlpha;
		final float barCountAlpha = barCount > 0 ? parentAlpha : unpowAlpha;
		final int powerDiff = Math.min( barMax, reactorMaxBarsToShow ) - barDisabledCount;
		float x = getX(), y = getY();

		// Draw power lines
		connector.setPosition( x, y );
		connector.draw( batch, barCountAlpha );
		y += connector.getHeight();

		if ( barMax == 0 ) {
			reactorNoneCap.setPosition( x, y + reactorTileOverlapOffsetY );
			reactorNoneCap.draw( batch, unpowAlpha );
		}
		else if ( barMax == 1 ) {
			x += ( connector.getWidth() - reactorFirstCap.getWidth() ) * 2;

			reactorFirstCap.setPosition( x, y + reactorTileOverlapOffsetY );
			reactorFirstCap.draw( batch, barCount <= barMax ? parentAlpha : unpowAlpha );

			x = getX();
		}
		else {
			x += ( connector.getWidth() - reactorTile.getWidth() ) * 2;

			reactorFirst.setPosition( x, y );
			reactorFirst.draw( batch, barCount >= 1 ? parentAlpha : unpowAlpha );

			y += reactorTileOverlapOffsetY;
			// Draw the middle reactor tiles (first & last handled separately)
			for ( int i = 0; i < barMax - 2 && i < reactorMaxBarsToShow - 2; ++i ) {
				reactorTile.setPosition( x, y );
				reactorTile.draw( batch, powerDiff >= i + 2 && barCount >= i + 2 ? parentAlpha : unpowAlpha );
				y += reactorTileAlignY;
			}

			reactorCap.setPosition( x, y );
			reactorCap.draw( batch, barDisabledCount == 0 &&
					( barCount == barMax || barCount >= reactorMaxBarsToShow ) ? parentAlpha : unpowAlpha );

			x = getX();
			y = getY() + connector.getHeight();
		}

		// Draw power bars
		x += barOffsetX;
		y += reactorBarHeight;
		barDrawable.setPatch( barFull );
		for ( int i = 0; i < barMax && i < reactorMaxBarsToShow; ++i ) {
			if ( i >= powerDiff ) {
				if ( barDrawable.getPatch() != barDisabled )
					barDrawable.setPatch( barDisabled );
			}
			else if ( i >= barCount && barDrawable.getPatch() != barEmpty )
				barDrawable.setPatch( barEmpty );
			barDrawable.draw( batch, x - reactorBarWidth, y + barOffsetY, reactorBarWidth, reactorBarHeight );

			y += barSpacingV;
		}

		// Draw system wires
		x = getX() + connector.getWidth();
		y = getY();
		if ( systemOffsetsList.size() == 0 ) {
			systemNoneCap.setPosition( x, y );
			systemNoneCap.draw( batch, barCountAlpha );
		}
		else {
			wireTile.setPosition( x, y );
			wireTile.draw( batch, barCountAlpha );
			x += wireTile.getWidth();

			if ( systemOffsetsList.size() > 0 ) {
				systemCap.setPosition( x, y );
				systemCap.draw( batch, barCountAlpha );
			}

			x = getX() + connector.getWidth() + connectorSystemOffsetX;
			for ( int i = 0; i < systemOffsetsList.size() - 1; ++i ) {
				float offset = systemOffsetsList.get( i ).x;
				float width = systemOffsetsList.get( i ).y;
				x += offset;
				systemTile.setPosition( x + systemTileAlignX, y + systemTileAlignY );
				systemTile.draw( batch, barCountAlpha );
				x += width;
			}
		}

		super.draw( batch, parentAlpha );
	}

	public void setShipModel( OverdriveContext context, int shipModelRefId ) {
		this.shipModelRefId = shipModelRefId;
		updateInfo( context );
	}

	@Override
	public void gamePlayerShipChanged( OverdriveContext context, GamePlayerShipChangeEvent e ) {
		if ( e.getPlayerRefId() != context.getNetManager().getLocalPlayerRefId() ) {
			return;
		}

		setShipModel( context, e.getShipRefId() );
	}

	@Override
	public void systemPropertyChanged( OverdriveContext context, SystemPropertyEvent e ) {
		for ( Actor a : getChildren() ) {
			if ( a instanceof SystemPropertyListener ) {
				( (SystemPropertyListener)a ).systemPropertyChanged( context, e );
			}
		}
	}

	@Override
	public void shipPropertyChanged( OverdriveContext context, ShipPropertyEvent e ) {
		if ( e.getModelRefId() != shipModelRefId ) return;

		if ( e.getPropertyType() == PropertyEvent.INT_TYPE ) {
			if ( OVDConstants.POWER.equals( e.getPropertyKey() ) ) {
				updateInfo( context );
			}
			if ( OVDConstants.POWER_MAX.equals( e.getPropertyKey() ) ) {
				updateInfo( context );
			}
		}
	}

	private void updateInfo( OverdriveContext context ) {
		if ( shipModelRefId == -1 ) {
			barDisabledCount = 0;
			barCount = 0;
			barMax = 0;

			systemOffsetsList.clear();

			lblPower.setVisible( false );
			
			for ( Actor a : getChildren() ) {
				if ( a instanceof SystemActor )
					a.remove();
			}
		}
		else {
			ShipModel shipModel = context.getReferenceManager().getObject( shipModelRefId, ShipModel.class );
			barDisabledCount = shipModel.getProperties().getInt( OVDConstants.POWER_IONED );
			barCount = shipModel.getProperties().getInt( OVDConstants.POWER );
			barMax = shipModel.getProperties().getInt( OVDConstants.POWER_MAX );

			if ( barMax > reactorMaxBarsToShow ) {
				lblPower.setText( barCount + " / " + barMax );
				if ( barDisabledCount > 0 )
					lblPower.setText( lblPower.getText() + " - " + barDisabledCount );
				float y = connector.getHeight() + reactorMaxBarsToShow * reactorTileAlignY;
				lblPower.setPosition( powerTextOffsetX, y + powerTextOffsetY );
				lblPower.setVisible( true );
			}
			else {
				lblPower.setVisible( false );
			}

			int systemWireWidth = connectorSystemOffsetX;
			Keys it = shipModel.getLayout().systemRefIds();
			while ( it.hasNext ) {
				int systemRefId = it.next();
				// TODO: Reuse actors?
				SystemActor sysActor = new SystemActor( context, resourceMap );
				addActor( sysActor );
				sysActor.setModelRefId( systemRefId );

				systemOffsetsList.add( new Vector2( sysActor.getOffsetX(), sysActor.getWidth() ) );
				if ( it.hasNext ) {
					sysActor.setPosition( sysActor.getOffsetX() + systemWireWidth, sysActor.getOffsetY() );
					systemWireWidth += sysActor.getWidth();
				}
				else {
					sysActor.setPosition( sysActor.getOffsetX() + systemWireWidth - systemTileAlignX, sysActor.getOffsetY() );
				}
			}

			wireTile.setSize( systemWireWidth, wireTile.getHeight() );
		}
	}

	@Override
	public void dispose() {
		assetManager.unload( SKIN_PATH );
		assetManager.unload( ATLAS_PATH );
	}
}
