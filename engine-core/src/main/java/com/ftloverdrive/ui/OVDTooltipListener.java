package com.ftloverdrive.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;


/**
 * Shows/hides the actor passed in constructor when this listener is notified
 * of an enter/exit event.
 * 
 */
public class OVDTooltipListener extends InputListener {

	private Actor tooltip = null;

	private Vector2 position = new Vector2();
	private Vector2 offset = new Vector2();
	private Vector2 tmp = new Vector2();

	private final ShowTask showTask = new ShowTask();
	private final HideTask hideTask = new HideTask();

	private boolean inside = false;

	private float showDelay;
	private float hideDelay;
	private boolean followCursor = false;
	private boolean dismissOnClick = false;


	public OVDTooltipListener( Actor tooltip ) {
		this( tooltip, 5, 5 );
	}

	public OVDTooltipListener( Actor tooltip, boolean followCursor ) {
		this( tooltip );
		this.followCursor = followCursor;
	}

	public OVDTooltipListener( Actor tooltip, int offsetX, int offsetY ) {
		this.tooltip = tooltip;
		setOffset( offsetX, offsetY );
		setShowDelay( 0.5f );
		setHideDelay( 0 );
	}

	@Override
	public boolean mouseMoved( InputEvent event, float x, float y ) {
		if ( inside && followCursor ) {
			tmp.set( x, y );
			event.getListenerActor().localToStageCoordinates( tmp );

			tooltip.setPosition( position.x + offset.x + tmp.x, position.y + offset.y + tmp.y );
		}
		return false;
	}

	@Override
	public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor ) {
		if ( !dismissOnClick && fromActor == event.getListenerActor() )
			return;

		inside = true;

		tmp.set( x, y );
		event.getListenerActor().localToStageCoordinates( tmp );

		if ( tooltip.getStage() == null ) {
			event.getStage().addActor( tooltip );
		}

		if ( showDelay == 0 )
			showTask.run();
		else if ( !showTask.isScheduled() )
			Timer.schedule( showTask, showDelay );
	}

	@Override
	public void exit( InputEvent event, float x, float y, int pointer, Actor toActor ) {
		if ( !dismissOnClick && toActor == event.getListenerActor() )
			return;

		inside = false;

		if ( showTask.isScheduled() ) {
			showTask.cancel();
		}

		if ( hideDelay == 0 )
			hideTask.run();
		else if ( !hideTask.isScheduled() )
			Timer.schedule( hideTask, hideDelay );
	}

	public void setFollowCursor( boolean follow ) {
		followCursor = follow;
	}

	/**
	 * The offset of the tooltip from the touch position. It should not be
	 * positive as the tooltip will flicker otherwise.
	 */
	public void setOffset( float offsetX, float offsetY ) {
		offset.set( offsetX, offsetY );
	}

	public void setOffsetX( float offsetX ) {
		offset.x = offsetX;
	}

	public void setOffsetY( float offsetY ) {
		offset.y = offsetY;
	}

	public void setShowDelay( float seconds ) {
		showDelay = seconds;
	}

	public float getShowDelay() {
		return showDelay;
	}

	public void setHideDelay( float seconds ) {
		hideDelay = seconds;
	}

	public float getHideDelay() {
		return hideDelay;
	}

	public void setDismissOnClick( boolean dismiss ) {
		dismissOnClick = dismiss;
	}


	private class ShowTask extends Task {

		@Override
		public void run() {
			tooltip.setPosition( position.x + offset.x + tmp.x, position.y + offset.y + tmp.y );
			tooltip.setVisible( true );
			tooltip.toFront();
		}
	}

	private class HideTask extends Task {

		@Override
		public void run() {
			tooltip.setVisible( false );
		}
	}
}
