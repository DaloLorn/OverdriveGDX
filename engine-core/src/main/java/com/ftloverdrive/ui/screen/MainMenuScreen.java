package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ftloverdrive.core.OverdriveContext;

public class MainMenuScreen extends AbstractScriptedScreen implements Disposable, OVDScreen {

	private Stage mainStage;

	private boolean renderedPreviousFrame = false;


	public MainMenuScreen( OverdriveContext srcContext ) {
		super( srcContext );

		mainStage = new Stage(new ScreenViewport());
		stageManager.putStage( "Main", mainStage );

		screenNameSpace = scriptManager.requestNewNameSpace( "MainMenuNameSpace" );
		try {
			screenNameSpace.setVariable( "stage", mainStage, false );
		} catch (Exception e) {
			log.error( "Error occured while setting variable.", e );
		}

		runScript( "overdrive-assets/scripts/ui/screens/main-menu.java" );
	}

	@Override
	public void resize( int width, int height ) {
		mainStage.getViewport().update( width, height, true );
		super.resize( width, height );
	}

	@Override
	public void render( float delta ) {
		if ( renderedPreviousFrame )
			eventManager.secondsElapsed( delta );
		eventManager.processEvents( context );

		Gdx.gl.glClearColor( 0, 0, 0, 0 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		if ( renderedPreviousFrame )
			mainStage.act( delta );

		mainStage.draw();
		super.render( delta );

		renderedPreviousFrame = true;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor( mainStage );
		super.show();
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor( null );
		renderedPreviousFrame = false;
		super.hide();
	}

	@Override
	public void pause() {
		renderedPreviousFrame = false;
		super.pause();
	}

	@Override
	public void dispose() {
		super.dispose();
		mainStage.dispose();
		Pools.get( OverdriveContext.class ).free( context );
	}
}
