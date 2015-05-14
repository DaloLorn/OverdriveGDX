// Based on Matsemann's demo.
//   https://github.com/Matsemann/libgdx-loading-screen
// The demo for Libgdx 1.4.1:
// https://github.com/Matsemann/libgdx-loading-screen/tree/libgdx1.2.0-EyeOfMidas

package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ftloverdrive.blueprint.ship.EngineSystemBlueprint;
import com.ftloverdrive.blueprint.ship.ShieldSystemBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.model.system.EngineSystemModel;
import com.ftloverdrive.model.system.ShieldSystemModel;
import com.ftloverdrive.script.OVDScriptManager;
import com.ftloverdrive.util.OVDConstants;


public class LoadingScreen implements Disposable, OVDScreen {

	private Logger log;

	private AssetManager loadingAssetManager;
	private TextureAtlas atlas;

	private OVDStageManager stageManager = null;
	private OVDEventManager eventManager = null;
	private OVDScriptManager scriptManager = null;

	private Stage mainStage;

	private Image logo;
	private Image loadingFrame;
	private Image loadingBarHidden;
	private Image screenBg;
	private Image loadingBg;
	private Actor loadingBar;

	private float startX, endX;
	private float progress, percent;
	private boolean renderedPreviousFrame = false;

	private OverdriveContext context;


	public LoadingScreen( OverdriveContext srcContext ) {
		this.context = Pools.get( OverdriveContext.class ).obtain();
		this.context.init( srcContext );
		this.context.setScreen( this );

		log = new Logger( LoadingScreen.class.getCanonicalName(), Logger.INFO );

		stageManager = new OVDStageManager();
		eventManager = new OVDEventManager();
		scriptManager = new OVDScriptManager();


		mainStage = new Stage( new ScreenViewport() );
		stageManager.putStage( "Main", mainStage );

		loadingAssetManager = new AssetManager();
		loadingAssetManager.load( "overdrive-assets/images/loading.atlas", TextureAtlas.class );
		loadingAssetManager.finishLoading(); // Block until loaded completely.

		atlas = loadingAssetManager.get( "overdrive-assets/images/loading.atlas", TextureAtlas.class );
		logo = new Image( atlas.findRegion( "libgdx-logo" ) );
		loadingFrame = new Image( atlas.findRegion( "loading-frame" ) );
		loadingBarHidden = new Image( atlas.findRegion( "loading-bar-hidden" ) );
		screenBg = new Image( atlas.findRegion( "screen-bg" ) );
		loadingBg = new Image( atlas.findRegion( "loading-frame-bg" ) );

		screenBg.setFillParent( true );

		// Animated bar.
		// Animation anim = new Animation( 0.05f, atlas.findRegions("loading-bar-anim") );
		// anim.setPlayMode(Animation.LOOP_REVERSED);
		// loadingBar = new LoadingBar(anim);

		// Static bar.
		loadingBar = new Image( atlas.findRegion( "loading-bar1" ) );

		mainStage.addActor( screenBg );
		mainStage.addActor( loadingBar );
		mainStage.addActor( loadingBg );
		mainStage.addActor( loadingBarHidden );
		mainStage.addActor( loadingFrame );
		// mainStage.addActor( logo );

		// Set hardware cursor.
		// TODO: With libGDX, setting hardware cursor is clumsy -- requires pixmaps, and pixmaps
		// can't be conveniently created, they need to be transcribed pixel by pixel.
		// TODO: Will probably need some sort of cental Cursor class that stores loaded pixmaps, and
		// exposes methods to change the cursor's appearance, since in FTL the cursor changes depending
		// on the actor currently being hoevered over.
		// TODO: Will probably need to use a software cursor anyway, since FTL had an animated
		// cursor when opening doors.
		context.getAssetManager().load( OVDConstants.MOUSE_ATLAS, TextureAtlas.class );
		context.getAssetManager().finishLoading();
		TextureAtlas mouseAtlas = context.getAssetManager().get( OVDConstants.MOUSE_ATLAS, TextureAtlas.class );
		// It appears that the cursor is positioned wrong -- buttons don't detect it properly near
		// their bottom/right edge. Fix by drawing the pixels at an offset.
		Pixmap pointerInvalid = loadPixmap( mouseAtlas.findRegion( "pointerInvalid" ), 0, 3 );
		Gdx.input.setCursorImage( pointerInvalid, 0, 0 );

		// Uncomment to preload textures.
		// context.getAssetManager().load( OVDConstants.BUTTONS_FTL_ATLAS, TextureAtlas.class );
		// context.getAssetManager().load( OVDConstants.COMBATUI_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.EFFECTS_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.ICONS_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.PEOPLE_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.SHIP_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.SHIP_INTERIOR_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.STATUSUI_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.SYSTEMUI_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.BKG_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.ROOT_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.MISC_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.MENU_ATLAS, TextureAtlas.class );
		// context.getAssetManager().load( OVDConstants.WEAPONS_ATLAS, TextureAtlas.class );

		context.getBlueprintManager().storeBlueprint( ShieldSystemBlueprint.class.getSimpleName(), new ShieldSystemBlueprint() );
		context.getBlueprintManager().associateModel( ShieldSystemBlueprint.class.getSimpleName(), ShieldSystemModel.class );
		context.getBlueprintManager().storeBlueprint( EngineSystemBlueprint.class.getSimpleName(), new EngineSystemBlueprint() );
		context.getBlueprintManager().associateModel( EngineSystemBlueprint.class.getSimpleName(), EngineSystemModel.class );

		// XXX: Re-enable once scripts are actually needed for something
		// Preload scripts.
		/*
		FileHandle scriptsFolder = context.getFileHandleResolver().resolve( "overdrive-assets/scripts" );
		FilenameFilter scriptFilter = new FilenameFilter() {

			@Override
			public boolean accept( File dir, String name ) {
				return name.endsWith( ".java" ) || name.endsWith( ".bsh" );
			}
		};
		Array<FileHandle> scriptHandles = new Array<FileHandle>();
		getHandles( scriptsFolder, scriptHandles );
		for ( FileHandle scriptFile : scriptHandles ) {
			if ( scriptFilter.accept( null, scriptFile.name() ) ) {
				context.getAssetManager().load( scriptFile.path(), ScriptResource.class );
			}
		}
		*/
	}

	/**
	 * Recursively scans all subdirectories, starting at the handle provided in argument,
	 * and adds files contained within them to the list passed in argument.
	 * 
	 * Note: folders located within the .jar show up as having 0 children.
	 */
	private void getHandles( FileHandle begin, Array<FileHandle> handles ) {
		FileHandle[] newHandles = begin.list();
		for ( FileHandle fh : newHandles ) {
			if ( fh.isDirectory() ) {
				getHandles( fh, handles );
			}
			else {
				handles.add( fh );
			}
		}
	}

	private Pixmap loadPixmap( TextureRegion region, int ox, int oy ) {
		Texture texture = region.getTexture();
		if ( !texture.getTextureData().isPrepared() )
			texture.getTextureData().prepare();
		Pixmap pixmap = texture.getTextureData().consumePixmap();
		Pixmap result = new Pixmap( region.getRegionWidth(), region.getRegionHeight(), Format.RGBA8888 );
		for ( int x = 0; x < region.getRegionWidth(); x++ ) {
			for ( int y = 0; y < region.getRegionHeight(); y++ ) {
				int colorInt = pixmap.getPixel( region.getRegionX() + x, region.getRegionY() + y );
				result.setColor( colorInt );
				result.drawPixel( x + ox, y + oy );
			}
		}
		pixmap.dispose();

		return result;
	}

	@Override
	public void resize( int width, int height ) {
		Vector2 scaledView = Scaling.stretch.apply( 800, 400, width, height );
		mainStage.getViewport().update( (int)scaledView.x, (int)scaledView.y, true );

		// Make the background fill the screen.
		// screenBg.setSize(scaledView.x, scaledView.y);
		// screenBg.setX( 0 );
		// screenBg.setY( 0 );

		// Place the logo in the middle of the screen and 100 px up.
		logo.setX( ( scaledView.x - logo.getWidth() ) / 2 );
		logo.setY( ( scaledView.y - logo.getHeight() ) / 2 + 100 );

		// Place the loading frame in the middle of the screen.
		loadingFrame.setX( ( mainStage.getWidth() - loadingFrame.getWidth() ) / 2 );
		loadingFrame.setY( ( mainStage.getHeight() - loadingFrame.getHeight() ) / 2 );

		// Place the loading bar at the same spot as the frame, adjusted a few px.
		loadingBar.setX( loadingFrame.getX() + 15 );
		loadingBar.setY( loadingFrame.getY() + 5 );

		// Place the image that will hide the bar on top of the bar, adjusted a few px.
		loadingBarHidden.setX( loadingBar.getX() + 35 );
		loadingBarHidden.setY( loadingBar.getY() - 3 );
		// The start position and how far to move the hidden loading bar.
		startX = loadingBarHidden.getX();
		endX = 440;

		// The rest of the hidden bar.
		loadingBg.setSize( 450, 50 );
		loadingBg.setX( loadingBarHidden.getX() + 30 );
		loadingBg.setY( loadingBarHidden.getY() + 3 );
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
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0, 0, 0, 0 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		// Incrementally load assets until completely done.
		if ( context.getAssetManager().update() ) {
			// if ( Gdx.input.isTouched() )
			context.getScreenManager().continueToNextScreen();
		}

		// Interpolate the percentage to make it more smooth.
		progress = context.getAssetManager().getProgress();
		percent = Interpolation.linear.apply( percent, progress, 0.1f );

		loadingBarHidden.setX( startX + endX * percent );
		loadingBg.setX( loadingBarHidden.getX() + 30 );
		loadingBg.setWidth( 450 - 450 * percent );
		loadingBg.invalidate();

		if ( renderedPreviousFrame )
			mainStage.act( delta );

		mainStage.draw();

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
		mainStage.dispose();
		loadingAssetManager.dispose();
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

	@Override
	public int getScreenWidth() {
		return (int)mainStage.getWidth();
	}

	@Override
	public int getScreenHeight() {
		return (int)mainStage.getHeight();
	}
}
