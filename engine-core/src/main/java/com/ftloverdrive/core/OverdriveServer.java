package com.ftloverdrive.core;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.event.engine.DelayedEvent;
import com.ftloverdrive.event.handler.ServerEventHandler;
import com.ftloverdrive.net.FetchRefIdRange;
import com.ftloverdrive.net.OVDNetManager;
import com.ftloverdrive.net.Range;


public class OverdriveServer implements Disposable, FetchRefIdRange {

	private static final int rangeLength = 1024;

	private Logger log;
	private Server kryoServer;

	private OVDClock clock;
	private OVDEventManager eventManager;
	private Map<Integer, List<Range>> connectionRangeMap;
	private int nextRangeStart;

	private OverdriveContext context;

	private boolean paused = false;


	public OverdriveServer( OverdriveContext context ) {
		log = new Logger( OverdriveServer.class.getCanonicalName(), Logger.DEBUG );

		this.context = context;

		connectionRangeMap = new HashMap<Integer, List<Range>>();
		nextRangeStart = 0;

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

			public void connected( Connection connection ) {
				onClientConnected( connection );
			}
		} );
	}

	public int countConnectedClients() {
		return kryoServer.getConnections().length;
	}

	public void sendAllTCP( Object o ) {
		kryoServer.sendToAllTCP( o );
	}

	public void sendTCP( int connectionId, Object o ) {
		kryoServer.sendToTCP( connectionId, o );
	}

	public void start() {
		start( 54555, 54777 ); // KryoNet's default ports
	}

	public void start( int tcpPort, int udpPort ) {
		try {
			// TODO: Use constants instead of magic numbers and strings
			FetchRefIdRange stub = (FetchRefIdRange)UnicastRemoteObject.exportObject( this, 0 );
			Registry registry = LocateRegistry.createRegistry( 54556 );
			registry.rebind( "FetchRefIdRange", stub );

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
		// TODO: Other cleanup?
	}

	public void update( float delta ) {
		if ( !paused )
			clock.secondsElapsed( delta );
		eventManager.processEvents( context );
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public void onClientConnected( Connection connection ) {
		// TODO: Send some kind of condensed object representing the data needed to bring the
		// client up to speed
		// connection.sendTCP( Object );
	}

	public OVDEventManager getEventManager() {
		return eventManager;
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

	/**
	 * refId range fetching with caching based on connectionId, should the server
	 * want to keep track of which client has which range of refIds.
	 */
	private Range fetchRefIdRange( int connectionId ) throws RemoteException {
		List<Range> rangeList = connectionRangeMap.get( connectionId );
		if ( rangeList == null ) {
			rangeList = new ArrayList<Range>();
			connectionRangeMap.put( connectionId, rangeList );
		}
		Range result = fetchRefIdRange();
		rangeList.add( result );
		return result;
	}

	/**
	 * This method is remotely invoked by OVDNetManager to fetch a new range of ref ids.
	 */
	@Override
	public Range fetchRefIdRange() throws RemoteException {
		return new Range( nextRangeStart, nextRangeStart += rangeLength );
	}
}
