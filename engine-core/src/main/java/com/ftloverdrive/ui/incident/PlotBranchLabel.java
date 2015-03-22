package com.ftloverdrive.ui.incident;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;


/**
 * An actor representing a PlotBranch in an IncidentDialog.
 */
public class PlotBranchLabel extends Label {

	private static final LabelStyle defaultLabelStyle = new LabelStyle( new BitmapFont(), new Color() );


	public PlotBranchLabel( String text, final LabelStyle textStyle, final LabelStyle hoverStyle ) {
		super( text, defaultLabelStyle );

		setAlignment( Align.top | Align.left, Align.center | Align.left );
		setWrap( true );

		setStyle( textStyle );

		addListener( new InputListener() {

			public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor ) {
				if ( event.getTarget() == PlotBranchLabel.this )
					setStyle( hoverStyle );
			}

			public void exit( InputEvent event, float x, float y, int pointer, Actor toActor ) {
				// Exit events are apparently also sent on mouseUp/Down...
				if ( event.getTarget() == PlotBranchLabel.this && toActor != PlotBranchLabel.this )
					setStyle( textStyle );
			}
		} );
	}
}
