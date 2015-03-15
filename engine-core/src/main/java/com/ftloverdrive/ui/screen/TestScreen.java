package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ftloverdrive.blueprint.ship.TestShipBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.TickEvent;
import com.ftloverdrive.event.TickListener;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.handler.DoorEventHandler;
import com.ftloverdrive.event.handler.GameEventHandler;
import com.ftloverdrive.event.handler.LocalEventHandler;
import com.ftloverdrive.event.handler.ShipEventHandler;
import com.ftloverdrive.event.handler.TickEventHandler;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.model.DefaultGameModel;
import com.ftloverdrive.model.DefaultPlayerModel;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.model.PlayerModel;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.ui.ShatteredImage;
import com.ftloverdrive.ui.hud.PlayerShipDoorHighlighter;
import com.ftloverdrive.ui.hud.PlayerShipHullMonitor;
import com.ftloverdrive.ui.ship.CrewActor;
import com.ftloverdrive.ui.ship.ShipActor;
import com.ftloverdrive.util.OVDConstants;


public class TestScreen extends BaseScreen {

	protected static final String PLOT_FONT = "fonts/JustinFont12Bold.ttf?size=13";

	private TextureAtlas bgAtlas;
	private TextureAtlas rootAtlas;
	private TextureAtlas miscAtlas;

	private Stage mainStage;
	private Stage hudStage;
	private Stage popupStage;
	private Matrix4 projMatrix;

	private PlayerShipHullMonitor playerShipHullMonitor;
	private PlayerShipDoorHighlighter doorHighlighter;
	private ShipActor shipActor;

	private CrewActor selectedActor = null;


	public TestScreen( OverdriveContext srcContext ) {
		super( srcContext );

		// Stages use StretchViewport by default, which causes everything that is drawn
		// on the stage to be stretched to fit the new dimensions.
		// ScreenViewport instead adds more area to the stage, allowing things to retain
		// their original size. The downside is that higher resolutions have an advantage
		// over lower ones (due to more of the game world being visible), but that's not
		// really an issue for a game like FTL.
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
		mainLayerNames.add( "BackgroundAccent" ); // Planet.
		mainLayerNames.add( "BackgroundDetail" ); // Asteroids, fleet, etc.
		mainLayerNames.add( "ShipShield" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipWeapon" );
		mainLayerNames.add( "ShipGib" );
		mainLayerNames.add( "ShipBase" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipFloor" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipFloorSheen" ); // Oxygen stripes.
		mainLayerNames.add( "ShipRoomDecor" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipRoomAccent" ); // Terminal.
		mainLayerNames.add( "ShipBreach" );
		mainLayerNames.add( "ShipFire" );
		mainLayerNames.add( "ShipSystemIcon" );
		mainLayerNames.add( "ShipPersonnelDot" ); // Sensor blip or walk target.
		mainLayerNames.add( "ShipPersonnel" );
		mainLayerNames.add( "ShipWall" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipDoor" ); // Done: inside ShipActor
		mainLayerNames.add( "Satellite" ); // Flying drones.
		mainLayerNames.add( "Debris" ); // Crystal lockdown chunks. Explosions.
		for ( String layerName : mainLayerNames ) {
			Group tmpGroup = new Group();
			tmpGroup.setName( layerName );
			mainStage.getRoot().addActor( tmpGroup );
		}

		Array<String> hudLayerNames = new Array<String>();
		hudLayerNames.add( "Warning" ); // "Danger", "Intruders Detected" and "O2 Low".
		hudLayerNames.add( "UnitStatus" ); // Health bars over crew.
		hudLayerNames.add( "ShipDoorHighlight" ); // Done: PlayerShipDoorHighlighter
		hudLayerNames.add( "FloatyDamageNumber" ); // When hit.
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
		// Group stageRoot = stage.getRoot();
		// Group lowerLayer = (Group)stageRoot.findActor( layerName );
		// stageRoot.addActorAfter( lowerLayer, newGroup );

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

		int playerRefId = context.getNetManager().requestNewRefId();
		PlayerModel playerModel = new DefaultPlayerModel();
		context.getReferenceManager().addObject( playerModel, playerRefId );
		context.getNetManager().setLocalPlayerRefId( playerRefId );

		int gameRefId = context.getNetManager().requestNewRefId();
		GameModel gameModel = new DefaultGameModel();
		context.getReferenceManager().addObject( gameModel, gameRefId );
		context.setGameModelRefId( gameRefId );

		playerShipHullMonitor = new PlayerShipHullMonitor( context );
		playerShipHullMonitor.setPosition( 0, hudStage.getHeight() - playerShipHullMonitor.getHeight() );
		hudStage.addActor( playerShipHullMonitor );

		doorHighlighter = new PlayerShipDoorHighlighter( context );
		doorHighlighter.setVisible( false );
		hudStage.addActor( doorHighlighter );
		mainStage.addListener( doorHighlighter );

		shipActor = new ShipActor( context );
		// Ship's offset from the window's top left corner in FTL: X + 350, Y + 170
		// At this point, shipActor's height is 0...
		shipActor.setPosition( 350, mainStage.getHeight() - shipActor.getHeight() - 170 );
		mainStage.addActor( shipActor );
		mainStage.addListener( shipActor );

		inputMultiplexer.addProcessor( popupStage );
		inputMultiplexer.addProcessor( hudStage );
		inputMultiplexer.addProcessor( mainStage );


		// Wire up the event manager...
		TickEventHandler tickHandler = new TickEventHandler();
		for ( Class c : tickHandler.getEventClasses() )
			eventManager.setEventHandler( c, tickHandler );

		LocalEventHandler localHandler = new LocalEventHandler();
		for ( Class c : localHandler.getEventClasses() )
			eventManager.setEventHandler( c, localHandler );

		GameEventHandler gameHandler = new GameEventHandler();
		for ( Class c : gameHandler.getEventClasses() )
			eventManager.setEventHandler( c, gameHandler );

		ShipEventHandler shipHandler = new ShipEventHandler();
		for ( Class c : shipHandler.getEventClasses() )
			eventManager.setEventHandler( c, shipHandler );

		DoorEventHandler doorHandler = new DoorEventHandler();
		for ( Class c : doorHandler.getEventClasses() )
			eventManager.setEventHandler( c, doorHandler );

		eventManager.addEventListener( playerShipHullMonitor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( playerShipHullMonitor, ShipPropertyListener.class );

		eventManager.addEventListener( shipActor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( shipActor, ShipPropertyListener.class );

		// When there's a ship, increment its hull after every tick.
		eventManager.addEventListener( new TickListener() {

			@Override
			public void ticksAccumulated( TickEvent e ) {
				// System.out.println( "Tick ("+ e.getTickCount() +")" );

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
		}, TickListener.class );

		// Create a test ship.
		int shipRefId = new TestShipBlueprint( null ).createShip( context );

		// Set it as the player's ship.
		GamePlayerShipChangeEvent shipChangeEvent = Pools.get( GamePlayerShipChangeEvent.class ).obtain();
		shipChangeEvent.init( gameRefId, playerRefId, shipRefId );
		eventManager.postDelayedEvent( shipChangeEvent );

		resize( getScreenWidth(), getScreenHeight() );
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
		plotLbl.setAlignment( Align.top | Align.left, Align.center | Align.left );
		plotLbl.setWrap( true );
		plotDlg.add( plotLbl );

		// setKeepWithinStage() only applies if added to the stage root. :/
		popupStage.addActor( plotDlg );
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
		playerShipHullMonitor.setPosition( 0, hudStage.getHeight() - playerShipHullMonitor.getHeight() );
		// Main
		shipActor.setPosition( 350, mainStage.getHeight() - shipActor.getHeight() - 170 );

		// SpriteBatches get resized to match the new aspect ratio,
		// need to counteract this.
		// http://stackoverflow.com/questions/14085212/libgdx-framebuffer-scaling
		// projMatrix = new Matrix4();
		// projMatrix.setToOrtho2D( 0, 0, width, height );
		// batch.setProjectionMatrix( projMatrix );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );

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
	public int getScreenWidth() {
		return (int)mainStage.getWidth();
	}

	@Override
	public int getScreenHeight() {
		return (int)mainStage.getHeight();
	}
}
