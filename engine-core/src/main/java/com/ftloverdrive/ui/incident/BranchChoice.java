package com.ftloverdrive.ui.incident;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;


/**
 * An actor representing a branch choice in an incident window.
 * 
 * TODO: Use skins instead of hardcoding the atlases and appearance.
 */
public class BranchChoice extends Label {

	public BranchChoice( String text, final LabelStyle textStyle, final LabelStyle hoverStyle ) {
		super( text, textStyle );

		setAlignment( Align.top | Align.left, Align.center | Align.left );
		setWrap( true );

		addListener( new InputListener() {

			public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor ) {
				if ( event.getTarget() == BranchChoice.this )
					setStyle( hoverStyle );
			}

			public void exit( InputEvent event, float x, float y, int pointer, Actor toActor ) {
				// Exit events are apparently also sent on mouseUp/Down...
				if ( event.getTarget() == BranchChoice.this && toActor != BranchChoice.this )
					setStyle( textStyle );
			}
		} );
	}
}
