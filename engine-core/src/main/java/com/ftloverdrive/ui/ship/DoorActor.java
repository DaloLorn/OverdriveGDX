package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.command.DoorToggleCommand;
import com.ftloverdrive.command.ModelCommand;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.TickEvent;
import com.ftloverdrive.event.TickListener;
import com.ftloverdrive.event.local.LocalActorClickedEvent;
import com.ftloverdrive.event.local.LocalActorClickedListener;
import com.ftloverdrive.event.ship.DoorPropertyEvent;
import com.ftloverdrive.event.ship.DoorPropertyListener;
import com.ftloverdrive.io.AnimSpec;
import com.ftloverdrive.model.ship.DoorModel;
import com.ftloverdrive.ui.ModelActor;
import com.ftloverdrive.util.OVDConstants;


/**
 * An actor that represents a single door.
 */
public class DoorActor extends ModelActor
		implements Disposable, DoorPropertyListener, TickListener, LocalActorClickedListener {

	protected AnimSpec animSpec;
	protected Animation doorAnim;
	private TextureRegion roomFloor;

	protected boolean horizontal = false;

	/**
	 * Keep the elapsed time as a field so that the animation will move smoothly
	 * between closing and opening states when the user clicks mid-animation,
	 * instead of starting from the first frame.
	 */
	private float elapsed = 0;


	public DoorActor( OverdriveContext context, TextureRegion floorRegion, boolean horizontal ) {
		super( context );
		this.horizontal = horizontal;
		roomFloor = floorRegion;
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		if ( modelRefId != -1 ) {
			if ( !isAppearanceClosed() ) {
				// Draw part of floor to mask the wall underneath
				int w = horizontal ? 20 : 4;
				int h = horizontal ? 4 : 20;
				float x = getX() + ( getWidth() - w ) / 2 - ( horizontal ? 35 : 0 );
				float y = getY() + ( getHeight() - h - 1 ) / 2;
				batch.draw( roomFloor, x, y, w, h );
			}

			// Draw the door proper
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
	 * Updates everything to match the current DoorModel.
	 */
	protected void updateInfo( OverdriveContext context ) {
		if ( modelRefId == -1 ) {
		}
		else {
			DoorModel doorModel = context.getReferenceManager().getObject( modelRefId, DoorModel.class );
			AnimSpec newAnimSpec = doorModel.getAnimSpec();
			boolean wantOpen = doorModel.getProperties().getBool( OVDConstants.DOOR_OPEN );
			boolean animSpecChanged = false;

			// Update the animation
			PlayMode playMode = wantOpen ? PlayMode.NORMAL : PlayMode.REVERSED;
			if ( doorAnim != null )
				playMode = doorAnim.getPlayMode(); // Save for when the door's AnimSpec has been changed.

			if ( animSpec != null && !animSpec.equals( newAnimSpec ) ) {
				context.getAssetManager().unload( animSpec.getAtlasPath() );
				animSpec = null;
				animSpecChanged = true;
			}
			if ( animSpec == null && newAnimSpec != null ) {
				animSpec = newAnimSpec;
				context.getAssetManager().load( animSpec.getAtlasPath(), TextureAtlas.class );
				context.getAssetManager().finishLoading();
				doorAnim = animSpec.create( context );
				doorAnim.setPlayMode( playMode );
				setSize( animSpec.getFrameWidth(), animSpec.getFrameHeight() );
			}

			// Don't touch the animation state if the anim spec was changed, to avoid visual glitches
			if ( !animSpecChanged ) {
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
	}

	@Override
	public Actor hit( float x, float y, boolean touchable ) {
		// Door sheet's frame size is 35px, but the clickable area of the door is smaller
		// TODO: find a way not to hardcode this?
		if ( touchable && getTouchable() != Touchable.enabled ) return null;
		return x >= 6 && x < 27 && y >= 3 && y < 33 ? this : null;
	}

	@Override
	public void doorPropertyChanged( OverdriveContext context, DoorPropertyEvent e ) {
		if ( e.getDoorRefId() != modelRefId ) return;

		if ( e.getPropertyKey() == OVDConstants.DOOR_OPEN ) {
			updateInfo( context );
		}
		else if ( e.getPropertyKey().equals( OVDConstants.DOOR_HEALTH ) ) {
			DoorModel doorModel = context.getReferenceManager().getObject( modelRefId, DoorModel.class );
			int curHealth = doorModel.getProperties().getInt( OVDConstants.DOOR_HEALTH );
			if ( curHealth <= 0 ) {
				// Open the door
				DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
				ev.init( modelRefId, DoorPropertyEvent.BOOL_TYPE, DoorPropertyEvent.SET_ACTION, OVDConstants.DOOR_OPEN, true );
				ev.setSource( context.getNetManager().getLocalPlayerRefId() );
				context.getScreenEventManager().postDelayedEvent( ev );

				// Lock it in that state
				ev = Pools.get( DoorPropertyEvent.class ).obtain();
				ev.init( modelRefId, DoorPropertyEvent.BOOL_TYPE, DoorPropertyEvent.SET_ACTION, OVDConstants.DOOR_LOCKED, true );
				ev.setSource( context.getNetManager().getLocalPlayerRefId() );
				context.getScreenEventManager().postDelayedEvent( ev );
			}
			else if ( curHealth == doorModel.getProperties().getInt( OVDConstants.DOOR_HEALTH_MAX ) &&
					doorModel.getProperties().getBool( OVDConstants.DOOR_LOCKED ) ) {
				// Unlock the door
				DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
				ev.init( modelRefId, DoorPropertyEvent.BOOL_TYPE, DoorPropertyEvent.SET_ACTION, OVDConstants.DOOR_LOCKED, false );
				ev.setSource( context.getNetManager().getLocalPlayerRefId() );
				context.getScreenEventManager().postDelayedEvent( ev );
			}
		}
		else if ( e.getPropertyKey() == OVDConstants.DOOR_ANIM_SPEC ) {
			// TODO: Get the spec from event
			AnimSpec newSpec = new AnimSpec( OVDConstants.EFFECTS_ATLAS, "door-sheet", 35, 35, 5, 0, 1, 0.25f );

			DoorModel doorModel = context.getReferenceManager().getObject( modelRefId, DoorModel.class );
			doorModel.setAnimSpec( newSpec );

			updateInfo( context );
		}
	}

	@Override
	public void ticksAccumulated( TickEvent e ) {
		// TODO: Test code, remove later
		DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
		ev.init( modelRefId, DoorPropertyEvent.INT_TYPE, DoorPropertyEvent.INCREMENT_ACTION, OVDConstants.DOOR_HEALTH, 10 );
		context.getScreenEventManager().postDelayedEvent( ev );
		// End test code
	}

	@Override
	public void actorClicked( OverdriveContext context, LocalActorClickedEvent e ) {
		if ( e.getTarget() instanceof DoorActor ) {
			DoorActor target = (DoorActor)e.getTarget();

			if ( target.getModelRefId() != modelRefId ) return;

			if ( e.getButton() == Buttons.LEFT ) {
				ModelCommand command = new DoorToggleCommand();

				command.setAffectedModel( target.getModelRefId() );
				command.setSourcePlayer( context.getNetManager().getLocalPlayerRefId() );
				command.execute( context );
			}
			else if ( e.getButton() == Buttons.RIGHT ) {
				// TODO: Remove this entire if case, used only for testing
				DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
				// ev.init( target.getDoorModel(), DoorPropertyEvent.INT_TYPE, DoorPropertyEvent.INCREMENT_ACTION, OVDConstants.DOOR_HEALTH, -50 );
				ev.init( target.getModelRefId(), DoorPropertyEvent.INT_TYPE, DoorPropertyEvent.INCREMENT_ACTION, OVDConstants.DOOR_ANIM_SPEC, 0 );
				context.getScreenEventManager().postDelayedEvent( ev );
			}
		}
	}

	@Override
	public void dispose() {
		context.getAssetManager().unload( animSpec.getAtlasPath() );
	}

	/*
	 * ==============================================
	 * Animation state control.
	 */

	/**
	 * Plays the opening animation, gradually transiting from closed to open state.
	 */
	protected void playOpen() {
		if ( modelRefId == -1 ) throw new IllegalArgumentException( "RefID is -1!" );

		doorAnim.setPlayMode( PlayMode.NORMAL );
		if ( elapsed > 0 )
			elapsed = doorAnim.getAnimationDuration() - elapsed;
	}

	/**
	 * Plays the closing animation, gradually transiting from open to closed state.
	 */
	protected void playClose() {
		if ( modelRefId == -1 ) throw new IllegalArgumentException( "RefID is -1!" );

		doorAnim.setPlayMode( PlayMode.REVERSED );
		if ( elapsed > 0 )
			elapsed = doorAnim.getAnimationDuration() - elapsed;
	}

	/**
	 * Instantly changes the actor's appearance to the 'closed' state.
	 * This method does not change the door's actual open/closed state.
	 */
	protected void setAppearanceClosed() {
		if ( modelRefId == -1 ) throw new IllegalArgumentException( "RefID is -1!" );

		doorAnim.setPlayMode( PlayMode.REVERSED );
		elapsed = doorAnim.getAnimationDuration();
	}

	/**
	 * Instantly changes the actor's appearance to the 'opened' state.
	 * This method does not change the door's actual open/closed state.
	 */
	protected void setAppearanceOpen() {
		if ( modelRefId == -1 ) throw new IllegalArgumentException( "RefID is -1!" );

		doorAnim.setPlayMode( PlayMode.NORMAL );
		elapsed = doorAnim.getAnimationDuration();
	}

	/*
	 * ==============================================
	 * Animation state querying.
	 */

	/**
	 * Returns true if the actor's appearance indicates that the door is
	 * closed, false otherwise, or if the animation is playing.
	 */
	public boolean isAppearanceClosed() {
		if ( modelRefId == -1 ) throw new IllegalArgumentException( "RefID is -1!" );
		return ( doorAnim.getPlayMode() == PlayMode.REVERSED && elapsed >= doorAnim.getAnimationDuration() ) ||
				( doorAnim.getPlayMode() == PlayMode.NORMAL && elapsed == 0 );
	}

	/**
	 * Returns true if the actor's appearance indicates that the door is
	 * open, false otherwise, or if the animation is playing.
	 */
	public boolean isAppearanceOpen() {
		if ( modelRefId == -1 ) throw new IllegalArgumentException( "RefID is -1!" );
		return ( doorAnim.getPlayMode() == PlayMode.NORMAL && elapsed >= doorAnim.getAnimationDuration() ) ||
				( doorAnim.getPlayMode() == PlayMode.REVERSED && elapsed == 0 );
	}

	/**
	 * Returns true if the door is currently playing the close animation, false otherwise.
	 */
	public boolean isPlayingClose() {
		if ( modelRefId == -1 ) throw new IllegalArgumentException( "RefID is -1!" );
		return doorAnim.getPlayMode() == PlayMode.REVERSED && elapsed < doorAnim.getAnimationDuration();
	}

	/**
	 * Returns true if the door is currently playing the open animation, false otherwise.
	 */
	public boolean isPlayingOpen() {
		if ( modelRefId == -1 ) throw new IllegalArgumentException( "RefID is -1!" );
		return doorAnim.getPlayMode() == PlayMode.NORMAL && elapsed < doorAnim.getAnimationDuration();
	}
}
