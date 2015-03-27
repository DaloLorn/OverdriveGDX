package com.ftloverdrive.net;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class NetTest {
	
	Server server;
	Client client;

	public NetTest() throws IOException {
		server = new Server();
		//server.getKryo().register( Integer.class );
		server.start();
		server.bind( 54555, 54777 );

		server.addListener( new Listener() {

			public void received( Connection connection, Object object ) {
				if ( object instanceof Integer ) {
					Integer request = (Integer)object;
					System.out.println( "Server: Received request: " + request );

					Integer response = new Integer( request + 1 );
					connection.sendTCP( response );
				}
			}
		} );

		client = new Client();
		//client.getKryo().register( Integer.class );
		client.start();
		client.connect( 5000, "127.0.0.1", 54555, 54777 );

		client.addListener( new Listener() {

			public void received( Connection connection, Object object ) {
				if ( object instanceof Integer ) {
					Integer response = (Integer)object;
					System.out.println( "Client: Received response: " + response );
				}
			}
		} );

		Integer request = new Integer( 1 );
		client.sendTCP( request );
	}
	
	public void shutdown() {
		client.stop();
		server.stop();
	}
}
