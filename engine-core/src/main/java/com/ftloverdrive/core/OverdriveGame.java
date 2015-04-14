package com.ftloverdrive.core;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Logger;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ftloverdrive.blueprint.OVDBlueprintManager;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.io.FreeTypeFontLoader;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.io.OVDSkinLoader;
import com.ftloverdrive.io.RelativeFileHandleResolver;
import com.ftloverdrive.io.URIFileHandleResolver;
import com.ftloverdrive.net.OVDNetManager;
import com.ftloverdrive.script.ScriptLoader;
import com.ftloverdrive.script.ScriptResource;
import com.ftloverdrive.ui.screen.OVDScreen;
import com.ftloverdrive.ui.screen.OVDScreenManager;
import com.ftloverdrive.util.OVDReferenceManager;


/**
 * TODO: Problem: pause()/resume() is not called when the application window
 * is being moved on desktop. Possible workaround:
 * http://stackoverflow.com/a/21731002
 */
public class OverdriveGame implements ApplicationListener {

	public static final String APP_NAME = "Overdrive";
	public static final String APP_VERSION = "0.1";

	private static final String ENV_APP_PATH = "OVERDRIVE_APP_PATH";


	// Don't instantiate early, or the classloader might complain?
	private Logger log;
	private File appDir;
	private File resourcesDir;
	private Client kryoClient;

	private URIFileHandleResolver fileHandleResolver;

	private AssetManager assetManager;
	private OVDScreenManager screenManager;
	private OVDReferenceManager refManager;
	private OVDBlueprintManager blueManager;
	private OVDNetManager netManager;
	private OVDScreen currentScreen = null;

	// Every client is capable of acting as a server, but only does so if it is the
	// host in a multiplayer game, or the user is playing single.
	private OverdriveServer server;


	@Override
	public void create() {
		Gdx.app.setLogLevel( Application.LOG_DEBUG );
		log = new Logger( OverdriveGame.class.getCanonicalName(), Logger.DEBUG );
		log.info( String.format( "%s v%s", APP_NAME, APP_VERSION ) );
		log.info( String.format( "%s %s", System.getProperty( "os.name" ), System.getProperty( "os.version" ) ) );
		log.info( String.format( "%s, %s, %s", System.getProperty( "java.vm.name" ), System.getProperty( "java.version" ),
				System.getProperty( "os.arch" ) ) );

		appDir = new File( "." );
		log.info( "CWD: " + appDir.getAbsolutePath() );

		String envAppPath = System.getenv( ENV_APP_PATH );
		if ( envAppPath != null && envAppPath.length() > 0 ) {
			File envAppDir = new File( envAppPath );
			if ( envAppDir.exists() ) {
				log.info( String.format( "Environment var (%s) changed app path: %s", ENV_APP_PATH, envAppPath ) );
				appDir = envAppDir;
			}
			else {
				log.error( String.format( "Environment var (%s) set a non-existent app path: %s", ENV_APP_PATH, envAppPath ) );
			}
		}

		resourcesDir = new File( appDir, "resources" );

		java.nio.IntBuffer buf = com.badlogic.gdx.utils.BufferUtils.newIntBuffer( 16 );
		Gdx.gl.glGetIntegerv( GL20.GL_MAX_TEXTURE_SIZE, buf );
		int maxTextureSize = buf.get();
		log.debug( "Device Estimated Max Texture Size: " + maxTextureSize );

		log.debug( "Device GL30: " + Gdx.graphics.isGL30Available() );

		fileHandleResolver = new URIFileHandleResolver();
		fileHandleResolver.setResolver( "internal:", new InternalFileHandleResolver(), true );
		fileHandleResolver.setResolver( "external:", new ExternalFileHandleResolver(), true );
		fileHandleResolver.addDefaultResolver( new RelativeFileHandleResolver( resourcesDir ) );
		fileHandleResolver.addDefaultResolver( new RelativeFileHandleResolver( appDir ) );
		fileHandleResolver.addDefaultResolver( new InternalFileHandleResolver() );

		refManager = new OVDReferenceManager();
		blueManager = new OVDBlueprintManager();
		netManager = new OVDNetManager( this );

		assetManager = new AssetManager( fileHandleResolver );
		assetManager.setLoader( BitmapFont.class, new FreeTypeFontLoader( fileHandleResolver ) );
		assetManager.setLoader( ScriptResource.class, new ScriptLoader( fileHandleResolver ) );
		assetManager.setLoader( OVDSkin.class, new OVDSkinLoader( assetManager, fileHandleResolver ) );

		OverdriveContext context = new OverdriveContext();
		context.init( this, null, -1 );
		screenManager = new OVDScreenManager( context );

		kryoClient = new Client();
		OVDNetManager.registerClasses( kryoClient.getKryo() );
		kryoClient.addListener( new Listener() {

			public void received( Connection connection, Object object ) {
				if ( object instanceof OVDEvent ) {
					if ( currentScreen == null ) {
						log.debug( "Received event " + object + ", but could not handle it because screen is null." );
						return;
					}
					if ( currentScreen.getEventManager() == null ) {
						log.debug( "Received event " + object + ", but could not handle it because eventManager was not instantiated yet." );
						return;
					}
					currentScreen.getEventManager().postDelayedInboundEvent( (OVDEvent)object );
				}
			}

			public void connected( Connection connection ) {
				onConnected( connection );
			}
		} );

		server = new OverdriveServer( context );

		screenManager.showScreen( screenManager.getInitScreenKey() );
	}

	/**
	 * @param host
	 *            may be null to connect to localhost (useful for single-player)
	 */
	public void connect( String host ) {
		try {
			kryoClient.stop();
			if ( host == null )
				host = "127.0.0.1";
			kryoClient.start();
			// TODO Run connection in another thread, show window w/ abort button in this one
			kryoClient.connect( 5000, host, 54555, 54777 );
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the String representation of the IP address of the server, or null if
	 *         not connected. If the client this method was invoked on acts as a server,
	 *         then this method returns the localhost address (127.0.0.1).
	 */
	public String getServerAddress() {
		InetSocketAddress address = kryoClient.getRemoteAddressTCP();
		return address == null ? null : address.getAddress().getHostAddress();
	}

	public int getConnectionId() {
		return kryoClient.getID();
	}

	public void disconnect() {
		kryoClient.stop();
	}

	public void onConnected( Connection connection ) {
	}

	// TODO: Test code, remove (or wrap in a nice UI)
	public void discoverLocalHosts() {
		// Prints debug messages of its own
		kryoClient.discoverHosts( 54777, 1000 );
	}

	public void sendTCP( Object o ) {
		if ( kryoClient.isConnected() ) {
			try {
				kryoClient.sendTCP( o );
			}
			catch ( Exception e ) {
				e.printStackTrace();
				dispose();
			}
		}
		else {
			// TODO Actual error handling
			log.error( "Client not connected!" );
		}
	}

	/**
	 * Returns the main FileHandleResolver.
	 *
	 * Usage:
	 * FileHandle fh = getFileHandleResolver().resolve( pathString );
	 * // If you need a File object...
	 * File f = fh.file();
	 *
	 * Given a string, this resolver will look in several locations.
	 * {current_dir|APP_PATH}/resources/...
	 * {current_dir|APP_PATH}/...
	 * {internal}/...
	 *
	 * If a URI prefix is found, that will be stripped, and a specific
	 * location will be checked instead.
	 * internal://... - Root of the jar, and current dir on desktop.
	 * external://... - Android SD card, or desktop user's home dir.
	 */
	public FileHandleResolver getFileHandleResolver() {
		return fileHandleResolver;
	}

	/**
	 * Returns a manager to load assets.
	 *
	 * This one can load ttf fonts:
	 * String assetString = ".../myfont.ttf?size=10";
	 * assetManager.load( assetString, BitmapFont.class );
	 * assetManager.finishLoading();
	 * BitmapFont font = assetManager.get( assetString, BitmapFont.class );
	 */
	public AssetManager getAssetManager() {
		return assetManager;
	}

	/**
	 * Returns a manager to change screens.
	 */
	public OVDScreenManager getScreenManager() {
		return screenManager;
	}

	/**
	 * Returns a manager to link objects together by reference ids.
	 */
	public OVDReferenceManager getReferenceManager() {
		return refManager;
	}

	/**
	 * Returns a manager to link unique string identifiers with blueprints.
	 */
	public OVDBlueprintManager getBlueprintManager() {
		return blueManager;
	}

	/**
	 * Returns a manager to request new reference ids.
	 */
	public OVDNetManager getNetManager() {
		return netManager;
	}

	public OverdriveServer getServer() {
		return server;
	}

	/**
	 * Hides the current screen and shows another.
	 *
	 * This shouldn't be called directly.
	 * Use getScreenManager() instead.
	 */
	public void setScreen( OVDScreen screen ) {
		if ( currentScreen != null ) currentScreen.hide();

		currentScreen = screen;
		if ( currentScreen != null ) currentScreen.show();
	}

	public Screen getScreen() {
		return currentScreen;
	}

	@Override
	public void resize( int width, int height ) {
		log.info( String.format( "Screen resized: %dx%d", width, height ) );
		if ( currentScreen != null ) currentScreen.resize( width, height );
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		if ( currentScreen != null ) currentScreen.render( delta );
		if ( server != null ) server.update( delta );
	}

	@Override
	public void pause() {
		if ( currentScreen != null ) currentScreen.pause();
		if ( server != null ) server.pause();
	}

	@Override
	public void resume() {
		if ( currentScreen != null ) currentScreen.resume();
		if ( server != null ) server.resume();
	}

	@Override
	public void dispose() {
		disconnect();
		server.dispose();
		if ( currentScreen != null ) currentScreen.dispose();
		screenManager.dispose();
		assetManager.dispose();
	}
}
