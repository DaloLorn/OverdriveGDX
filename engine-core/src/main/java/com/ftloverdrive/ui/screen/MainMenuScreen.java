package com.ftloverdrive.ui.screen;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.script.OVDScriptManager;
import com.ftloverdrive.util.OVDConstants;

public class MainMenuScreen implements Disposable, OVDScreen {

	private Logger log;

	private Stage mainStage;

	private boolean renderedPreviousFrame = false;

	private OVDStageManager stageManager = null;
	private OVDEventManager eventManager = null;
	private OVDScriptManager scriptManager = null;

	private OverdriveContext context;


	public MainMenuScreen( OverdriveContext srcContext ) {
		this.context = Pools.get( OverdriveContext.class ).obtain();
		this.context.init( srcContext );
		this.context.setScreen( this );

		log = new Logger( LoadingScreen.class.getCanonicalName(), Logger.INFO );

		stageManager = new OVDStageManager();
		eventManager = new OVDEventManager();
		scriptManager = new OVDScriptManager();

		mainStage = new Stage();
		stageManager.putStage( "Main", mainStage );

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put( "context", context );
		vars.put( "stage", mainStage );

		try {
			FileHandleResolver resolver = context.getFileHandleResolver();
			scriptManager.eval( resolver.resolve( "overdrive-assets/scripts/ui/screen-main-menu.java" ), vars );
		}
		catch ( Exception e ) {
			log.error( "Error evaluating script.", e );
		}
	}

	@Override
	public void render(float delta) {
		if ( renderedPreviousFrame )
			eventManager.secondsElapsed( delta );
		eventManager.processEvents( context );

		Gdx.gl.glClearColor( 0, 0, 0, 0 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		if ( renderedPreviousFrame )
			mainStage.act( delta );

		mainStage.draw();

		renderedPreviousFrame = true;
	}

	@Override
	public void resize(int width, int height) {
		Vector2 scaledView = Scaling.stretch.apply( 800, 400, width, height );
		mainStage.getViewport().update( (int) scaledView.x, (int) scaledView.y, true );
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor( mainStage );
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor( null );
		renderedPreviousFrame = false;
	}

	@Override
	public void pause() {
		renderedPreviousFrame = false;
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		mainStage.dispose();
		context.getAssetManager().unload( OVDConstants.MENU_ATLAS );
		Pools.get( OverdriveContext.class ).free( context );
	}


	@Override
	public OVDStageManager getStageManager() {
		return stageManager;
	}

	@Override
	public OVDEventManager getEventManager() {
		return eventManager;
	}

	@Override
	public OVDScriptManager getScriptManager() {
		return scriptManager;
	}
}
