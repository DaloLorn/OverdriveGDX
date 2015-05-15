package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.IntMap.Keys;
import com.ftloverdrive.blueprint.ship.ShieldSystemBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.engine.TickEvent;
import com.ftloverdrive.event.engine.TickListener;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.local.LocalActorClickedListener;
import com.ftloverdrive.event.player.OrderListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyListener;
import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.ship.RoomModel;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.model.system.SystemModel;
import com.ftloverdrive.ui.ModelActor;
import com.ftloverdrive.util.OVDConstants;


public class ShipActor extends ModelActor
		implements Disposable, GamePlayerShipChangeListener, ShipPropertyListener, SystemPropertyListener, TickListener {

	protected AssetManager assetManager;

	protected Group shipFudgeGroup;
	protected Group shipHullGroup;
	protected Group shipFloorplanGroup;

	protected SpriteDrawable nullDrawable;
	protected Image shieldImage;
	protected Image baseImage;
	protected Image cloakImage;
	protected Image floorImage;

	protected ShipFloorTilesActor floorTiles;
	protected ShipFloorLinesActor floorLines;
	protected ShipRoomDecorsActor roomDecors;
	protected ShipWallLinesActor wallLines;
	protected ShipDoorsActor doorGroup;
	protected ShipSystemIconsActor systemIcons;
	protected Group tpadGroup;
	protected Group crewGroup;

	protected ImageSpec shieldImgSpec = null;
	protected ImageSpec baseImgSpec = null;
	protected ImageSpec cloakImgSpec = null;
	protected ImageSpec floorImgSpec = null;


	public ShipActor( OverdriveContext context ) {
		super( context );
		assetManager = context.getAssetManager();

		assetManager.load( OVDConstants.ROOT_ATLAS, TextureAtlas.class );
		assetManager.load( OVDConstants.SHIP_ATLAS, TextureAtlas.class );
		assetManager.finishLoading();

		TextureAtlas rootAtlas = assetManager.get( OVDConstants.ROOT_ATLAS, TextureAtlas.class );
		Sprite nullSprite = rootAtlas.createSprite( "nullResource" );
		nullDrawable = new SpriteDrawable( nullSprite );

		shipFudgeGroup = new Group();
		shipFudgeGroup.setName( "ShipFudgeGroup" );
		this.addActor( shipFudgeGroup );

		shipHullGroup = new Group();
		shipHullGroup.setName( "ShipHullGroup" );
		shipFudgeGroup.addActor( shipHullGroup );

		shipFloorplanGroup = new Group();
		shipFloorplanGroup.setName( "ShipFloorplanGroup" );
		shipFudgeGroup.addActor( shipFloorplanGroup );

		// Shield is not affected by hull offset, but is drawn below it.
		shieldImage = new Image( nullDrawable );
		shipFudgeGroup.addActor( shieldImage );
		shieldImage.toBack();

		baseImage = new Image( nullDrawable );
		shipHullGroup.addActor( baseImage );

		cloakImage = new Image( nullDrawable );
		cloakImage.setVisible( false );
		shipHullGroup.addActor( cloakImage );

		floorImage = new Image( nullDrawable );
		shipHullGroup.addActor( floorImage );

		floorTiles = new ShipFloorTilesActor( context );
		floorTiles.setTouchable( Touchable.childrenOnly );
		context.getScreenEventManager().addEventListener( floorTiles, LocalActorClickedListener.class );
		shipFloorplanGroup.addActor( floorTiles );

		floorLines = new ShipFloorLinesActor( context );
		floorLines.setTouchable( Touchable.disabled );
		shipFloorplanGroup.addActor( floorLines );

		tpadGroup = new Group();
		tpadGroup.setTouchable( Touchable.childrenOnly );
		shipFloorplanGroup.addActor( tpadGroup );

		roomDecors = new ShipRoomDecorsActor( context );
		roomDecors.setTouchable( Touchable.disabled );
		shipFloorplanGroup.addActor( roomDecors );

		wallLines = new ShipWallLinesActor( context );
		wallLines.setTouchable( Touchable.disabled );
		shipFloorplanGroup.addActor( wallLines );

		doorGroup = new ShipDoorsActor( context );
		doorGroup.setTouchable( Touchable.childrenOnly );
		shipFloorplanGroup.addActor( doorGroup );

		// TODO: Where to draw crew layer?
		// Needs to be drawn above door layer for sure
		crewGroup = new Group();
		crewGroup.setTouchable( Touchable.childrenOnly );
		shipFloorplanGroup.addActor( crewGroup );

		systemIcons = new ShipSystemIconsActor( context );
		systemIcons.setTouchable( Touchable.disabled );
		shipFloorplanGroup.addActor( systemIcons );
	}


	@Override
	public void draw( Batch batch, float parentAlpha ) {
		if ( modelRefId != -1 ) {
			super.draw( batch, parentAlpha );
		}
	}

	@Override
	public void gamePlayerShipChanged( OverdriveContext context, GamePlayerShipChangeEvent e ) {
		if ( e.getPlayerRefId() != context.getNetManager().getLocalPlayerRefId() ) {
			return;
		}

		setModelRefId( e.getShipRefId() );
		setPosition( 350, context.getScreen().getScreenHeight() - getHeight() - 170 );
	}

	@Override
	public void shipPropertyChanged( OverdriveContext context, ShipPropertyEvent e ) {
		if ( e.getModelRefId() != modelRefId ) return;

		ShipModel shipModel = context.getReferenceManager().getObject( modelRefId, ShipModel.class );
		if ( e.getPropertyType() == PropertyEvent.INT_TYPE ) {
			if ( OVDConstants.SHIELD.equals( e.getPropertyKey() ) || OVDConstants.SHIELD_MAX.equals( e.getPropertyKey() ) ) {
				if ( e.getIntValue() > 0 ) {
					int shield = shipModel.getProperties().getInt( OVDConstants.SHIELD );
					int shieldMax = shipModel.getProperties().getInt( OVDConstants.SHIELD_MAX );
					if ( shield < shieldMax ) {
						ShipPropertyEvent event = Pools.get( ShipPropertyEvent.class ).obtain();
						event.init( modelRefId, PropertyEvent.INCREMENT_ACTION, OVDConstants.SHIELD, 1 );
						context.getScreenEventManager().postDelayedEvent( event, 3 );
					}
					else {
						ShipPropertyEvent event = Pools.get( ShipPropertyEvent.class ).obtain();
						event.init( modelRefId, PropertyEvent.SET_ACTION, OVDConstants.SHIELD, shieldMax );
						context.getScreenEventManager().postDelayedEvent( event );
					}
				}
			}
		}
	}

	@Override
	public void systemPropertyChanged( OverdriveContext context, SystemPropertyEvent e ) {
		if ( OVDConstants.POWER.equals( e.getPropertyKey() ) ) {
			ShipModel shipModel = context.getReferenceManager().getObject( modelRefId, ShipModel.class );
			Keys it = shipModel.getLayout().systemRefIds();
			boolean found = false;
			while ( it.hasNext && !found )
				found = it.next() == e.getModelRefId();
			it.reset();

			if ( found ) {
				SystemModel systemModel = context.getReferenceManager().getObject( e.getModelRefId(), SystemModel.class );
				// TODO Revise this
				if ( systemModel.getProperties().getString( OVDConstants.BLUEPRINT_NAME ).equals( ShieldSystemBlueprint.class.getSimpleName() ) ) {
					int level = systemModel.getCurrentPower() / systemModel.getPowerIncrement();

					ShipPropertyEvent event = Pools.get( ShipPropertyEvent.class ).obtain();
					event.init( modelRefId, PropertyEvent.SET_ACTION, OVDConstants.SHIELD_MAX, level );
					context.getScreenEventManager().postDelayedEvent( event );
				}
			}
		}
	}


	/**
	 * Updates the everything to match the current ShipModel.
	 */
	@Override
	protected void updateInfo( OverdriveContext context ) {

		if ( modelRefId == -1 ) {
			shipFudgeGroup.setPosition( 0, 0 );
			shipHullGroup.setPosition( 0, 0 );

			shieldImgSpec = null;
			shieldImage.setDrawable( nullDrawable );
			shieldImage.setBounds( 0, 0, 0, 0 );
			shieldImage.validate();

			baseImgSpec = null;
			baseImage.setDrawable( nullDrawable );
			baseImage.setBounds( 0, 0, 0, 0 );
			baseImage.validate();

			floorImgSpec = null;
			floorImage.setDrawable( nullDrawable );
			floorImage.setBounds( 0, 0, 0, 0 );
			floorImage.validate();

			cloakImgSpec = null;
			cloakImage.setDrawable( nullDrawable );
			cloakImage.setBounds( 0, 0, 0, 0 );
			cloakImage.validate();

			floorTiles.clear();
			floorTiles.setSize( 0, 0 );

			floorLines.clear();
			floorLines.setSize( 0, 0 );

			roomDecors.clear();
			roomDecors.setSize( 0, 0 );

			wallLines.clear();
			wallLines.setSize( 0, 0 );

			doorGroup.clear();
			doorGroup.setSize( 0, 0 );

			crewGroup.clear();
			crewGroup.setSize( 0, 0 );

			systemIcons.clear();
			systemIcons.setSize( 0, 0 );

			this.setSize( 0, 0 );
		}
		else {
			ShipModel shipModel = context.getReferenceManager().getObject( modelRefId, ShipModel.class );
			TextureAtlas shipAtlas = assetManager.get( OVDConstants.SHIP_ATLAS, TextureAtlas.class );

			shipFudgeGroup.setPosition( shipModel.getShipOffsetX(), -shipModel.getShipOffsetY() );
			shipHullGroup.setPosition( shipModel.getHullOffsetX(), -shipModel.getHullOffsetY() );

			ImageSpec currentBaseImgSpec = shipModel.getBaseImageSpec();
			if ( !isEqual( baseImgSpec, currentBaseImgSpec ) ) {
				baseImgSpec = currentBaseImgSpec;

				Sprite baseSprite = shipAtlas.createSprite( baseImgSpec.getRegionName() );
				if ( baseSprite != null ) {
					baseImage.setDrawable( new SpriteDrawable( baseSprite ) );
				}
				else {
					baseImage.setDrawable( nullDrawable );
				}

				// Not relative to anything (except the ship's base group)
				baseImage.setPosition( 0, 0 );
				baseImage.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
				baseImage.validate();
			}

			ImageSpec currentShieldImgSpec = shipModel.getShieldImageSpec();
			if ( !isEqual( shieldImgSpec, currentShieldImgSpec ) ) {
				shieldImgSpec = currentShieldImgSpec;

				Sprite shieldSprite = shipAtlas.createSprite( shieldImgSpec.getRegionName() );
				if ( shieldSprite != null ) {
					shieldImage.setDrawable( new SpriteDrawable( shieldSprite ) );
				}
				else {
					shieldImage.setDrawable( nullDrawable );
				}

				Vector2 layoutSize = shipModel.getLayout().getSize();
				layoutSize.x *= 35;
				layoutSize.y *= 35;
				shieldImage.setX( layoutSize.x / 2 + shipModel.getShieldEllipseOffsetX() - shipModel.getShieldEllipseSemiMajorAxis() );
				shieldImage.setY( -shipModel.getShieldEllipseSemiMinorAxis() + baseImage.getHeight() - layoutSize.y / 2
						- shipModel.getShieldEllipseOffsetY() );
				shieldImage.setSize( shipModel.getShieldEllipseSemiMajorAxis() * 2, shipModel.getShieldEllipseSemiMinorAxis() * 2 );
				shieldImage.validate();
			}

			ImageSpec currentCloakImgSpec = shipModel.getCloakImageSpec();
			if ( !isEqual( cloakImgSpec, currentCloakImgSpec ) ) {
				cloakImgSpec = currentCloakImgSpec;

				Sprite cloakSprite = shipAtlas.createSprite( cloakImgSpec.getRegionName() );
				if ( cloakSprite != null ) {
					cloakImage.setDrawable( new SpriteDrawable( cloakSprite ) );
				}
				else {
					cloakImage.setDrawable( nullDrawable );
				}

				// Relative to base image's bottom-left corner
				// Ie. X values calculated normally, but Y needs some adjustments...
				cloakImage.setPosition( shipModel.getCloakOffsetX(),
						baseImage.getHeight() - cloakImage.getPrefHeight() - shipModel.getCloakOffsetY() );
				cloakImage.setSize( cloakImage.getPrefWidth(), cloakImage.getPrefHeight() );
				cloakImage.validate();
			}

			ImageSpec currentFloorImgSpec = shipModel.getFloorImageSpec();
			if ( !isEqual( floorImgSpec, currentFloorImgSpec ) ) {
				floorImgSpec = currentFloorImgSpec;

				Sprite floorSprite = shipAtlas.createSprite( floorImgSpec.getRegionName() );
				if ( floorSprite != null ) {
					floorImage.setDrawable( new SpriteDrawable( floorSprite ) );
				}
				else {
					floorImage.setDrawable( nullDrawable );
				}

				// Relative to base image's bottom-left corner
				// Ie. X values calculated normally, but Y needs some adjustments...
				floorImage.setPosition( shipModel.getFloorOffsetX(),
						baseImage.getHeight() - floorImage.getPrefHeight() - shipModel.getFloorOffsetY() );
				floorImage.setSize( floorImage.getPrefWidth(), floorImage.getPrefHeight() );
				floorImage.validate();
			}

			floorTiles.clear();
			floorTiles.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
			floorTiles.setTileSize( 35 );
			for ( ShipCoordinate coord : shipModel.getLayout().getAllShipCoords() ) {
				floorTiles.addTile( coord );
			}

			floorLines.clear();
			floorLines.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
			floorLines.setTileSize( 35 );
			for ( ShipCoordinate coord : shipModel.getLayout().getAllShipCoords() ) {
				floorLines.addTile( coord );
			}

			roomDecors.clear();
			roomDecors.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
			roomDecors.setTileSize( 35 );
			for ( IntMap.Keys it = shipModel.getLayout().roomRefIds(); it.hasNext; ) {
				int roomRefId = it.next();
				RoomModel roomModel = context.getReferenceManager().getObject( roomRefId, RoomModel.class );
				ImageSpec decorImageSpec = roomModel.getRoomDecorImageSpec();
				if ( decorImageSpec == null ) continue;

				roomDecors.addDecor( decorImageSpec, shipModel.getLayout().getRoomCoords( roomRefId ) );
			}

			wallLines.clear();
			wallLines.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
			wallLines.setTileSize( 35 );
			for ( ShipCoordinate coord : shipModel.getLayout().getAllShipCoords() ) {
				wallLines.addTile( coord, shipModel.getLayout().getAllShipCoords() );
			}

			doorGroup.clear();
			doorGroup.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
			doorGroup.setTileSize( 35 );
			for ( IntMap.Keys it = shipModel.getLayout().doorRefIds(); it.hasNext; ) {
				int doorRefId = it.next();
				doorGroup.addTile( context, shipModel.getLayout().getDoorCoords( doorRefId ), doorRefId );
			}

			tpadGroup.clear();
			tpadGroup.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
			for ( IntMap.Keys it = shipModel.getLayout().tpadRefIds(); it.hasNext; ) {
				int tpadRefId = it.next();
				ShipCoordinate coord = shipModel.getLayout().getTeleportPadCoords( tpadRefId );

				TeleportPadActor tpadActor = new TeleportPadActor( context );
				tpadActor.setModelRefId( tpadRefId );

				tpadGroup.addActor( tpadActor );
				tpadActor.setPosition( ( coord.x + 0.5f ) * 35 - 1,
						tpadGroup.getHeight() - ( coord.y - 0.5f ) * 35 - 1, Align.center );

				context.getScreenEventManager().addEventListener( tpadActor, LocalActorClickedListener.class );
			}

			crewGroup.clear();
			crewGroup.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
			for ( IntMap.Keys it = shipModel.getLayout().crewRefIds(); it.hasNext; ) {
				int crewRefId = it.next();
				ShipCoordinate coord = shipModel.getLayout().getCrewCoords( crewRefId );

				CrewActor crewActor = new CrewActor( context );
				crewActor.setModelRefId( crewRefId );

				crewGroup.addActor( crewActor );
				crewActor.setPosition( coord.x * 35, crewGroup.getHeight() - coord.y * 35 );

				context.getScreenEventManager().addEventListener( crewActor, LocalActorClickedListener.class );
				context.getScreenEventManager().addEventListener( crewActor, OrderListener.class );
			}

			systemIcons.clear();
			systemIcons.setSize( shipModel.getHullWidth(), shipModel.getHullHeight() );
			systemIcons.setTileSize( 35 );
			for ( IntMap.Keys it = shipModel.getLayout().systemRefIds(); it.hasNext; ) {
				int systemRefId = it.next();
				SystemModel systemModel = context.getReferenceManager().getObject( systemRefId, SystemModel.class );
				String iconName = systemModel.getIconName();
				if ( iconName == null ) continue;

				Vector2 iconOffset = shipModel.getLayout().getRoomSystemIcon( systemRefId );
				systemIcons.addSystemIcon( iconOffset.x, iconOffset.y, iconName );
			}
		}
	}

	@Override
	public float getHeight() {
		return baseImgSpec == null ? 0 : baseImage.getHeight();
	}

	protected boolean isEqual( Object a, Object b ) {
		if ( a != null ) return a.equals( b );
		if ( b != null ) return b.equals( a );
		return true; // Both null.
	}

	// Actors don't normally have a dispose().
	public void dispose() {
		floorTiles.dispose();
		roomDecors.dispose();
		assetManager.unload( OVDConstants.SHIP_ATLAS );
		assetManager.unload( OVDConstants.ROOT_ATLAS );
	}

	@Override
	public void ticksAccumulated( TickEvent e ) {
	}
}
