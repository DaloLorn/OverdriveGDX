package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.ship.ShipCoordinate;

public class ShipDoorsActor extends Group implements Disposable {
	protected AssetManager assetManager;

	protected float tileSize = 35;

	protected Array<DoorActor> tiles;


	public ShipDoorsActor( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		tiles = new Array<DoorActor>();
	}

	/**
	 * Sets a new tile size (default: 35).
	 *
	 * The clear() method should be called first, if tiles have been added.
	 */
	public void setTileSize( float n ) {
		tileSize = n;
	}

	/**
	 * Removes all floor tiles.
	 */
	public void clear() {
		tiles.clear();
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		super.draw( batch, parentAlpha );
	}

	protected float calcTileX( ShipCoordinate coord ) {
		int wallThickness = 2;
		if ( coord.v == ShipCoordinate.TYPE_DOOR_H )
			return coord.x * tileSize + tileSize;
		else if ( coord.v == ShipCoordinate.TYPE_DOOR_V )
			return coord.x * tileSize - tileSize / 2 + wallThickness / 2;
		else
			return 0;
	}

	protected float calcTileY( ShipCoordinate coord ) {
		if ( coord.v == ShipCoordinate.TYPE_DOOR_H )
			return getHeight() - ( coord.y * tileSize ) + tileSize / 2;
		else if ( coord.v == ShipCoordinate.TYPE_DOOR_V )
			return getHeight() - ( coord.y * tileSize );
		else
			return 0;
	}

	/**
	 * Adds a tile to represent a ShipCoordinate.
	 */
	public void addTile( OverdriveContext context, ShipCoordinate coord, int doorRefId ) {
		if ( coord.v != ShipCoordinate.TYPE_DOOR_H &&
				coord.v != ShipCoordinate.TYPE_DOOR_V )
			return;

		DoorActor doorActor = new DoorActor( assetManager );
		doorActor.setDoorModel( context, doorRefId );
		doorActor.setPosition( calcTileX( coord ), calcTileY( coord ) );
		doorActor.setSize( 35, 35 );
		doorActor.setRotation( coord.v == ShipCoordinate.TYPE_DOOR_H ? 90 : 0 );
		doorActor.setStateClosed();
		addActor( doorActor );
	}

	@Override
	public void dispose() {
		// TODO ?
	}
}
