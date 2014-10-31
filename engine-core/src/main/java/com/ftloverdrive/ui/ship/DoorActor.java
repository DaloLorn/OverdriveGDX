package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.event.ship.DoorPropertyEvent;
import com.ftloverdrive.event.ship.DoorPropertyListener;
import com.ftloverdrive.io.AnimSpec;
import com.ftloverdrive.model.ship.DoorModel;
import com.ftloverdrive.util.OVDConstants;

/**
 * An actor that represents a single door.
 */
public class DoorActor extends Actor implements Disposable, EventListener, DoorPropertyListener {
	protected AssetManager assetManager;
	protected OVDEventManager eventManager;

	protected AnimSpec animSpec;
	protected Animation doorAnim;
	/* 
	 * Keep the elapsed time as a field so that the animation will move smoothly
	 * between closing and opening states when the user clicks mid-animation,
	 * instead of starting from the first frame.
	 */
	private float elapsed = 0;

	protected int doorModelRefId = -1;

	public DoorActor( OverdriveContext context ) {
		super();
		this.assetManager = context.getAssetManager();
		this.eventManager = context.getScreenEventManager();
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		if ( doorModelRefId != -1 ) {
			batch.draw( doorAnim.getKeyFrame( elapsed ), getX(), getY(), 0, 0,
					getWidth(), getHeight(), 1, 1, getRotation() );
		}
	}

	@Override
	public void act( float delta ) {
		if ( elapsed < doorAnim.getAnimationDuration() ) {
			elapsed += delta;
		}
	}

	/**
	 * Plays the opening animation, gradually transiting from closed to open state.
	 */
	public void playOpen() {
		if ( doorModelRefId == -1 ) return;

		doorAnim.setPlayMode( PlayMode.NORMAL );
		if ( elapsed > 0 )
			elapsed = doorAnim.getAnimationDuration() - elapsed;
	}

	/**
	 * Plays the closing animation, gradually transiting from open to closed state.
	 */
	public void playClose() {
		if ( doorModelRefId == -1 ) return;

		doorAnim.setPlayMode( PlayMode.REVERSED );
		if ( elapsed > 0 )
			elapsed = doorAnim.getAnimationDuration() - elapsed;
	}

	/**
	 * Instantly changes the actor to display the 'closed' state.
	 */
	public void setStateClosed() {
		if ( doorModelRefId == -1 ) return;

		doorAnim.setPlayMode( PlayMode.REVERSED );
		elapsed = doorAnim.getAnimationDuration();
	}

	/**
	 * Instantly changes the actor to display the 'opened' state.
	 */
	public void setStateOpen() {
		if ( doorModelRefId == -1 ) return;

		doorAnim.setPlayMode( PlayMode.NORMAL );
		elapsed = doorAnim.getAnimationDuration();
	}

	public boolean isStateClosed() {
		if ( doorModelRefId == -1 ) return false;
		return ( doorAnim.getPlayMode() == PlayMode.REVERSED && elapsed >= doorAnim.getAnimationDuration() ) ||
				( doorAnim.getPlayMode() == PlayMode.NORMAL && elapsed == 0 );
	}

	public boolean isStateOpen() {
		if ( doorModelRefId == -1 ) return false;
		return ( doorAnim.getPlayMode() == PlayMode.NORMAL && elapsed >= doorAnim.getAnimationDuration() ) ||
				( doorAnim.getPlayMode() == PlayMode.REVERSED && elapsed == 0 );
	}

	public void setDoorModel( OverdriveContext context, int doorModelRefId ) {
		this.doorModelRefId = doorModelRefId;
		updateDoorInfo( context );
	}

	public int getDoorModel() {
		return doorModelRefId;
	}

	/**
	 * Updates everything to match the current DoorModel.
	 */
	private void updateDoorInfo( OverdriveContext context ) {
		if ( doorModelRefId == -1 ) {
			setPosition( 0, 0 );
			setSize( 0, 0 );
		}
		else {
			DoorModel doorModel = context.getReferenceManager().getObject( doorModelRefId, DoorModel.class );
			AnimSpec newAnimSpec = doorModel.getAnimSpec();

			// Default values
			boolean stateOpen = false;
			boolean stateClosed = !doorModel.getProperties().getBool( OVDConstants.DOOR_OPEN );
			if ( doorAnim != null ) {
				// Both can be null when door is currently changing states
				stateOpen = isStateOpen();
				stateClosed = isStateClosed();
			}

			if ( animSpec != null && !animSpec.equals( newAnimSpec ) ) {
				assetManager.unload( animSpec.getAtlasPath() );
				animSpec = null;
			}
			if ( animSpec == null && newAnimSpec != null ) {
				animSpec = newAnimSpec;
				assetManager.load( animSpec.getAtlasPath(), TextureAtlas.class );
				assetManager.finishLoading();
				doorAnim = animSpec.create( context );
			}

			boolean open = doorModel.getProperties().getBool( OVDConstants.DOOR_OPEN );
			if ( stateOpen )
				if ( open ) setStateOpen(); else playClose();
			else if ( stateClosed )
				if ( open ) playOpen(); else setStateClosed();
		}
	}

	@Override
	public void dispose() {
		assetManager.unload( animSpec.getAtlasPath() );
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof InputEvent) {
			InputEvent e = (InputEvent)event;
			switch (e.getType()) {
				case touchDown:
					DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
					ev.init( doorModelRefId, DoorPropertyEvent.BOOL_TYPE, DoorPropertyEvent.TOGGLE_ACTION, OVDConstants.DOOR_OPEN, false );
					eventManager.postDelayedEvent(ev);
					return true;
				default:
					return false;
			}
		}
		return false;
	}

	@Override
	public void doorPropertyChanged(OverdriveContext context, DoorPropertyEvent e) {
		if ( e.getDoorRefId() != doorModelRefId ) return;

		if ( e.getPropertyType() == DoorPropertyEvent.BOOL_TYPE ) {
			updateDoorInfo( context );
		}
	}
}
