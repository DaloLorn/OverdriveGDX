package com.ftloverdrive.ui.ship;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.local.LocalActorClickedEvent;
import com.ftloverdrive.event.local.LocalActorClickedListener;
import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.ship.TeleportPadModel;
import com.ftloverdrive.ui.ModelActor;


public class TeleportPadActor extends ModelActor
		implements Disposable, LocalActorClickedListener {

	protected TextureRegion tpadSprite;


	public TeleportPadActor( OverdriveContext context ) {
		super( context );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		if ( modelRefId != -1 ) {
			batch.draw( tpadSprite, getX(), getY(), 0, 0,
					getWidth(), getHeight(), 1, 1, getRotation() );
		}
	}

	/**
	 * Updates everything to match the current CrewModel.
	 */
	protected void updateInfo( OverdriveContext context ) {
		if ( modelRefId == -1 ) {
		}
		else {
			TeleportPadModel tpadModel = context.getReferenceManager().getObject( modelRefId, TeleportPadModel.class );
			ImageSpec spec = null;

			spec = tpadModel.getAnimSpec();

			TextureAtlas atlas = context.getAssetManager().get( spec.getAtlasPath(), TextureAtlas.class );
			tpadSprite = atlas.findRegion( spec.getRegionName() );

			setSize( tpadSprite.getRegionWidth(), tpadSprite.getRegionHeight() );
		}
	}

	@Override
	public Actor hit( float x, float y, boolean touchable ) {
		if ( touchable && getTouchable() != Touchable.enabled ) return null;
		return x >= 0 && x < 35 && y >= 0 && y < 35 ? this : null;
	}

	@Override
	public void actorClicked( OverdriveContext context, LocalActorClickedEvent e ) {
		if ( e.getTarget() instanceof TeleportPadActor ) {
			TeleportPadActor target = (TeleportPadActor)e.getTarget();

			if ( target.getModelRefId() != modelRefId ) return;

			if ( e.getButton() == Buttons.LEFT ) {
			}
		}
	}

	@Override
	public void dispose() {
		tpadSprite = null;
	}
}
