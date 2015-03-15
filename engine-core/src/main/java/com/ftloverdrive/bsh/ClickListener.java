package com.ftloverdrive.bsh;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

/**
 * Overdrive wrapper for libgdx's ClickListeer
 * 
 * @see {@link com.ftloverdrive.bsh}
 */
public class ClickListener extends com.badlogic.gdx.scenes.scene2d.utils.ClickListener {

	/** Override this to implement the listener's functionality. */
	public void clicked( InputEvent event, Float x, Float y ) {
	}

	@Override
	public void clicked( InputEvent event, float x, float y ) {
		clicked( event, (Float) x, (Float) y ); 
	}
}
