package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ftloverdrive.blueprint.ship.TestShipBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.event.TickEvent;
import com.ftloverdrive.event.TickListener;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.handler.GameEventHandler;
import com.ftloverdrive.event.handler.ShipEventHandler;
import com.ftloverdrive.event.handler.TickEventHandler;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.model.DefaultGameModel;
import com.ftloverdrive.model.DefaultPlayerModel;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.model.PlayerModel;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.script.OVDScriptManager;
import com.ftloverdrive.ui.ShatteredImage;
import com.ftloverdrive.ui.hud.PlayerShipDoorHighlighter;
import com.ftloverdrive.ui.hud.PlayerShipHullMonitor;
import com.ftloverdrive.ui.ship.DoorActor;
import com.ftloverdrive.ui.ship.ShipActor;
import com.ftloverdrive.util.OVDConstants;


public class TestScreen implements Disposable, OVDScreen {

	protected static final String PLOT_FONT = "fonts/JustinFont12Bold.ttf?size=13";

	private Logger log;
	private TextureAtlas bgAtlas;
	private TextureAtlas rootAtlas;
	private TextureAtlas miscAtlas;
	private TextureAtlas peopleAtlas;
	private SpriteBatch batch;

	private boolean renderedPreviousFrame = false;
	private float elapsed = 0;

	private OVDStageManager stageManager = null;
	private OVDEventManager eventManager = null;
	private OVDScriptManager scriptManager = null;

	private InputMultiplexer inputMultiplexer;
	private Stage mainStage;
	private Stage hudStage;
	private Stage popupStage;
	private Matrix4 projMatrix;

	private Sprite driftSprite = null;
	private Animation walkAnim = null;
	private PlayerShipHullMonitor playerShipHullMonitor;
	private PlayerShipDoorHighlighter doorHighlighter;
	private ShipActor shipActor;

	private OverdriveContext context;


	public TestScreen( OverdriveContext srcContext ) {
		this.context = Pools.get( OverdriveContext.class ).obtain();
		this.context.init( srcContext );
		this.context.setScreen( this );

		log = new Logger( TestScreen.class.getCanonicalName(), Logger.DEBUG );

		stageManager = new OVDStageManager();
		eventManager = new OVDEventManager();
		scriptManager = new OVDScriptManager();

		// Stages use StretchViewport by default, which causes everything that is drawn
		// on the stage to be stretched to fit the new dimensions.
		// ScreenViewport instead adds more area to the stage, allowing things to retain
		// their original size. The downside is that higher resolutions have an advantage
		// over lower ones (due to more of the game world being visible), but that's
		// not really an issue for a game like FTL.
		// https://github.com/libgdx/libgdx/wiki/Viewports
		mainStage = new Stage( new ScreenViewport() );
		stageManager.putStage( "Main", mainStage );
		hudStage = new Stage( new ScreenViewport() );
		stageManager.putStage( "HUD", hudStage );
		popupStage = new Stage( new ScreenViewport() );
		stageManager.putStage( "Popup", popupStage );

		// These layers are mainly notes. Many will probably be moved inside actors.
		Array<String> mainLayerNames = new Array<String>();
		mainLayerNames.add( "Background" );
		mainLayerNames.add( "BackgroundAccent" );	// Planet.
		mainLayerNames.add( "BackgroundDetail" );	// Asteroids, fleet, etc.
		mainLayerNames.add( "ShipShield" );				// Done: inside ShipActor
		mainLayerNames.add( "ShipGib" );
		mainLayerNames.add( "ShipWeapon" );
		mainLayerNames.add( "ShipBase" );				// Done: inside ShipActor
		mainLayerNames.add( "ShipFloor" );				// Done: inside ShipActor
		mainLayerNames.add( "ShipFloorSheen" );		// Oxygen stripes.
		mainLayerNames.add( "ShipRoomDecor" );			// Done: inside ShipActor
		mainLayerNames.add( "ShipRoomAccent" );		// Terminal.
		mainLayerNames.add( "ShipBreach" );
		mainLayerNames.add( "ShipFire" );
		mainLayerNames.add( "ShipSystemIcon" );
		mainLayerNames.add( "ShipPersonnelDot" );	// Sensor blip or walk target.
		mainLayerNames.add( "ShipPersonnel" );
		mainLayerNames.add( "ShipWall" );				// Done: inside ShipActor
		mainLayerNames.add( "ShipDoor" );				// Done: inside ShipActor
		mainLayerNames.add( "Satellite" );			// Flying drones.
		mainLayerNames.add( "Debris" );				// Crystal lockdown chunks. Explosions.
		for ( String layerName : mainLayerNames ) {
			Group tmpGroup = new Group();
			tmpGroup.setName( layerName );
			mainStage.getRoot().addActor( tmpGroup );
		}

		Array<String> hudLayerNames = new Array<String>();
		hudLayerNames.add( "Warning" );				// "Danger", "Intruders Detected" and "O2 Low".
		hudLayerNames.add( "UnitStatus" );			// Health bars over crew.
		hudLayerNames.add( "ShipDoorHighlight" );
		hudLayerNames.add( "FloatyDamageNumber" );	// When hit.
		hudLayerNames.add( "Beam" );
		hudLayerNames.add( "Reticle" );
		hudLayerNames.add( "CtrlPanel" );
		for ( String layerName : hudLayerNames ) {
			Group tmpGroup = new Group();
			tmpGroup.setName( layerName );
			hudStage.getRoot().addActor( tmpGroup );
		}

		// Layers can be looked up with stage.getRoot().findActor( layerName );
		// A layer can be inserted like this...
		//   Group stageRoot = stage.getRoot();
		//   Group lowerLayer = (Group)stageRoot.findActor( layerName );
		//   stageRoot.addActorAfter( lowerLayer, newGroup );

		context.getAssetManager().load( OVDConstants.BKG_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.ROOT_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.MISC_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.PEOPLE_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( PLOT_FONT, BitmapFont.class );
		context.getAssetManager().finishLoading();

		bgAtlas = context.getAssetManager().get( OVDConstants.BKG_ATLAS, TextureAtlas.class );
		ShatteredImage bgImage = new ShatteredImage( bgAtlas.findRegions( "bg-blueStarcluster" ), 5 );
		bgImage.setFillParent( true );
		Group bg = mainStage.getRoot().findActor( "Background" );
		bg.setSize( bgImage.getCompleteWidth(), bgImage.getCompleteHeight() );
		bg.addActor( bgImage );

		textWindowDemo();
		movingSpriteDemo();
		walkAnimDemo();

		batch = new SpriteBatch();

		int playerRefId = context.getNetManager().requestNewRefId();
		PlayerModel playerModel = new DefaultPlayerModel();
		context.getReferenceManager().addObject( playerModel, playerRefId );
		context.getNetManager().setLocalPlayerRefId( playerRefId );

		int gameRefId = context.getNetManager().requestNewRefId();
		GameModel gameModel = new DefaultGameModel();
		context.getReferenceManager().addObject( gameModel, gameRefId );
		context.setGameModelRefId( gameRefId );

		playerShipHullMonitor = new PlayerShipHullMonitor( context );
		playerShipHullMonitor.setPosition( 0, hudStage.getHeight()-playerShipHullMonitor.getHeight() );
		hudStage.addActor( playerShipHullMonitor );
		
		doorHighlighter = new PlayerShipDoorHighlighter( context );
		doorHighlighter.setVisible( false );
		hudStage.addActor( doorHighlighter );

		shipActor = new ShipActor( context );
		// Ship's offset from the window's top left corner in FTL: X + 350, Y + 170
		// At this point, shipActor's height is 0...
		shipActor.setPosition( 350, mainStage.getHeight() - shipActor.getHeight() - 170 );
		mainStage.addActor( shipActor );

		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor( popupStage );
		inputMultiplexer.addProcessor( hudStage );
		inputMultiplexer.addProcessor( mainStage );

		mainStage.addListener(new InputListener() {
			@Override
			public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
				Actor actor = event.getTarget();
				if ( actor instanceof EventListener )
					return ( (EventListener)actor ).handle( event );
				return false;
			}

			@Override
			public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor ) {
				Actor actor = event.getTarget();
				if ( actor instanceof DoorActor ) {
					float r = actor.getRotation();
					doorHighlighter.setVisible( true );
					Vector2 pos = new Vector2( 0, 0 );
					pos = actor.localToStageCoordinates( pos );
					doorHighlighter.setPosition( pos.x - ( r == 0 ? 0 : doorHighlighter.getWidth() ), pos.y );
					doorHighlighter.setRotation( r );
				}
			}

			@Override
			public void exit( InputEvent event, float x, float y, int pointer, Actor toActor ) {
				Actor actor = event.getTarget();
				if ( actor instanceof DoorActor && event.getPointer() == -1 ) {
					doorHighlighter.setVisible( false );
				}
			}
		});

		// Wire up the event manager...
		TickEventHandler tickHandler = new TickEventHandler();
		for ( Class c : tickHandler.getEventClasses() )
			eventManager.setEventHandler( c, tickHandler );

		GameEventHandler gameHandler = new GameEventHandler();
		for ( Class c : gameHandler.getEventClasses() )
			eventManager.setEventHandler( c, gameHandler );

		ShipEventHandler shipHandler = new ShipEventHandler();
		for ( Class c : shipHandler.getEventClasses() )
			eventManager.setEventHandler( c, shipHandler );

		eventManager.addEventListener( playerShipHullMonitor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( playerShipHullMonitor, ShipPropertyListener.class );

		eventManager.addEventListener( shipActor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( shipActor, ShipPropertyListener.class );

		// When there's a ship, increment its hull after every tick.
		eventManager.addEventListener(new TickListener() {
			@Override
			public void ticksAccumulated( TickEvent e ) {
				//System.out.println( "Tick ("+ e.getTickCount() +")" );

				GameModel gameModel = context.getReferenceManager().getObject( context.getGameModelRefId(), GameModel.class );
				int shipRefId = gameModel.getPlayerShip( context.getNetManager().getLocalPlayerRefId() );
				if ( shipRefId != -1 ) {
					ShipModel shipModel = context.getReferenceManager().getObject( shipRefId, ShipModel.class );
					int hull = shipModel.getProperties().getInt( OVDConstants.HULL );
					int hullMax = shipModel.getProperties().getInt( OVDConstants.HULL_MAX );
					if ( hull < hullMax ) {
						ShipPropertyEvent event = Pools.get( ShipPropertyEvent.class ).obtain();
						event.init( shipRefId, ShipPropertyEvent.INT_TYPE, ShipPropertyEvent.INCREMENT_ACTION, OVDConstants.HULL, 1 );
						context.getScreenEventManager().postDelayedEvent( event );
					}
				}

				// Eventually a PropertyEvent would have an INC_IF_LESS_THAN flag
				// or something. Or maybe a sentinel event listener - set to watch
				// certain property names - vetoing attempts to increment beyond
				// the names' associated maxes?
			}
		}, TickListener.class);


		// Create a test ship.
		int shipRefId = new TestShipBlueprint().createShip( context );

		// Set it as the player's ship.
		GamePlayerShipChangeEvent shipChangeEvent = Pools.get( GamePlayerShipChangeEvent.class ).obtain();
		shipChangeEvent.init( gameRefId, playerRefId, shipRefId );
		eventManager.postDelayedEvent( shipChangeEvent );


		try {
			//FileHandleResolver resolver = context.getFileHandleResolver();
			//scriptManager.eval( resolver.resolve( "script.java" ) );
		}
		catch ( Exception e ) {
			log.error( "Error evaluating script.", e );
		}
	}


	private void textWindowDemo() {
		BitmapFont plotFont = context.getAssetManager().get( PLOT_FONT, BitmapFont.class );

		String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, ";
		loremIpsum += "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
		loremIpsum += "\n\nThis window is draggable.";

		rootAtlas = context.getAssetManager().get( OVDConstants.ROOT_ATLAS, TextureAtlas.class );
		// TODO box_text1 no longer available in AE, use the nearest equivalent
		TextureRegion plotDlgRegion = rootAtlas.findRegion( "window-base-alpha" );
		NinePatchDrawable plotDlgBgDrawable = new NinePatchDrawable( new NinePatch( plotDlgRegion, 22, 22, 36, 22 ) );

		Window plotDlg = new Window( "Test", new Window.WindowStyle( plotFont, new Color( 1f, 1f, 1f, 1f ), plotDlgBgDrawable ) );
		plotDlg.setKeepWithinStage( true );
		plotDlg.setMovable( true );
		plotDlg.setSize( 200, 250 );
		plotDlg.setPosition( 765, 60 );

		plotDlg.row().top().expand().fill();
		Label plotLbl = new Label( loremIpsum, new Label.LabelStyle( plotFont, new Color( 1f, 1f, 1f, 1f ) ) );
		plotLbl.setAlignment( Align.top|Align.left, Align.center|Align.left );
		plotLbl.setWrap( true );
		plotDlg.add( plotLbl );

		// setKeepWithinStage() only applies if added to the stage root. :/
		popupStage.addActor( plotDlg );
	}

	private void movingSpriteDemo() {
		miscAtlas = context.getAssetManager().get( OVDConstants.MISC_ATLAS, TextureAtlas.class );
		driftSprite = miscAtlas.createSprite( "crosshairs-placed" );
	}

	private void walkAnimDemo() {
		peopleAtlas = context.getAssetManager().get( OVDConstants.PEOPLE_ATLAS, TextureAtlas.class );
		// TODO FTL:AE introduced layers to color the base images
		TextureRegion crewRegion = peopleAtlas.findRegion( "human-base" );

		// FTL's animations.xml counts 0-based rows from the bottom.
		TextureRegion[][] tmpFrames = crewRegion.split( 35, 35 );
		TextureRegion[] walkFrames = new TextureRegion[ 4 ];
		int walkStart = 4;
		for ( int i=0; i < walkFrames.length; i++ ) {
			walkFrames[i] = tmpFrames[0][ walkStart + i ];
		}
		walkAnim = new Animation( .3f, walkFrames );
	}


	@Override
	public void resize( int width, int height ) {
		hudStage.getViewport().update( width, height, true );
		mainStage.getViewport().update( width, height, true );
		popupStage.getViewport().update( width, height, true );
		Group bg = mainStage.getRoot().findActor( "Background" );
		Vector2 scaled = Scaling.fill.apply( bg.getWidth(), bg.getHeight(), width, height );
		bg.setScale( Math.min( scaled.x / bg.getWidth(), scaled.y / bg.getHeight() ) );

		// TODO: Re-layout Stages.

		// HUD
		playerShipHullMonitor.setPosition( 0, hudStage.getHeight()-playerShipHullMonitor.getHeight() );
		// Main
		shipActor.setPosition( 350, mainStage.getHeight() - shipActor.getHeight() - 170 );

		// SpriteBatches get resized to match the new aspect ratio,
		// need to counteract this.
		// http://stackoverflow.com/questions/14085212/libgdx-framebuffer-scaling
		projMatrix = new Matrix4();
		projMatrix.setToOrtho2D( 0, 0, width, height );
		batch.setProjectionMatrix( projMatrix );
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor( inputMultiplexer );
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor( null );
		renderedPreviousFrame = false;
	}


	@Override
	public void render( float delta ) {
		if ( renderedPreviousFrame )
			eventManager.secondsElapsed( delta );
		eventManager.processEvents( context );

		Gdx.gl.glClearColor( 0, 0, 0, 0 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		if ( renderedPreviousFrame )
			elapsed += delta;

		mainStage.act( delta );
		mainStage.draw();


		batch.begin();
		if ( driftSprite != null ) {
			driftSprite.setPosition( 100+100*(float)Math.cos(elapsed), 100+25*(float)Math.sin(elapsed) );
			driftSprite.draw( batch );
		}

		if ( walkAnim != null ) {
			TextureRegion walkFrame = walkAnim.getKeyFrame( elapsed, true );
			batch.draw( walkFrame, 50, 50 );
		}
		batch.end();


		hudStage.act( delta );
		hudStage.draw();

		popupStage.act( delta );
		popupStage.draw();

		renderedPreviousFrame = true;
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
		hudStage.dispose();
		playerShipHullMonitor.dispose();
		shipActor.dispose();
		context.getAssetManager().unload( OVDConstants.BKG_ATLAS );
		context.getAssetManager().unload( OVDConstants.ROOT_ATLAS );
		context.getAssetManager().unload( OVDConstants.MISC_ATLAS );
		context.getAssetManager().unload( OVDConstants.PEOPLE_ATLAS );
		context.getAssetManager().unload( PLOT_FONT );
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
