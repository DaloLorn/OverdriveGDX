package com.ftloverdrive.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class ShaderLabel extends Label {

	protected ShaderProgram shader = null;


	public ShaderLabel( CharSequence text, Skin skin ) {
		super( text, skin );
	}

	public ShaderLabel( CharSequence text, Skin skin, String styleName ) {
		super( text, skin, styleName );
	}

	public ShaderLabel( CharSequence text, Skin skin, String fontName, Color color ) {
		super( text, skin, fontName, color );
	}

	public ShaderLabel( CharSequence text, Skin skin, String fontName, String colorName ) {
		super( text, skin, fontName, colorName );
	}

	public ShaderLabel( CharSequence text, LabelStyle style ) {
		super( text, style );
	}

	public void draw( Batch batch, float parentAlpha ) {
		batch.setShader( shader );
		super.draw( batch, parentAlpha );
		batch.setShader( null );
	}

	public void setShader( ShaderProgram shader ) {
		this.shader = shader;
	}


	public static class ShaderLabelStyle extends LabelStyle {

		public String shader;
	}
}
