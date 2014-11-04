package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.ui.ship.DoorActor;
import com.ftloverdrive.util.OVDConstants;

public class PlayerShipDoorHighlighter extends Actor implements Disposable, EventListener {
	protected AssetManager assetManager;
	protected Sprite highlightSprite;

	public PlayerShipDoorHighlighter( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( OVDConstants.EFFECTS_ATLAS, TextureAtlas.class );
		assetManager.finishLoading();
		TextureAtlas effectsAtlas = assetManager.get( OVDConstants.EFFECTS_ATLAS, TextureAtlas.class );

		highlightSprite = effectsAtlas.createSprite( "door-highlight" );
		this.setWidth( highlightSprite.getWidth() );
		this.setHeight( highlightSprite.getHeight() );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		highlightSprite.setPosition( this.getX(), this.getY() );
		highlightSprite.setRotation( this.getRotation() );
		highlightSprite.draw( batch, 0.5f );
	}

	@Override
	public void dispose() {
		assetManager.unload( OVDConstants.EFFECTS_ATLAS );
	}

	@Override
	public boolean handle( Event e ) {
		if ( e instanceof InputEvent ) {
			Actor target = e.getTarget();
			InputEvent event = (InputEvent)e;
			
			if ( event.getType() == Type.enter && target instanceof DoorActor ) {
				float r = target.getRotation();
				setVisible( true );
				Vector2 pos = new Vector2( 0, 0 );
				pos = target.localToStageCoordinates( pos );
				setPosition( pos.x - ( r == 0 ? 0 : getWidth() ), pos.y );
				setRotation( r );
				return true;
			}
			else if ( event.getType() == Type.exit && target instanceof DoorActor &&
					event.getPointer() == -1 ) {
				setVisible( false );
				return true;
			}
		}

		return false;
	}
}
