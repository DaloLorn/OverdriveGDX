package com.ftloverdrive.ui.incident;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ftloverdrive.io.OVDSkin;


/**
 * A container for actors created by Consequences, used to illustrate
 * their effects.
 */
public class ConsequenceBox extends Table {

	private final LabelStyle stlGood;
	private final LabelStyle stlBad;
	private final LabelStyle stlNormal;

	private final Color cNormal;
	private final Color cHover;
	private final Color cBlue;
	private final Color cDisabled;
	private final Color cGood;
	private final Color cBad;

	private Drawable border;
	private final NinePatchDrawable borderNormal;
	private final NinePatchDrawable borderHover;
	private final NinePatchDrawable borderBlue;
	private final NinePatchDrawable borderDisabled;


	public ConsequenceBox( OVDSkin skin ) {
		stlGood = new LabelStyle( skin.get( "conseq-good", LabelStyle.class ) );
		stlNormal = new LabelStyle( skin.get( "conseq-normal", LabelStyle.class ) );
		stlBad = new LabelStyle( skin.get( "conseq-bad", LabelStyle.class ) );

		cNormal = stlNormal.fontColor;
		cGood = stlGood.fontColor;
		cBad = stlBad.fontColor;
		cHover = skin.get( "choice-hover", LabelStyle.class ).fontColor;
		cBlue = skin.get( "choice-blue", LabelStyle.class ).fontColor;
		cDisabled = skin.get( "choice-disabled", LabelStyle.class ).fontColor;

		// libGDX makes it prohibitively hard to draw a simple rectangle -- need to use textures instead...
		TextureRegion region = skin.getAtlas().findRegion( "consequence-border-normal" );
		borderNormal = new NinePatchDrawable( new NinePatch( region, 2, 2, 2, 2 ) );
		region = skin.getAtlas().findRegion( "consequence-border-hover" );
		borderHover = new NinePatchDrawable( new NinePatch( region, 2, 2, 2, 2 ) );
		region = skin.getAtlas().findRegion( "consequence-border-blue" );
		borderBlue = new NinePatchDrawable( new NinePatch( region, 2, 2, 2, 2 ) );
		region = skin.getAtlas().findRegion( "consequence-border-disabled" );
		borderDisabled = new NinePatchDrawable( new NinePatch( region, 2, 2, 2, 2 ) );

		setBackground( new TextureRegionDrawable( skin.getAtlas().findRegion( "consequence-bg" ) ) );
		border = borderNormal;
	}

	public void setStyleNormal() {
		border = borderNormal;
		stlNormal.fontColor = cNormal;
		stlGood.fontColor = cGood;
		stlBad.fontColor = cBad;
	}

	public void setStyleHover() {
		border = borderHover;
		stlNormal.fontColor = cHover;
		stlGood.fontColor = cHover;
		stlBad.fontColor = cHover;
	}

	public void setStyleBlue() {
		border = borderBlue;
		stlNormal.fontColor = cBlue;
		stlGood.fontColor = cBlue;
		stlBad.fontColor = cBlue;
	}

	public void setStyleDisabled() {
		border = borderDisabled;
		stlNormal.fontColor = cDisabled;
		stlGood.fontColor = cDisabled;
		stlBad.fontColor = cDisabled;
	}

	public LabelStyle getStylePositive() {
		return stlGood;
	}

	public LabelStyle getStyleNormal() {
		return stlNormal;
	}

	public LabelStyle getStyleNegative() {
		return stlBad;
	}

	public Actor hit( float x, float y, boolean touchable ) {
		if ( touchable && getTouchable() != Touchable.enabled ) return null;
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		super.draw( batch, parentAlpha );
		batch.setColor( Color.WHITE );
		border.draw( batch, getX(), getY(), getWidth(), getHeight() );
	}
}
