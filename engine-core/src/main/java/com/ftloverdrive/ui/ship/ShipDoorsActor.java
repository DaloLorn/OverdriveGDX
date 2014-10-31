package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.DoorPropertyListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.model.ship.DoorModel;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.util.OVDConstants;

public class ShipDoorsActor extends Group implements Disposable, ShipPropertyListener {
	protected AssetManager assetManager;

	protected float tileSize = 35;

	protected int shipModelRefId = -1;

	public ShipDoorsActor( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();
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
		if ( coord.v != ShipCoordinate.TYPE_DOOR_H && coord.v != ShipCoordinate.TYPE_DOOR_V )
			return;

		DoorActor doorActor = new DoorActor( context );
		doorActor.setDoorModel( context, doorRefId );
		doorActor.setPosition( calcTileX( coord ), calcTileY( coord ) );
		doorActor.setSize( 35, 35 );
		doorActor.setRotation( coord.v == ShipCoordinate.TYPE_DOOR_H ? 90 : 0 );
		doorActor.setStateClosed();
		addActor( doorActor );

		context.getScreenEventManager().addEventListener( doorActor, DoorPropertyListener.class );
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

			// TODO: Interpret the door level into an imageSpec, eg.
			// TODO: Need AnimSpec class or something
			// SystemModel system = shipModel.getSystemByType( OVDConstants.SYSTEM_DOOR );
			// ImageSpec imgSpec = (ImageSpec) system.interpretIntValue( doorUpgradeLevel, OVDConstants.DOOR_SYSTEM_LEVEL );
			
			for ( IntMap.Keys it = shipModel.getLayout().doorRefIds(); it.hasNext; ) {
				int doorRefId = it.next();
				DoorModel doorModel = context.getReferenceManager().getObject( doorRefId, DoorModel.class );
				//doorModel.setAnimSpec(  );
			}
		}
	}

	public void dispose() {
		// TODO ?
	}
}
