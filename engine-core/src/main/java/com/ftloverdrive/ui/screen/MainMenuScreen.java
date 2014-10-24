package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventListener;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.event.OVDInputEvent;
import com.ftloverdrive.script.OVDScriptManager;
import com.ftloverdrive.ui.ShatteredImage;

public class MainMenuScreen implements Disposable, OVDScreen {
	
	protected static final String MENU_ATLAS = "img/main_menus/.atlas.atlas";

	private Logger log;
	private TextureAtlas menuAtlas;

	private Stage mainStage;
    private Table menuTable;

	private ShatteredImage bgImage;

    private Button buttonPlay;
    private Button buttonExit;

	private boolean renderedPreviousFrame = false;
	private float elapsed = 0;

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

		Array<String> mainLayerNames = new Array<String>();
		mainLayerNames.add( "Background" );
		mainLayerNames.add( "Menu" );
		
		for ( String layerName : mainLayerNames ) {
			Group tmpGroup = new Group();
			tmpGroup.setName( layerName );
			mainStage.getRoot().addActor( tmpGroup );
		}

		context.getAssetManager().load( MENU_ATLAS, TextureAtlas.class );
		context.getAssetManager().finishLoading();

		menuAtlas = context.getAssetManager().get( MENU_ATLAS, TextureAtlas.class );

		bgImage = new ShatteredImage( menuAtlas.findRegions( "main_base2" ), 5 );
		bgImage.setFillParent( true );
		bgImage.setPosition( 0, 0 );
		((Group) mainStage.getRoot().findActor( "Background" )).addActor( bgImage );
		
		menuTable = new Table();

		TextureRegionDrawable temp;

		// TODO use json or scripts to specify all of this instead of being hard-coded (we're being mod-friendly after all)
		ButtonStyle btnStylePlay = new ButtonStyle();
		btnStylePlay.up = new TextureRegionDrawable( menuAtlas.findRegion( "start-on" ) );
		btnStylePlay.disabled = new TextureRegionDrawable( menuAtlas.findRegion( "start-off" ) );
		temp = new TextureRegionDrawable( menuAtlas.findRegion( "start-select2" ) );
		btnStylePlay.down = temp;
		btnStylePlay.over = temp;
		buttonPlay = new Button( btnStylePlay );
		
		ButtonStyle btnStyleExit = new ButtonStyle();
		btnStyleExit.up = new TextureRegionDrawable( menuAtlas.findRegion( "quit-on" ) );
		btnStyleExit.disabled = new TextureRegionDrawable( menuAtlas.findRegion( "quit-off" ) );
		temp = new TextureRegionDrawable( menuAtlas.findRegion( "quit-select2" ) );
		btnStyleExit.down = temp;
		btnStyleExit.over = temp;
		buttonExit = new Button( btnStyleExit );
		
		buttonPlay.addListener(new ClickListener() {
	        @Override
	        public void clicked( InputEvent event, float x, float y ) {
	        	// TODO forward the event to the EventManager so that it can scrutinize it?
	        	// OVDInputEvent e = Pools.obtain(OVDInputEvent.class);
	        	// e.init(event);
	        	// eventManager.postDelayedEvent(e);
	        	context.getScreenManager().continueToNextScreen();
	        }
	    });

		buttonExit.addListener(new ClickListener() {
	        @Override
	        public void clicked( InputEvent event, float x, float y ) {
	        	System.exit(0);
	        }
	    });

		menuTable.add( buttonPlay ).row();
		menuTable.add( buttonExit ).row();
		menuTable.setFillParent(true);
		// causes the menu to appear to the right, like in FTL -- disable for now
		// menuTable.align(Align.right);

		mainStage.addActor( menuTable );
	}

	@Override
	public void render(float delta) {
		if ( renderedPreviousFrame )
			eventManager.secondsElapsed( delta );
		eventManager.processEvents( context );

		Gdx.gl.glClearColor( 0, 0, 0, 0 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		//

		if ( renderedPreviousFrame )
			mainStage.act( delta );

		mainStage.draw();

		renderedPreviousFrame = true;
	}

	@Override
	public void resize(int width, int height) {
		Vector2 scaledView = Scaling.stretch.apply( 800, 400, width, height );
		mainStage.getViewport().update( (int) scaledView.x, (int) scaledView.y, true );

		// Make the background fill the screen.
		bgImage.setSize( width, height );
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
		context.getAssetManager().unload( MENU_ATLAS );
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
