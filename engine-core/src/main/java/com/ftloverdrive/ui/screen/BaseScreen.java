package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.script.OVDScriptManager;


/**
 * Contains most of the boilerplate code and default implementations for other screens to use.
 */
public abstract class BaseScreen implements Disposable, OVDScreen {

	public static final String MAIN_STAGE_ID = "mainStage";
	public static final String HUD_STAGE_ID = "hudStage";
	public static final String POPUP_STAGE_ID = "popupStage";

	protected Logger log;

	protected OVDStageManager stageManager = null;
	protected OVDEventManager eventManager = null;
	protected OVDScriptManager scriptManager = null;

	protected InputMultiplexer inputMultiplexer;

	protected OverdriveContext context;

	protected Stage mainStage;
	protected Stage hudStage;
	protected Stage popupStage;

	protected boolean renderedPreviousFrame = false;
	protected float elapsed = 0;


	public BaseScreen( OverdriveContext srcContext ) {
		this.context = Pools.get( OverdriveContext.class ).obtain();
		this.context.init( srcContext );
		this.context.setScreen( this );

		log = new Logger( getClass().getCanonicalName(), Logger.INFO );

		stageManager = new OVDStageManager();
		eventManager = new OVDEventManager();
		scriptManager = new OVDScriptManager();
		inputMultiplexer = new InputMultiplexer();

		// Stages use StretchViewport by default, which causes everything that is drawn
		// on the stage to be stretched to fit the new dimensions.
		// ScreenViewport instead adds more area to the stage, allowing things to retain
		// their original size. The downside is that higher resolutions have an advantage
		// over lower ones (due to more of the game world being visible), but that's not
		// really an issue for a game like FTL.
		// https://github.com/libgdx/libgdx/wiki/Viewports
		mainStage = new Stage( new ScreenViewport() );
		stageManager.putStage( MAIN_STAGE_ID, mainStage );
		hudStage = new Stage( new ScreenViewport() );
		stageManager.putStage( HUD_STAGE_ID, hudStage );
		popupStage = new Stage( new ScreenViewport() );
		stageManager.putStage( POPUP_STAGE_ID, popupStage );

		// Add the stages in reverse order -- that way the higher-up stage intercepts events
		inputMultiplexer.addProcessor( popupStage );
		inputMultiplexer.addProcessor( hudStage );
		inputMultiplexer.addProcessor( mainStage );
	}

	public void render( float delta ) {
		if ( renderedPreviousFrame )
			getEventManager().secondsElapsed( delta );
		getEventManager().processEvents( context );

		Gdx.gl.glClearColor( 0, 0, 0, 0 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		if ( renderedPreviousFrame )
			elapsed += delta;
	}

	public void show() {
		Gdx.input.setInputProcessor( inputMultiplexer );
	}

	public void hide() {
		Gdx.input.setInputProcessor( null );
		renderedPreviousFrame = false;
	}

	public void pause() {
		renderedPreviousFrame = false;
	}

	public void resume() {
	}

	/**
	 * Unloads all resources loaded by the script, and clears the screens namespace, if it exists.
	 */
	public void dispose() {
		Pools.get( OverdriveContext.class ).free( context );
	}

	public OVDStageManager getStageManager() {
		return stageManager;
	}

	public OVDEventManager getEventManager() {
		return eventManager;
	}

	public OVDScriptManager getScriptManager() {
		return scriptManager;
	}

	/*
	 * =============================================================
	 */

	protected static Button createPushButton( TextureAtlas atlas, String up, String down, String over ) {
		ButtonStyle style = new ButtonStyle();

		String[] args = new String[] { up, down, over };
		TextureRegionDrawable[] temp = new TextureRegionDrawable[args.length];
		initTextures( atlas, args, temp );

		style.up = temp[0];
		style.down = temp[1];
		style.over = temp[2];

		return new Button( style );
	}

	protected static Button createCheckButton( TextureAtlas atlas, String up, String down, String over, String checked ) {
		ButtonStyle style = new ButtonStyle();

		String[] args = new String[] { up, down, over, checked };
		TextureRegionDrawable[] temp = new TextureRegionDrawable[args.length];
		initTextures( atlas, args, temp );

		style.up = temp[0];
		style.down = temp[1];
		style.over = temp[2];
		style.checked = temp[3];

		return new Button( style );
	}

	private static void initTextures( TextureAtlas atlas, String[] regionNames, TextureRegionDrawable[] drawables ) {
		for ( int i = 0; i < drawables.length; ++i ) {
			drawables[i] = new TextureRegionDrawable( atlas.findRegion( regionNames[i] ) );
		}
		for ( int i = 0; i < drawables.length; ++i ) {
			for ( int j = i; j < drawables.length; ++j ) {
				if ( i != j && regionNames[i].equals( regionNames[j] ) ) {
					drawables[j] = drawables[i];
				}
			}
		}
	}
}
