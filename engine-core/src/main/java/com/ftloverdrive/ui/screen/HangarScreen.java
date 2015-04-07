package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.ui.ShatteredImage;
import com.ftloverdrive.util.OVDConstants;


public class HangarScreen extends BaseScreen implements EventListener {

	private Group grpBg = null;

	private ShatteredImage mainFrame = null;
	private Image startFrame = null;
	private Image aeFrame = null;
	private Image achievFrame = null;
	private Image layoutFrame = null;
	private Image shipnameFrame = null;
	private Image[] augBoxes = null;

	private Button btnPlay = null;
	private Button btnEasy = null;
	private Button btnNorm = null;
	private Button btnHard = null;
	private Button btnRename = null;
	private Button btnEnableAE = null;
	private Button btnDisableAE = null;


	public HangarScreen( OverdriveContext srcContext ) {
		super( srcContext );

		// Set up layers for later use
		Array<String> mainLayerNames = new Array<String>();
		mainLayerNames.add( "Background" );

		for ( String layerName : mainLayerNames ) {
			Group tmpGroup = new Group();
			tmpGroup.setName( layerName );
			mainStage.getRoot().addActor( tmpGroup );
		}

		context.getAssetManager().load( OVDConstants.HANGARUI_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.CUSTOM_HANGAR_ATLAS, TextureAtlas.class );
		context.getAssetManager().finishLoading();

		TextureAtlas hangarAtlas = context.getAssetManager().get( OVDConstants.HANGARUI_ATLAS, TextureAtlas.class );
		TextureAtlas hangarAtlas2 = context.getAssetManager().get( OVDConstants.CUSTOM_HANGAR_ATLAS, TextureAtlas.class );

		// Set up the background image
		/*
		 * ShatteredImages need to be put inside of a Group in order for them to render correctly,
		 * since changing the size of a ShatteredImage directly creates blank spaces between the
		 * image's tiles at some resolutions (this is due to the way Table displays its content)
		 * Group's scaling is (presumably) applied after Table's size, therefore avoids this problem.
		 */
		ShatteredImage bgImage = new ShatteredImage( hangarAtlas2.findRegions( "hangar-bg" ), 5 );
		bgImage.setFillParent( true );

		grpBg = mainStage.getRoot().findActor( "Background" );
		grpBg.setSize( bgImage.getCompleteWidth(), bgImage.getCompleteHeight() );
		grpBg.addActor( bgImage );

		// Set up UI frames
		mainFrame = new ShatteredImage( hangarAtlas2.findRegions( "main-frame" ), 5 );
		mainFrame.setSize( mainFrame.getCompleteWidth(), mainFrame.getCompleteHeight() );
		mainStage.addActor( mainFrame );

		startFrame = new Image( hangarAtlas2.findRegion( "start-frame" ) );
		mainStage.addActor( startFrame );

		aeFrame = new Image( hangarAtlas2.findRegion( "ae-frame" ) );
		mainStage.addActor( aeFrame );

		achievFrame = new Image( hangarAtlas2.findRegion( "achiev-frame" ) );
		mainStage.addActor( achievFrame );

		layoutFrame = new Image( hangarAtlas2.findRegion( "layout-frame" ) );
		mainStage.addActor( layoutFrame );

		shipnameFrame = new Image( hangarAtlas.findRegion( "box-shipname" ) );
		mainStage.addActor( shipnameFrame );

		augBoxes = new Image[3];
		TextureRegion augBox = hangarAtlas2.findRegion( "aug-box" );
		NinePatch augBoxPatch = new NinePatch( augBox, 4, 4, 4, 4 );
		for ( int i = 0; i < augBoxes.length; ++i ) {
			augBoxes[i] = new Image( augBoxPatch );
			augBoxes[i].setSize( 235, 40 );
			mainStage.addActor( augBoxes[i] );
		}

		// Set up the buttons
		btnPlay = createButton( hangarAtlas, "button-start-on", "button-start-select2" );
		mainStage.addActor( btnPlay );

		btnEasy = createButton( hangarAtlas, "button-easy-on", "button-easy-off", "button-easy-select2", "button-easy-select2",
				"button-easy-select2", "button-easy-select2" );
		mainStage.addActor( btnEasy );
		btnNorm = createButton( hangarAtlas, "button-normal-on", "button-normal-off", "button-normal-select2", "button-normal-select2",
				"button-normal-select2", "button-normal-select2" );
		mainStage.addActor( btnNorm );
		btnHard = createButton( hangarAtlas, "button-hard-on", "button-hard-off", "button-hard-select2", "button-hard-select2",
				"button-hard-select2", "button-hard-select2" );
		mainStage.addActor( btnHard );

		ButtonGroup btngDiff = new ButtonGroup();
		btngDiff.add( btnEasy );
		btngDiff.add( btnNorm );
		btngDiff.add( btnHard );
		btnNorm.setChecked( true );

		btnRename = createButton( hangarAtlas, "button-name-on", null, "button-name-select2", "button-name-select2",
				"button-name-select2", "button-name-select2" );
		mainStage.addActor( btnRename );

		btnEnableAE = createButton( hangarAtlas, "button-enabled-on", null, "button-enabled-select2", "button-enabled-select2",
				"button-enabled-select2", "button-enabled-select2" );
		mainStage.addActor( btnEnableAE );
		btnDisableAE = createButton( hangarAtlas, "button-disabled-on", null, "button-disabled-select2", "button-disabled-select2",
				"button-disabled-select2", "button-disabled-select2" );
		mainStage.addActor( btnDisableAE );

		ButtonGroup btngAE = new ButtonGroup();
		btngAE.add( btnEnableAE );
		btngAE.add( btnDisableAE );
		btnEnableAE.setChecked( true ); // TODO: Config setting.

		mainStage.addListener( this );
		btnPlay.addListener( new ClickListener() {

			public void clicked( InputEvent event, float x, float y ) {
				context.getScreenManager().continueToNextScreen();
			}
		} );

		resize( getScreenWidth(), getScreenHeight() );
	}

	public void resize( int width, int height ) {
		Vector2 scaled = Scaling.stretch.apply( grpBg.getWidth(), grpBg.getHeight(), width, height );
		float scaleX = scaled.x / grpBg.getWidth();
		float scaleY = scaled.y / grpBg.getHeight();
		grpBg.setScale( scaleX, scaleY );


		mainFrame.setPosition( ( width - mainFrame.getWidth() ) / 2, 0 );
		for ( int i = 0; i < augBoxes.length; ++i ) {
			augBoxes[i].setPosition( width - mainFrame.getX() - 172 - augBoxes[i].getWidth() / 2, 35 + i * ( augBoxes[i].getHeight() + 15 ) );
		}

		startFrame.setPosition( width - startFrame.getWidth() - 33, height - startFrame.getHeight() - 2 );
		btnPlay.setPosition( startFrame.getX() + 117, startFrame.getY() + 26 );
		btnEasy.setPosition( startFrame.getX() + 12, startFrame.getY() + 64 );
		btnNorm.setPosition( startFrame.getX() + 12, startFrame.getY() + 38 );
		btnHard.setPosition( startFrame.getX() + 12, startFrame.getY() + 12 );

		float t = mainFrame.getHeight();
		achievFrame.setPosition( 0, t + ( t * scaleY - t ) - 5 );
		layoutFrame.setPosition( 0, achievFrame.getY() + achievFrame.getHeight() + 15 );

		aeFrame.setPosition( width - aeFrame.getWidth() - 40, t + ( t * scaleY - t ) + 20 );
		btnDisableAE.setPosition( aeFrame.getX() + 13, aeFrame.getY() + 13 );
		btnEnableAE.setPosition( aeFrame.getX() + 147, aeFrame.getY() + 13 );

		shipnameFrame.setPosition( 5, height - shipnameFrame.getHeight() - 5 );
		btnRename.setPosition( shipnameFrame.getX() + 8, shipnameFrame.getY() + 8 );

		mainStage.getViewport().update( width, height, true );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );

		if ( renderedPreviousFrame ) {
			mainStage.act( delta );
		}

		mainStage.draw();

		renderedPreviousFrame = true;
	}

	@Override
	public void resume() {
	}

	@Override
	public int getScreenWidth() {
		return (int)mainStage.getWidth();
	}

	@Override
	public int getScreenHeight() {
		return (int)mainStage.getHeight();
	}

	@Override
	public boolean handle( Event e ) {
		if ( e instanceof InputEvent ) {
			InputEvent event = (InputEvent)e;

			if ( event.getType() == Type.keyDown ) {
				if ( event.getKeyCode() == Keys.ESCAPE ) {
					context.getScreenManager().returnToPrevScreen();
				}
			}
		}
		return false;
	}
}
