package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.ship.DoorModel;

/**
 * An actor that represents a single door.
 */
public class DoorActor extends Actor implements Disposable {
	protected AssetManager assetManager;

	protected ImageSpec animSpec;
	protected Animation doorAnim;
	/* 
	 * Keep the elapsed time as a field so that the animation will move smoothly
	 * between closing and opening states when the user clicks mid-animation,
	 * instead of starting from the first frame.
	 */
	private float elapsed = 0;

	protected int doorModelRefId = -1;

	public DoorActor( AssetManager assetManager ) {
		super();
		this.assetManager = assetManager;
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

	public void setDoorModel( OverdriveContext context, int doorModelRefId ) {
		this.doorModelRefId = doorModelRefId;
		updateInfo( context );
	}

	/**
	 * Updates everything to match the current DoorModel.
	 */
	private void updateInfo( OverdriveContext context ) {
		if ( doorModelRefId == -1 ) {
			setPosition( 0, 0 );
			setSize( 0, 0 );
		}
		else {
			if (animSpec != null)
				assetManager.unload( animSpec.getAtlasPath() );

			DoorModel doorModel = context.getReferenceManager().getObject( doorModelRefId, DoorModel.class );
			animSpec = doorModel.getAnimSpec();
			
			assetManager.get( animSpec.getAtlasPath(), TextureAtlas.class );
			TextureAtlas doorAtlas = assetManager.get( animSpec.getAtlasPath(), TextureAtlas.class );
			TextureRegion doorRegion = doorAtlas.findRegion( animSpec.getRegionName() ); 
			// FTL's animations.xml counts 0-based rows from the bottom.
			TextureRegion[][] tmpFrames = doorRegion.split( 35, 35 );
			TextureRegion[] doorFrames = new TextureRegion[5];
			for ( int i=0; i < doorFrames.length; i++ )
				doorFrames[i] = tmpFrames[0][i];

			setSize( 35, 35 );
			doorAnim = new Animation( .2f, doorFrames );
		}
	}

	@Override
	public void dispose() {
		assetManager.unload( animSpec.getAtlasPath() );
	}
}
