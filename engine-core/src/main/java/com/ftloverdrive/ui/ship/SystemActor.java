package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.scenes.scene2d.Group;


/**
 * Actor representing a system in the player's HUD.
 * 
 */
public class SystemActor extends Group {

	private float offsetX = 0;
	private float offsetY = 0;


	public void setOffsetX( float x ) {
		offsetX = x;
	}

	public void setOffsetY( float y ) {
		offsetY = y;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	// TODO: Add abstract methods allowing to control the system's power, etc.
}
