package com.ftloverdrive.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;


/**
 * A NinePatch that repeats the non-corner patches, instead of stretching them.
 */
public class TiledNinePatchDrawable extends BaseDrawable {

	private static final int TOP_LEFT = 0;
	private static final int TOP_RIGHT = 1;
	private static final int BOTTOM_LEFT = 2;
	private static final int BOTTOM_RIGHT = 3;

	private static final int MIDDLE_CENTER = 0;
	private static final int TOP_CENTER = 1;
	private static final int MIDDLE_LEFT = 2;
	private static final int MIDDLE_RIGHT = 3;
	private static final int BOTTOM_CENTER = 4;

	private TextureRegion[] corners = null;
	private TiledDrawable[] bodies = null;
	private int middleWidth = 0;
	private int middleHeight = 0;


	protected TiledNinePatchDrawable() {
	}

	public TiledNinePatchDrawable( TextureRegion region, float left, float right, float top, float bottom ) {
		setRegion( region, left, right, top, bottom );
	}

	public void setRegion( TextureRegion region, float left, float right, float top, float bottom ) {
		setRegion( region, (int)left, (int)right, (int)top, (int)bottom );
	}

	public void setRegion( TextureRegion region, int left, int right, int top, int bottom ) {
		if ( region == null ) throw new IllegalArgumentException( "region cannot be null." );
		middleWidth = region.getRegionWidth() - left - right;
		middleHeight = region.getRegionHeight() - top - bottom;

		corners = new TextureRegion[4];
		bodies = new TiledDrawable[5];
		if ( top > 0 ) {
			if ( left > 0 )
				corners[TOP_LEFT] = new TextureRegion( region, 0, 0, left, top );
			if ( middleWidth > 0 )
				bodies[TOP_CENTER] = new TiledDrawable( new TextureRegion( region, left, 0, middleWidth, top ) );
			if ( right > 0 )
				corners[TOP_RIGHT] = new TextureRegion( region, left + middleWidth, 0, right, top );
		}
		if ( middleHeight > 0 ) {
			if ( left > 0 )
				bodies[MIDDLE_LEFT] = new TiledDrawable( new TextureRegion( region, 0, top, left, middleHeight ) );
			if ( middleWidth > 0 )
				bodies[MIDDLE_CENTER] = new TiledDrawable( new TextureRegion( region, left, top, middleWidth, middleHeight ) );
			if ( right > 0 )
				bodies[MIDDLE_RIGHT] = new TiledDrawable( new TextureRegion( region, left + middleWidth, top, right, middleHeight ) );
		}
		if ( bottom > 0 ) {
			if ( left > 0 )
				corners[BOTTOM_LEFT] = new TextureRegion( region, 0, top + middleHeight, left, bottom );
			if ( middleWidth > 0 )
				bodies[BOTTOM_CENTER] = new TiledDrawable( new TextureRegion( region, left, top + middleHeight, middleWidth, bottom ) );
			if ( right > 0 )
				corners[BOTTOM_RIGHT] = new TextureRegion( region, left + middleWidth, top + middleHeight, right, bottom );
		}

		setMinWidth( region.getRegionWidth() );
		setMinHeight( region.getRegionHeight() );
		setTopHeight( top );
		setRightWidth( right );
		setBottomHeight( bottom );
		setLeftWidth( left );
	}

	public int getTileWidth() {
		return middleWidth;
	}

	public int getTileHeight() {
		return middleHeight;
	}

	public float computeTiledWidth( float w ) {
		float bw = w - ( getLeftWidth() + getRightWidth() );
		if ( bw % middleWidth != 0 )
			w += middleWidth - bw % middleWidth;
		return w;
	}

	public float computeTiledHeight( float h ) {
		float bh = h - ( getTopHeight() + getBottomHeight() );
		if ( bh % middleHeight != 0 )
			h += middleHeight - bh % middleHeight;
		return h;
	}

	public void draw( Batch batch, float x, float y, float width, float height ) {
		final float centerColumnX = x + getLeftWidth();
		final float rightColumnX = x + width - getRightWidth();
		final float middleRowY = y + getBottomHeight();
		final float topRowY = y + height - getTopHeight();

		// TODO: Can possibly optimize into single draw call, like NinePatch already does?

		// Draw corners.
		if ( corners[TOP_LEFT] != null )
			batch.draw( corners[TOP_LEFT], x, topRowY, getLeftWidth(), getTopHeight() );
		if ( corners[TOP_RIGHT] != null )
			batch.draw( corners[TOP_RIGHT], rightColumnX, topRowY, getRightWidth(), getTopHeight() );
		if ( corners[BOTTOM_LEFT] != null )
			batch.draw( corners[BOTTOM_LEFT], x, y, getLeftWidth(), getBottomHeight() );
		if ( corners[BOTTOM_RIGHT] != null )
			batch.draw( corners[BOTTOM_RIGHT], rightColumnX, y, getRightWidth(), getBottomHeight() );

		// Draw tiled bodies.
		if ( bodies[MIDDLE_CENTER] != null )
			bodies[MIDDLE_CENTER].draw( batch, centerColumnX, middleRowY,
					width - getLeftWidth() - getRightWidth(), height - getTopHeight() - getBottomHeight() );
		if ( bodies[TOP_CENTER] != null )
			bodies[TOP_CENTER].draw( batch, centerColumnX, topRowY,
					width - getLeftWidth() - getRightWidth(), getTopHeight() );
		if ( bodies[MIDDLE_LEFT] != null )
			bodies[MIDDLE_LEFT].draw( batch, x, middleRowY,
					getLeftWidth(), height - getTopHeight() - getBottomHeight() );
		if ( bodies[MIDDLE_RIGHT] != null )
			bodies[MIDDLE_RIGHT].draw( batch, rightColumnX, middleRowY,
					getRightWidth(), height - getTopHeight() - getBottomHeight() );
		if ( bodies[BOTTOM_CENTER] != null )
			bodies[BOTTOM_CENTER].draw( batch, centerColumnX, y,
					width - getLeftWidth() - getRightWidth(), getBottomHeight() );
	}
}
