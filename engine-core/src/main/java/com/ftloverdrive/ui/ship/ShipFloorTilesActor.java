package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.command.CrewMoveCommand;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.local.LocalActorClickedEvent;
import com.ftloverdrive.event.local.LocalActorClickedListener;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.ui.LocalActor;
import com.ftloverdrive.util.OVDConstants;


/**
 * All the floor tiles on a ship (but no lines).
 *
 * After construction, set the height, then call addTile() for all
 * ShipCoordinates.
 */
public class ShipFloorTilesActor extends LocalActor
		implements Disposable, EventListener, LocalActorClickedListener {

	protected float tileSize = 35;

	protected AssetManager assetManager;


	public ShipFloorTilesActor( OverdriveContext context ) {
		super( context );
		assetManager = context.getAssetManager();

		assetManager.load( OVDConstants.FLOORPLAN_ATLAS, TextureAtlas.class );
		assetManager.finishLoading();
	}


	/**
	 * Sets a new tile size (default: 35).
	 *
	 * The clear() method should be called first, if tiles have been added.
	 */
	public void setTileSize( float n ) {
		tileSize = n;
	}


	@Override
	public void draw( Batch batch, float parentAlpha ) {
		super.draw( batch, parentAlpha );
	}


	protected float calcTileX( ShipCoordinate coord ) {
		return ( coord.x * tileSize );
	}

	protected float calcTileY( ShipCoordinate coord ) {
		return ( this.getHeight() - ( coord.y * tileSize ) );
	}

	protected ShipCoordinate calcCoord( float x, float y ) {
		ShipCoordinate coord = Pools.get( ShipCoordinate.class ).obtain();
		coord.init( (int)( x / tileSize ), (int)( ( this.getHeight() - y ) / tileSize ), ShipCoordinate.TYPE_SQUARE );

		return coord;
	}

	/**
	 * Adds a tile to represent a ShipCoordinate.
	 */
	public void addTile( ShipCoordinate coord ) {
		if ( coord.v != ShipCoordinate.TYPE_SQUARE ) return;

		TextureAtlas floorAtlas = assetManager.get( OVDConstants.FLOORPLAN_ATLAS, TextureAtlas.class );

		TextureRegion tileRegion = floorAtlas.findRegion( "floor-tile" );
		NinePatchDrawable tileDrawable = new NinePatchDrawable( new NinePatch( tileRegion, 1, 1, 1, 1 ) );
		Image tileImage = new Image( tileDrawable );
		tileImage.setPosition( calcTileX( coord ), calcTileY( coord ) );
		tileImage.setSize( tileSize, tileSize );
		this.addActor( tileImage );

		tileImage.addListener( this );

		// These are different floats which can cause gaps when mixed.
		// (x * size + size) != ((x+1) * size)
	}


	@Override
	public void dispose() {
		assetManager.unload( OVDConstants.FLOORPLAN_ATLAS );
	}

	@Override
	public void actorClicked( OverdriveContext context, LocalActorClickedEvent e ) {
		if ( e.getTarget() instanceof Image ) {
			Actor target = e.getTarget();

			if ( e.getButton() == Buttons.RIGHT ) {
				ShipCoordinate coord = calcCoord( target.getX(), target.getY() );

				// Issue a move command for the local player's current selection
				// At the moment it affects any Actor that implements OrderListener
				// TODO: Local player's crew selection
				// TODO: Move command should be able to tell which crew members it affects
				// (so that it can be reused for other cases, eg drones)
				CrewMoveCommand command = Pools.get( CrewMoveCommand.class ).obtain();
				command.setSourcePlayer( context.getNetManager().getLocalPlayerRefId() );
				command.setTargetCoord( coord );
				command.execute( context );
			}
		}
	}
}
