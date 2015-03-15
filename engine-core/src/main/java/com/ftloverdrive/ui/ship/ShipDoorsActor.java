package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.TickListener;
import com.ftloverdrive.event.local.LocalActorClickedListener;
import com.ftloverdrive.event.ship.DoorPropertyListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.util.OVDConstants;

public class ShipDoorsActor extends Group
		implements Disposable, ShipPropertyListener {
	protected AssetManager assetManager;

	protected float tileSize;
	private TextureRegion floorTileRegion;

	protected int shipModelRefId = -1;

	public ShipDoorsActor( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( OVDConstants.FLOORPLAN_ATLAS, TextureAtlas.class );
		assetManager.finishLoading();

		TextureAtlas floorAtlas = assetManager.get( OVDConstants.FLOORPLAN_ATLAS, TextureAtlas.class );
		floorTileRegion = floorAtlas.findRegion( "floor-tile" );
	}

	/**
	 * Sets a new tile size (default: 35).
	 *
	 * The clear() method should be called first, if tiles have been added.
	 */
	public void setTileSize( float n ) {
		tileSize = n;
	}

	public void setShipModel( OverdriveContext context, int shipModelRefId ) {
		this.shipModelRefId = shipModelRefId;
		updateDoorsInfo( context );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		super.draw( batch, parentAlpha );
	}

	protected float calcTileX( ShipCoordinate coord ) {
		// TODO: use the actor's frame size
		float frameSize = tileSize;
		if ( coord.v == ShipCoordinate.TYPE_DOOR_H )
			return coord.x * tileSize + frameSize;
		else if ( coord.v == ShipCoordinate.TYPE_DOOR_V )
			return Math.round( coord.x * tileSize - frameSize / 2 );
		else
			return 0;
	}

	protected float calcTileY( ShipCoordinate coord ) {
		// TODO: use the actor's frame size
		float frameSize = tileSize;
		if ( coord.v == ShipCoordinate.TYPE_DOOR_H )
			return Math.round( getHeight() - ( coord.y * tileSize ) + frameSize / 2 );
		else if ( coord.v == ShipCoordinate.TYPE_DOOR_V )
			return getHeight() - ( coord.y * tileSize );
		else
			return 0;
	}

	/**
	 * Adds a tile to represent a ShipCoordinate.
	 */
	public void addTile( OverdriveContext context, ShipCoordinate coord, int doorRefId ) {
		if ( coord.v != ShipCoordinate.TYPE_DOOR_H && coord.v != ShipCoordinate.TYPE_DOOR_V ) {
			return;
		}

		boolean horizontal = coord.v == ShipCoordinate.TYPE_DOOR_H;

		DoorActor doorActor = new DoorActor( context, floorTileRegion, horizontal );
		doorActor.setModelRefId( doorRefId );
		doorActor.setPosition( calcTileX( coord ), calcTileY( coord ) );
		doorActor.setRotation( horizontal ? 90 : 0 );
		doorActor.setAppearanceClosed();
		addActor( doorActor );

		context.getScreenEventManager().addEventListener( doorActor, LocalActorClickedListener.class );
		context.getScreenEventManager().addEventListener( doorActor, DoorPropertyListener.class );
		context.getScreenEventManager().addEventListener( doorActor, TickListener.class );
	}

	public void shipPropertyChanged( OverdriveContext context, ShipPropertyEvent e ) {
		if ( e.getShipRefId() != shipModelRefId ) return;

		if ( e.getPropertyType() == ShipPropertyEvent.INT_TYPE ) {
			if ( OVDConstants.DOOR_LEVEL.equals( e.getPropertyKey() ) ) {
				updateDoorsInfo( context );
			}
		}
	}

	/**
	 * Updates everything to match the current DoorModel.
	 */
	private void updateDoorsInfo( OverdriveContext context ) {
		if ( shipModelRefId == -1 ) {
		}
		else {
			ShipModel shipModel = context.getReferenceManager().getObject( shipModelRefId, ShipModel.class );
			int doorUpgradeLevel = shipModel.getProperties().getInt( OVDConstants.DOOR_LEVEL );

			// TODO: Interpret the door level into an animSpec, eg.
			// SystemModel system = shipModel.getSystemByType( OVDConstants.SYSTEM_DOOR );
			// AnimSpec animSpec = (AnimSpec) system.interpretIntValue( doorUpgradeLevel, OVDConstants.DOOR_SYSTEM_LEVEL );
			
			for ( IntMap.Keys it = shipModel.getLayout().doorRefIds(); it.hasNext; ) {
				//int doorRefId = it.next();
				//DoorModel doorModel = context.getReferenceManager().getObject( doorRefId, DoorModel.class );
				//doorModel.setAnimSpec(  );
			}
		}
	}

	public void dispose() {
		assetManager.unload( OVDConstants.FLOORPLAN_ATLAS );
		for ( Actor actor : getChildren() ) {
			if ( actor instanceof Disposable )
				((Disposable) actor).dispose();
		}
		// TODO ?
	}
}
