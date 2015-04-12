package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Predicate;
import com.badlogic.gdx.utils.Scaling;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.handler.EngineEventHandler;


/**
 * Intermediate screen used to synchronize the client with the server.
 *
 */
public class ConnectScreen extends BaseScreen {

	private static final Predicate<OverdriveContext> defaultCondition =
			new Predicate<OverdriveContext>() {

				public boolean evaluate( OverdriveContext context ) {
					return true;
				}
			};

	private Predicate<OverdriveContext> condition = defaultCondition;
	private String nextScreenKey = null;

	private AssetManager loadingAssetManager;


	public ConnectScreen( OverdriveContext srcContext ) {
		super( srcContext, false );

		EngineEventHandler engineHandler = new EngineEventHandler();
		for ( Class c : engineHandler.getEventClasses() )
			eventManager.setEventHandler( c, engineHandler );

		loadingAssetManager = new AssetManager();
		loadingAssetManager.load( "overdrive-assets/images/loading.atlas", TextureAtlas.class );
		loadingAssetManager.finishLoading(); // Block until loaded completely.

		TextureAtlas atlas = loadingAssetManager.get( "overdrive-assets/images/loading.atlas", TextureAtlas.class );
		Image screenBg = new Image( atlas.findRegion( "screen-bg" ) );
		screenBg.setFillParent( true );

		mainStage.addActor( screenBg );
	}

	/**
	 * Sets the condition that has to be met in order to advance to the next screen.
	 */
	public void setCondition( Predicate<OverdriveContext> condition ) {
		if ( condition == null )
			condition = defaultCondition;
		this.condition = condition;
	}

	/**
	 * Sets the screen to be shown after this one.
	 */
	public void setNextScreenKey( String screenKey ) {
		nextScreenKey = screenKey;
	}

	@Override
	public void resize( int width, int height ) {
		Vector2 scaledView = Scaling.stretch.apply( 800, 400, width, height );
		mainStage.getViewport().update( (int)scaledView.x, (int)scaledView.y, true );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );

		if ( condition.evaluate( context ) ) {
			context.getScreenManager().showScreen( nextScreenKey );
		}

		mainStage.act( delta );
		mainStage.draw();

		hudStage.act( delta );
		hudStage.draw();

		popupStage.act( delta );
		popupStage.draw();

		renderedPreviousFrame = true;
	}

	@Override
	public void dispose() {
		super.dispose();
		loadingAssetManager.dispose();
	}

	@Override
	public int getScreenWidth() {
		return (int)mainStage.getWidth();
	}

	@Override
	public int getScreenHeight() {
		return (int)mainStage.getHeight();
	}
}
