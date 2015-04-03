package com.ftloverdrive.core;

import java.io.IOException;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ftloverdrive.event.DelayedEvent;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.event.handler.ServerEventHandler;
import com.ftloverdrive.net.OVDNetManager;
import com.ftloverdrive.util.OVDReferenceManager;


public class OverdriveServer implements Disposable {

	private Logger log;
	private Server kryoServer;

	private OVDReferenceManager refManager; // TODO: Needed?
	private OVDNetManager netManager; // TODO: Needed?
	private OVDClock clock;
	private OVDEventManager eventManager;

	private OverdriveContext context;

	private boolean paused = false;


	public OverdriveServer( OverdriveContext context ) {
		log = new Logger( OverdriveServer.class.getCanonicalName(), Logger.DEBUG );

		this.context = context;

		refManager = new OVDReferenceManager();
		netManager = new OVDNetManager();

		eventManager = new OVDEventManager( true );
		OVDEventHandler handler = new ServerEventHandler();
		for ( Class c : handler.getEventClasses() )
			eventManager.setEventHandler( c, handler );

		clock = new OVDClock( eventManager );

		kryoServer = new Server();
		OVDNetManager.registerClasses( kryoServer.getKryo() );

		kryoServer.addListener( new Listener() {

			public void received( Connection connection, Object object ) {
				if ( object instanceof OVDEvent ) {
					eventManager.postDelayedInboundEvent( (OVDEvent)object );
				}
			}
		} );
	}

	public void sendAllTCP( Object o ) {
		kryoServer.sendToAllUDP( o );
	}

	public void start() {
		start( 54555, 54777 ); // KryoNet's default ports
	}

	public void start( int tcpPort, int udpPort ) {
		try {
			kryoServer.start();
			kryoServer.bind( tcpPort, udpPort );
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop() {
		kryoServer.stop();
	}

	@Override
	public void dispose() {
		stop();
	}

	public void update( float delta ) {
		if ( !paused ) {
			clock.secondsElapsed( delta );
			eventManager.processEvents( context );
		}
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public void postDelayedEvent( DelayedEvent de ) {
		clock.postDelayedEvent( de );
	}

	public void incrTick( int tickCount ) {
		clock.incrTick( tickCount );
	}

	public void decrTick( int tickCount ) {
		clock.decrTick( tickCount );
	}
}
