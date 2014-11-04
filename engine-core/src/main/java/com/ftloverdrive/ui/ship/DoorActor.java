package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.event.TickEvent;
import com.ftloverdrive.event.TickListener;
import com.ftloverdrive.event.ship.DoorPropertyEvent;
import com.ftloverdrive.event.ship.DoorPropertyListener;
import com.ftloverdrive.io.AnimSpec;
import com.ftloverdrive.model.ship.DoorModel;
import com.ftloverdrive.net.OVDNetManager;
import com.ftloverdrive.util.OVDConstants;

/**
 * An actor that represents a single door.
 */
public class DoorActor extends Actor implements Disposable, EventListener, DoorPropertyListener, TickListener {
	protected AssetManager assetManager;
	protected OVDEventManager eventManager;
	protected OVDNetManager netManager;

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
		this.netManager = context.getNetManager();
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
	 * Instantly changes the actor's appearance to the 'closed' state.
	 * This method does not change the door's actual open/closed state.
	 */
	public void setAppearanceClosed() {
		if ( doorModelRefId == -1 ) return;

		doorAnim.setPlayMode( PlayMode.REVERSED );
		elapsed = doorAnim.getAnimationDuration();
	}

	/**
	 * Instantly changes the actor's appearance to the 'opened' state.
	 * This method does not change the door's actual open/closed state.
	 */
	public void setAppearanceOpen() {
		if ( doorModelRefId == -1 ) return;

		doorAnim.setPlayMode( PlayMode.NORMAL );
		elapsed = doorAnim.getAnimationDuration();
	}

	/**
	 * Returns true if the actor's appearance indicates that the door is
	 * closed, false otherwise, or if the animation is playing.
	 */
	public boolean isAppearanceClosed() {
		if ( doorModelRefId == -1 ) return false;
		return ( doorAnim.getPlayMode() == PlayMode.REVERSED && elapsed >= doorAnim.getAnimationDuration() ) ||
				( doorAnim.getPlayMode() == PlayMode.NORMAL && elapsed == 0 );
	}

	/**
	 * Returns true if the actor's appearance indicates that the door is
	 * open, false otherwise, or if the animation is playing.
	 */
	public boolean isAppearanceOpen() {
		if ( doorModelRefId == -1 ) return false;
		return ( doorAnim.getPlayMode() == PlayMode.NORMAL && elapsed >= doorAnim.getAnimationDuration() ) ||
				( doorAnim.getPlayMode() == PlayMode.REVERSED && elapsed == 0 );
	}

	/**
	 * Returns true if the door is currently playing the close animation, false otherwise.
	 */
	public boolean isPlayingClose() {
		if ( doorModelRefId == -1 ) return false;
		return doorAnim.getPlayMode() == PlayMode.REVERSED && elapsed < doorAnim.getAnimationDuration();
	}

	/**
	 * Returns true if the door is currently playing the open animation, false otherwise.
	 */
	public boolean isPlayingOpen() {
		if ( doorModelRefId == -1 ) return false;
		return doorAnim.getPlayMode() == PlayMode.NORMAL && elapsed < doorAnim.getAnimationDuration();
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
			setBounds( 0, 0, 0, 0 );
		}
		else {
			DoorModel doorModel = context.getReferenceManager().getObject( doorModelRefId, DoorModel.class );
			AnimSpec newAnimSpec = doorModel.getAnimSpec();
			boolean wantOpen = doorModel.getProperties().getBool( OVDConstants.DOOR_OPEN );

			// Update the animation
			PlayMode playMode = wantOpen ? PlayMode.NORMAL : PlayMode.REVERSED;
			if ( doorAnim != null )
				playMode = doorAnim.getPlayMode(); // Save for when the door's AnimSpec has been changed.

			if ( animSpec != null && !animSpec.equals( newAnimSpec ) ) {
				assetManager.unload( animSpec.getAtlasPath() );
				animSpec = null;
			}
			if ( animSpec == null && newAnimSpec != null ) {
				animSpec = newAnimSpec;
				assetManager.load( animSpec.getAtlasPath(), TextureAtlas.class );
				assetManager.finishLoading();
				doorAnim = animSpec.create( context );
				doorAnim.setPlayMode( playMode );
				setSize( animSpec.getFrameWidth(), animSpec.getFrameHeight() );
			}

			if ( wantOpen ) {
				if ( isAppearanceClosed() || isPlayingClose() || isPlayingOpen() )
					playOpen();
				else
					setAppearanceOpen();
			}
			else {
				if ( isAppearanceOpen() || isPlayingOpen() || isPlayingClose() )
					playClose();
				else
					setAppearanceClosed();
			}
		}
	}

	@Override
	public Actor hit( float x, float y, boolean touchable ) {
		// Door sheet's frame size is 35px, but the clickable area of the door is smaller
		// TODO: find a way not to hardcode this
		if ( touchable && getTouchable() != Touchable.enabled ) return null;
		return x >= 11 && x < 24 && y >= 3 && y < 33 ? this : null;
	}

	@Override
	public void doorPropertyChanged( OverdriveContext context, DoorPropertyEvent e ) {
		if ( e.getDoorRefId() != doorModelRefId ) return;

		if ( e.getPropertyType() == DoorPropertyEvent.BOOL_TYPE ) {
			updateDoorInfo( context );
		}
		else if ( e.getPropertyType() == DoorPropertyEvent.INT_TYPE ) {
			if ( e.getPropertyKey().equals( OVDConstants.DOOR_HEALTH ) ) {
				DoorModel doorModel = context.getReferenceManager().getObject( doorModelRefId, DoorModel.class );
				int curHealth = doorModel.getProperties().getInt( OVDConstants.DOOR_HEALTH );
				if ( curHealth <= 0 ) {
					DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
					ev.init( doorModelRefId, DoorPropertyEvent.BOOL_TYPE, DoorPropertyEvent.SET_ACTION, OVDConstants.DOOR_OPEN, true );
					ev.setSource( netManager.getLocalPlayerRefId() );
					eventManager.postDelayedEvent( ev );

					ev = Pools.get( DoorPropertyEvent.class ).obtain();
					ev.init( doorModelRefId, DoorPropertyEvent.BOOL_TYPE, DoorPropertyEvent.SET_ACTION, OVDConstants.DOOR_LOCKED, true );
					ev.setSource( netManager.getLocalPlayerRefId() );
					eventManager.postDelayedEvent( ev );
				}
				else if ( curHealth == doorModel.getProperties().getInt( OVDConstants.DOOR_HEALTH_MAX ) &&
						doorModel.getProperties().getBool( OVDConstants.DOOR_LOCKED ) ) {
					DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
					ev.init( doorModelRefId, DoorPropertyEvent.BOOL_TYPE, DoorPropertyEvent.SET_ACTION, OVDConstants.DOOR_LOCKED, false );
					ev.setSource( netManager.getLocalPlayerRefId() );
					eventManager.postDelayedEvent( ev );
				}
			}
		}
	}

	@Override
	public boolean handle( Event event ) {
		if ( event instanceof InputEvent ) {
			InputEvent e = (InputEvent)event;
			switch ( e.getType() ) {
				case touchDown:
					if ( e.getButton() == Buttons.LEFT ) {
						DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
						ev.init( doorModelRefId, DoorPropertyEvent.BOOL_TYPE, DoorPropertyEvent.TOGGLE_ACTION, OVDConstants.DOOR_OPEN, false );
						ev.setSource( netManager.getLocalPlayerRefId() );
						eventManager.postDelayedEvent( ev );
						return true;
					}
					else if ( e.getButton() == Buttons.RIGHT ) {
						DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
						ev.init( doorModelRefId, DoorPropertyEvent.INT_TYPE, DoorPropertyEvent.INCREMENT_ACTION, OVDConstants.DOOR_HEALTH, -50 );
						eventManager.postDelayedEvent( ev );
						return true;
					}
				default:
					return false;
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		assetManager.unload( animSpec.getAtlasPath() );
	}

	@Override
	public void ticksAccumulated( TickEvent e ) {
		DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
		ev.init( doorModelRefId, DoorPropertyEvent.INT_TYPE, DoorPropertyEvent.INCREMENT_ACTION, OVDConstants.DOOR_HEALTH, 10 );
		eventManager.postDelayedEvent( ev );
	}
}
