package com.ftloverdrive.io;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ftloverdrive.core.OverdriveContext;

/**
 * An ImageSpec that describes which region of the image to use for animation.
 */
public class AnimSpec extends ImageSpec {
	protected int frameWidth = -1;
	protected int frameHeight = -1;
	protected int frameCount = -1;
	protected int xOffset = -1;
	protected int yOffset = -1;
	protected float time = -1;


	/**
	 * Creates a new AnimSpec.
	 * 
	 * @param atlasPath path to the atlas containing the animation sheet
	 * @param regionName region name of the animation sheet
	 * @param fw width of a single frame in the animation
	 * @param fh height of a single frame in the animation
	 * @param fc number of frames in the animation
	 * @param x how many frames to skip horizontally
	 * @param y how many frames to skip vertically
	 * @param time the total length of the animation
	 */
	public AnimSpec( String atlasPath, String regionName, int fw, int fh, int fc, int x, int y, float time ) {
		super( atlasPath, regionName );
		frameWidth = fw;
		frameHeight = fh;
		frameCount = fc;
		xOffset = x;
		yOffset = y;
		this.time = time;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public int getFrameCount() {
		return frameCount;
	}

	/**
	 * Returns the number of frames to skip horizontally.
	 */
	public int getXOffset() {
		return xOffset;
	}

	/**
	 * Returns the number of frames to skip vertically.
	 */
	public int getYOffset() {
		return yOffset;
	}

	/**
	 * Creates and returns a new Animation constructed using the data contained in this AnimSpec.
	 * 
	 * Note that this method does not load the resources in the AssetManager by itself.
	 */
	public Animation create( OverdriveContext context ) {
		TextureAtlas atlas = context.getAssetManager().get( atlasPath, TextureAtlas.class );
		TextureRegion region = atlas.findRegion( regionName ); 
		// FTL's animations.xml counts 0-based rows from the bottom.
		TextureRegion[][] tmpFrames = region.split( frameWidth, frameHeight );
		TextureRegion[] frames = new TextureRegion[frameCount];
		for ( int i=0; i < frames.length; i++ )
			frames[i] = tmpFrames[0][i];

		return new Animation( time / frameCount, frames );
	}

	@Override
	public boolean equals( Object o ) {
		if ( o instanceof AnimSpec == false ) return false;
		AnimSpec other = (AnimSpec)o;

		if ( !super.equals( other ) ) return false;

		if ( frameWidth != other.frameWidth || frameHeight != other.frameHeight ||
				frameCount != other.frameCount ||
				xOffset != other.xOffset || yOffset != other.yOffset )
			return false;

		return true;
	}
}
