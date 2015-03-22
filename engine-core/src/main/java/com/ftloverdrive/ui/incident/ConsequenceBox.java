package com.ftloverdrive.ui.incident;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.ftloverdrive.io.OVDSkin;


/**
 * A container for actors created by Consequences, used to illustrate
 * their effects.
 */
public class ConsequenceBox extends Table {

	private final OVDSkin skin;
	private final Drawable background;

	private final LabelStyle good;
	private final LabelStyle neutral;
	private final LabelStyle bad;


	public ConsequenceBox( OVDSkin skin ) {
		good = skin.get( "conseq-good", LabelStyle.class );
		neutral = skin.get( "conseq-neutral", LabelStyle.class );
		bad = skin.get( "conseq-bad", LabelStyle.class );

		this.skin = skin;
		TextureRegion conseqRegion = skin.getAtlas().findRegion( "consequence-box" );
		NinePatch conseqPatch = new NinePatch( conseqRegion, 2, 2, 2, 2 );
		background = new NinePatchDrawable( conseqPatch );
		setBackground( background );
	}

	protected void setBorderTint( Color c ) {
		if ( c == null )
			setBackground( background );
		else
			setBackground( skin.newDrawable( background, c ) );
	}

	public LabelStyle getStylePositive() {
		return good;
	}

	public LabelStyle getStyleNeutral() {
		return neutral;
	}

	public LabelStyle getStyleNegative() {
		return bad;
	}

	public Actor hit( float x, float y, boolean touchable ) {
		if ( touchable && getTouchable() != Touchable.enabled ) return null;
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
	}
}
