package com.ftloverdrive.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;


public class ClippableButton extends Button {

	private Rectangle clickArea = null;


	public ClippableButton( ButtonStyle style ) {
		super( style );
	}

	public void setClickArea( Rectangle area ) {
		clickArea = area;
	}

	public void setClickArea( int x, int y, int w, int h ) {
		clickArea = new Rectangle( x, y, w, h );
	}

	@Override
	public Actor hit( float x, float y, boolean touchable ) {
		if ( clickArea == null ||
				( x >= clickArea.x && x < clickArea.x + clickArea.width &&
						y >= clickArea.y && y < clickArea.y + clickArea.height ) ) {
			return super.hit( x, y, touchable );
		}
		return null;
	}
}
