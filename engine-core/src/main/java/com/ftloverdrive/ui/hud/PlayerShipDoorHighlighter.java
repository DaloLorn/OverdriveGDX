package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.ui.ship.DoorActor;


public class PlayerShipDoorHighlighter extends Actor implements Disposable, EventListener {

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/door-highlighter.json";

	protected AssetManager assetManager;

	protected Sprite highlightSprite;
	protected float alpha;


	public PlayerShipDoorHighlighter( OverdriveContext context ) {
		super();
		assetManager = context.getAssetManager();

		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );

		highlightSprite = skin.getSprite( "door-highlighter" );
		alpha = skin.getFloat( "door-highlighter-alpha" );

		setWidth( highlightSprite.getWidth() );
		setHeight( highlightSprite.getHeight() );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		highlightSprite.setPosition( getX(), getY() );
		highlightSprite.setRotation( getRotation() );
		highlightSprite.draw( batch, alpha * parentAlpha );
	}

	@Override
	public void dispose() {
		assetManager.unload( SKIN_PATH );
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
