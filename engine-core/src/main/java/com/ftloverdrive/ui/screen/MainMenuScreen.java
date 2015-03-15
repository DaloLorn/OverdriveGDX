package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.ui.ShatteredImage;
import com.ftloverdrive.util.OVDConstants;


public class MainMenuScreen extends BaseScreen {

	public static final String MAIN_STAGE_ID = "Main";

	protected Stage mainStage;

	private VerticalGroup menuTable = null;


	public MainMenuScreen( OverdriveContext srcContext ) {
		super( srcContext );

		mainStage = new Stage( new ScreenViewport() );
		stageManager.putStage( MAIN_STAGE_ID, mainStage );

		inputMultiplexer.addProcessor( mainStage );

		// Set up layers for later use
		Array<String> mainLayerNames = new Array<String>();
		mainLayerNames.add( "Background" );
		mainLayerNames.add( "Menu" );

		for ( String layerName : mainLayerNames ) {
			Group tmpGroup = new Group();
			tmpGroup.setName( layerName );
			mainStage.getRoot().addActor( tmpGroup );
		}

		// Set up the background image
		TextureAtlas menuAtlas = context.getAssetManager().get( OVDConstants.MENU_ATLAS, TextureAtlas.class );
		ShatteredImage bgImage = new ShatteredImage( menuAtlas.findRegions( "main-base2" ), 5 );
		bgImage.setFillParent( true );

		Group bg = mainStage.getRoot().findActor( "Background" );
		bg.setSize( bgImage.getCompleteWidth(), bgImage.getCompleteHeight() );
		bg.addActor( bgImage );

		// Set up the main menu buttons
		menuTable = new VerticalGroup();
		TextureRegionDrawable temp = null;

		ButtonStyle btnStylePlay = new ButtonStyle();
		btnStylePlay.up = new TextureRegionDrawable( menuAtlas.findRegion( "start-on" ) );
		btnStylePlay.disabled = new TextureRegionDrawable( menuAtlas.findRegion( "start-off" ) );
		temp = new TextureRegionDrawable( menuAtlas.findRegion( "start-select2" ) );
		btnStylePlay.down = temp;
		btnStylePlay.over = temp;
		Button buttonPlay = new Button( btnStylePlay );

		ButtonStyle btnStyleExit = new ButtonStyle();
		btnStyleExit.up = new TextureRegionDrawable( menuAtlas.findRegion( "quit-on" ) );
		btnStyleExit.disabled = new TextureRegionDrawable( menuAtlas.findRegion( "quit-off" ) );
		temp = new TextureRegionDrawable( menuAtlas.findRegion( "quit-select2" ) );
		btnStyleExit.down = temp;
		btnStyleExit.over = temp;
		Button buttonExit = new Button( btnStyleExit );

		// Add the buttons to the stage
		menuTable.addActor( buttonPlay );
		menuTable.addActor( buttonExit );
		menuTable.setFillParent( true );
		menuTable.align( Align.right );
		mainStage.addActor( menuTable );

		buttonPlay.addListener( new ClickListener() {

			public void clicked( InputEvent event, float x, float y ) {
				context.getScreenManager().continueToNextScreen();
			}
		} );

		buttonExit.addListener( new ClickListener() {

			public void clicked( InputEvent event, float x, float y ) {
				System.exit( 0 );
			}
		} );

		resize( getScreenWidth(), getScreenHeight() );
	}

	public void resize( int width, int height ) {
		Group bg = mainStage.getRoot().findActor( "Background" );
		Vector2 scaled = Scaling.stretch.apply( bg.getWidth(), bg.getHeight(), width, height );
		float scaleX = scaled.x / bg.getWidth();
		float scaleY = scaled.y / bg.getHeight();
		bg.setScale( scaleX, scaleY );

		menuTable.setPosition( -65 * scaleX, -250 * scaleY );

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

	public int getScreenWidth() {
		return (int)mainStage.getWidth();
	}

	public int getScreenHeight() {
		return (int)mainStage.getHeight();
	}
}
