package com.ftloverdrive.bsh;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

/**
 * Bug with Beanshell: overriding methods that have primitive parameters causes
 * a java.lang.VerifyError due to mismatched types. Workaround is to use wrappers.
 */
public class ClickListener extends com.badlogic.gdx.scenes.scene2d.utils.ClickListener {

	public void clicked( InputEvent event, Float x, Float y ) {
	}

	@Override
	public void clicked( InputEvent event, float x, float y ) {
		clicked( event, (Float) x, (Float) y ); 
	}
}
