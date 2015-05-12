package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.util.OVDConstants;


/**
 * TODO: Add referencing of icons so that they can be tinted / removed / etc
 */
public class ShipSystemIconsActor extends Group
		implements Disposable {

	protected float tileSize = 35;
	protected AssetManager assetManager;


	public ShipSystemIconsActor( OverdriveContext context ) {
		assetManager = context.getAssetManager();

		assetManager.load( OVDConstants.ICONS_ATLAS, TextureAtlas.class );
		assetManager.finishLoading();
	}

	/**
	 * Sets a new tile size (default: 35).
	 *
	 * The clear() method should be called first, if icons have been added.
	 */
	public void setTileSize( float n ) {
		tileSize = n;
	}

	protected float calcTileX( int x ) {
		return x * tileSize;
	}

	protected float calcTileY( int y ) {
		return getHeight() - ( y * tileSize );
	}

	/**
	 * @param x
	 *            x offset from the coordinate's top-left corner
	 * @param y
	 *            y offset from the coordinate's top-left corner
	 * @param iconName
	 *            name of the texture region in the icons atlas
	 */
	public void addSystemIcon( float x, float y, String iconName ) {
		TextureAtlas iconAtlas = assetManager.get( OVDConstants.ICONS_ATLAS, TextureAtlas.class );
		TextureRegion iconRegion = iconAtlas.findRegion( iconName + "-overlay" );

		Image iconImage = new Image( iconRegion );
		int xCoord = (int)x;
		int yCoord = (int)y;
		x -= xCoord;
		y -= yCoord;
		iconImage.setPosition( calcTileX( xCoord ) + x, calcTileY( yCoord ) + y );
		addActor( iconImage );
	}

	@Override
	public void dispose() {
		assetManager.unload( OVDConstants.ICONS_ATLAS );
	}
}
