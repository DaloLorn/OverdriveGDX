package com.ftloverdrive.ui.incident;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.ftloverdrive.ui.ShaderLabel;


/**
 * An actor representing a PlotBranch in an IncidentDialog.
 */
public class PlotBranchLabel extends ShaderLabel {

	private static final LabelStyle defaultLabelStyle = new LabelStyle( new BitmapFont(), new Color() );


	public PlotBranchLabel( String text, final LabelStyle textStyle, final LabelStyle hoverStyle ) {
		super( text, defaultLabelStyle );

		setAlignment( Align.top | Align.left, Align.center | Align.left );
		setWrap( true );

		setStyle( textStyle );
	}
}
