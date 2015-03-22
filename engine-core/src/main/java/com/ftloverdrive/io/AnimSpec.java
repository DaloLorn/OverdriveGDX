package com.ftloverdrive.io;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ftloverdrive.core.OverdriveContext;


/**
 * An ImageSpec that describes which region of the image to use for animation.
 * 
 * TODO: Invert the inheritance hierarchy, so that an ImageSpec is just a single-framed AnimSpec?
 * Would allow modders to use animations for things that normally have static images...
 */
public class AnimSpec extends ImageSpec {

	protected int frameWidth = -1;
	protected int frameHeight = -1;
	protected int frameCount = -1;
	protected int xOffset = -1;
	protected int yOffset = -1;
	protected float interval = -1;


	protected AnimSpec() {
	}

	/**
	 * Creates a new AnimSpec.
	 * 
	 * @param atlasPath
	 *            path to the atlas containing the animation sheet
	 * @param regionName
	 *            region name of the animation sheet
	 * @param fw
	 *            width of a single frame in the animation
	 * @param fh
	 *            height of a single frame in the animation
	 * @param fc
	 *            number of frames in the animation
	 * @param x
	 *            how many frames to skip horizontally
	 * @param y
	 *            how many frames to skip vertically
	 * @param time
	 *            the total length of the animation
	 */
	public AnimSpec( String atlasPath, String regionName, int fw, int fh, int fc, int x, int y, float time ) {
		super( atlasPath, regionName );
		frameWidth = fw;
		frameHeight = fh;
		frameCount = fc;
		xOffset = x;
		yOffset = y;
		interval = time / frameCount;
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
		for ( int i = 0, j = yOffset, k = xOffset; i < frameCount; ++i, ++k ) {
			if ( k >= tmpFrames[0].length ) {
				j++;
				k = 0;
			}

			frames[i] = tmpFrames[j][k];
		}

		return new Animation( interval, frames );
	}

	@Override
	public boolean equals( Object o ) {
		if ( o instanceof AnimSpec == false ) return false;
		AnimSpec other = (AnimSpec)o;

		if ( !super.equals( other ) ) return false;

		if ( frameCount != other.frameCount ||
				frameWidth != other.frameWidth || frameHeight != other.frameHeight ||
				xOffset != other.xOffset || yOffset != other.yOffset )
			return false;

		return true;
	}

	/**
	 * Converts the anim spec into a string
	 * 
	 * Dumb? Remove?
	 */
	public String serialize() {
		String s = atlasPath + ";";
		s += regionName + ";";
		s += frameWidth + ";";
		s += frameHeight + ";";
		s += frameCount + ";";
		s += xOffset + ";";
		s += yOffset + ";";
		s += interval;
		return s;
	}

	public AnimSpec deserialize( String serial ) {
		String[] s = serial.split( ";" );
		return new AnimSpec( s[0], s[1], Integer.parseInt( s[2] ), Integer.parseInt( s[3] ),
				Integer.parseInt( s[4] ), Integer.parseInt( s[5] ), Integer.parseInt( s[6] ),
				Float.parseFloat( s[7] ) );
	}
}
