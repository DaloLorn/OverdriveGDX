package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.io.ButtonSpec;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.system.SystemModel;
import com.ftloverdrive.script.ButtonScript;
import com.ftloverdrive.script.ScriptResource;
import com.ftloverdrive.ui.ClippableButton;
import com.ftloverdrive.ui.ModelActor;


/**
 * This actor encapsulates system buttons in player's HUD, eg. hacking or doors.
 * Buttons are stacked on top of each other ad infinitum.
 * 
 * @author kartoFlane
 */
public class SystemButtonsActor extends ModelActor {

	protected final int startX;
	protected final int startY;
	protected final int endX;
	protected final int endY;
	protected final int gapX;
	protected final int gapY;
	protected final int spacingY;

	protected final NinePatchDrawable buttonBase;

	private final AssetManager assetManager;

	private float buttonBaseWidth = 0;
	private float buttonBaseHeight = 0;


	public SystemButtonsActor( OverdriveContext context ) {
		super( context );
		assetManager = context.getAssetManager();

		OVDSkin skin = assetManager.get( SystemActor.SKIN_PATH, OVDSkin.class );

		buttonBase = skin.get( "button-base", NinePatchDrawable.class );
		startX = skin.getInt( "button-base-start-x" );
		startY = skin.getInt( "button-base-start-y" );
		endX = skin.getInt( "button-base-end-x" );
		endY = skin.getInt( "button-base-end-y" );
		gapX = skin.getInt( "button-base-gap-x" );
		gapY = skin.getInt( "button-base-gap-y" );
		spacingY = skin.getInt( "button-base-spacing-y" );

		setTouchable( Touchable.childrenOnly );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		buttonBase.draw( batch, getX(), getY(), buttonBaseWidth, buttonBaseHeight );
		super.draw( batch, parentAlpha );
	}

	@Override
	protected void updateInfo( final OverdriveContext context ) {
		if ( modelRefId == -1 ) {
			buttonBaseWidth = 0;
			buttonBaseHeight = 0;
		}
		else {
			SystemModel systemModel = context.getReferenceManager().getObject( modelRefId, SystemModel.class );

			ButtonSpec[] specs = systemModel.getButtons();

			buttonBaseWidth = 0;
			buttonBaseHeight = 0;
			for ( Actor a : getChildren() ) {
				if ( a instanceof Button )
					a.remove();
			}

			setVisible( specs != null );
			if ( specs == null )
				return;

			buttonBaseWidth = startX + gapX;
			buttonBaseHeight = startY + gapY;
			int maxW = 0;

			float x = startX + gapX;
			float y = startY + gapY;
			for ( int i = 0; i < specs.length; ++i ) {
				ButtonSpec spec = specs[i];

				assetManager.load( spec.getSkinPath(), OVDSkin.class );
				assetManager.load( spec.getScriptPath(), ScriptResource.class );
				assetManager.finishLoading();
				OVDSkin skin = assetManager.get( spec.getSkinPath(), OVDSkin.class );

				final int ox = skin.getInt( "clickable-offset-x" );
				final int oy = skin.getInt( "clickable-offset-y" );
				final int w = skin.getInt( "clickable-width" );
				final int h = skin.getInt( "clickable-height" );
				ButtonStyle style = skin.get( "style", ButtonStyle.class );

				final ClippableButton btn = new ClippableButton( style );
				btn.setClickArea( ox, oy, w, h );
				btn.setPosition( x - ox, y - oy );

				ScriptResource script = assetManager.get( spec.getScriptPath(), ScriptResource.class );
				final ButtonScript btnScript = context.getScreenScriptManager().getInterface( script, ButtonScript.class );
				btnScript.onUpdate( context, btn, modelRefId );
				btn.addListener( new ClickListener() {
					@Override
					public void clicked( InputEvent event, float x, float y ) {
						btnScript.onClick( context, btn );
					}
				} );

				addActor( btn );

				maxW = Math.max( maxW, w );
				buttonBaseHeight += h;
				y += h + spacingY;

				if ( i + 1 != specs.length )
					buttonBaseHeight += spacingY;

				assetManager.unload( spec.getSkinPath() );
			}
			buttonBaseWidth += maxW + endX + gapX;
			buttonBaseHeight += endY + gapY;

			setSize( buttonBaseWidth - ( startX + gapX ), buttonBaseHeight );
		}
	}
}
