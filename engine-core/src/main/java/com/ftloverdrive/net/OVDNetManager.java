package com.ftloverdrive.net;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.ftloverdrive.event.engine.DelayedEvent;
import com.ftloverdrive.event.engine.ModelDestructionEvent;
import com.ftloverdrive.event.engine.TickEvent;
import com.ftloverdrive.event.engine.TickListenerEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GameSpawnPlayerEvent;
import com.ftloverdrive.event.incident.BranchCreationEvent;
import com.ftloverdrive.event.incident.ConsequenceDamageCreationEvent;
import com.ftloverdrive.event.incident.ConsequenceResourceCreationEvent;
import com.ftloverdrive.event.incident.IncidentAddBranchEvent;
import com.ftloverdrive.event.incident.IncidentAddConsequenceEvent;
import com.ftloverdrive.event.incident.IncidentCreationEvent;
import com.ftloverdrive.event.incident.IncidentSelectionEvent;
import com.ftloverdrive.event.incident.IncidentTriggerEvent;
import com.ftloverdrive.event.player.CrewMoveOrderEvent;
import com.ftloverdrive.event.ship.CrewPropertyEvent;
import com.ftloverdrive.event.ship.DoorPropertyEvent;
import com.ftloverdrive.event.ship.ShipCreationEvent;
import com.ftloverdrive.event.ship.ShipCrewAddEvent;
import com.ftloverdrive.event.ship.ShipCrewCreationEvent;
import com.ftloverdrive.event.ship.ShipDoorCreationEvent;
import com.ftloverdrive.event.ship.ShipLayoutCrewPlacementEvent;
import com.ftloverdrive.event.ship.ShipLayoutDoorAddEvent;
import com.ftloverdrive.event.ship.ShipLayoutRoomAddEvent;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.event.ship.ShipRoomCreationEvent;
import com.ftloverdrive.event.ship.ShipRoomImageChangeEvent;
import com.ftloverdrive.io.AnimSpec;
import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.incident.PlotBranchRequirement;
import com.ftloverdrive.model.incident.requirement.ResourceRequirement;
import com.ftloverdrive.model.incident.requirement.ShipRequirement;
import com.ftloverdrive.model.ship.ShipCoordinate;


public class OVDNetManager {

	protected Array<Range> idRanges;
	protected int nextId = 0;
	protected Array<Integer> returnedIds;
	protected int localPlayerModelRefId = -1;
	protected int enemyPlayerModelRefId = -1;


	public OVDNetManager() {
		idRanges = new Array<Range>( true, 1 );
		// idRanges.add( new Range( 0, Integer.MAX_VALUE ) ); // Default range.
		returnedIds = new Array<Integer>( true, 16 );
	}

	/**
	 * Sets a range of assignable reference ids, clearing all others.
	 * The next id will be the start of this range.
	 *
	 * @param start
	 *            the start of the range, inclusive
	 * @param end
	 *            the end of the range, exclusive
	 */
	public void setRefIdRange( int start, int end ) {
		returnedIds.clear();
		idRanges.clear();
		idRanges.add( new Range( start, end ) );
		nextId = start;
	}

	/**
	 * Adds an additional range of assignable reference ids.
	 *
	 * @param start
	 *            the start of the range, inclusive
	 * @param end
	 *            the end of the range, exclusive
	 */
	public void addRefIdRange( int start, int end ) {
		idRanges.add( new Range( start, end ) );
	}

	/**
	 * Reserves and returns the next available reference id.
	 * If there are any ids in the reusable pool, then the a refId from that pool
	 * will be returned instead.
	 *
	 * TODO: Have the server pre-assign each player a different large range
	 * of ids. When this is called, get the next id from the range; if that's
	 * been exhausted, make a synchronous RMI call to fetch a new range of
	 * ids.
	 */
	public int requestNewRefId() {
		if ( returnedIds.size > 0 ) {
			return returnedIds.removeIndex( 0 );
		}
		while ( idRanges.size > 0 && nextId >= idRanges.get( 0 ).end ) {
			idRanges.removeIndex( 0 );
		}
		if ( idRanges.size == 0 ) {
			// TODO: Make a synchronous RMI call to fetch a new range.
			try {
				// TODO: Use constants instead of magic numbers and strings
				// TODO: Need a way to get IP address of the server we're connected to
				Registry registry = LocateRegistry.getRegistry( "127.0.0.1", 54556 );
				FetchRefIdRange stub = (FetchRefIdRange)registry.lookup( "FetchRefIdRange" );
				Range range = stub.fetchRefIdRange();
				System.out.println( "Received refId range: " + range );
				addRefIdRange( range.start, range.end );
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		return nextId++;
	}

	/**
	 * Returns the refId to the pool of reusable ids.
	 */
	public void returnRefId( int refId ) {
		// Don't add the refId to the pool if it doesn't belong to us
		boolean local = false;
		for ( int i = 0; i < idRanges.size && !local; ++i ) {
			Range r = idRanges.get( i );
			local = refId >= r.start && refId < r.end;
		}

		if ( local )
			returnedIds.add( refId );
	}

	/**
	 * Sets the reference id for the local PlayerModel.
	 */
	public void setLocalPlayerRefId( int playerModelRefId ) {
		localPlayerModelRefId = playerModelRefId;
	}

	public int getLocalPlayerRefId() {
		return localPlayerModelRefId;
	}

	/**
	 * Sets the reference id for the enemy PlayerModel.
	 */
	public void setEnemyPlayerRefId( int playerRefId ) {
		enemyPlayerModelRefId = playerRefId;
	}

	public int getEnemyPlayerRefId() {
		return enemyPlayerModelRefId;
	}

	/**
	 * KryoNet requires ahead-of-time knowledge about what classes will be transferred between
	 * the server and the client.
	 * 
	 * TODO: There's probably a better way to handle this, or at least elsewhere?
	 */
	public static void registerClasses( Kryo kryo ) {
		// TODO: Could use an external file to list classnames, and use
		// ClassReflection.forName( String ) to load and register them.

		// Data classes
		// - Java
		kryo.register( Object[].class );
		kryo.register( int[].class );
		kryo.register( Integer[].class );
		// - OVD
		kryo.register( ImageSpec.class );
		kryo.register( AnimSpec.class );
		kryo.register( ShipCoordinate.class );
		kryo.register( ShipCoordinate[].class );
		kryo.register( PlotBranchRequirement.class );
		kryo.register( PlotBranchRequirement[].class );
		kryo.register( ShipRequirement.class );
		kryo.register( ResourceRequirement.class );

		// Event classes
		kryo.register( TickEvent.class );
		kryo.register( TickListenerEvent.class );
		kryo.register( DelayedEvent.class );
		kryo.register( ModelDestructionEvent.class );
		kryo.register( GamePlayerShipChangeEvent.class );
		kryo.register( GameSpawnPlayerEvent.class );
		kryo.register( BranchCreationEvent.class );
		kryo.register( ConsequenceDamageCreationEvent.class );
		kryo.register( ConsequenceResourceCreationEvent.class );
		kryo.register( IncidentAddBranchEvent.class );
		kryo.register( IncidentAddConsequenceEvent.class );
		kryo.register( IncidentCreationEvent.class );
		kryo.register( IncidentSelectionEvent.class );
		kryo.register( IncidentTriggerEvent.class );
		kryo.register( CrewMoveOrderEvent.class );
		kryo.register( CrewPropertyEvent.class );
		kryo.register( DoorPropertyEvent.class );
		kryo.register( ShipCreationEvent.class );
		kryo.register( ShipCrewAddEvent.class );
		kryo.register( ShipCrewCreationEvent.class );
		kryo.register( ShipDoorCreationEvent.class );
		kryo.register( ShipRoomCreationEvent.class );
		kryo.register( ShipLayoutCrewPlacementEvent.class );
		kryo.register( ShipLayoutDoorAddEvent.class );
		kryo.register( ShipLayoutRoomAddEvent.class );
		kryo.register( ShipPropertyEvent.class );
		kryo.register( ShipRoomImageChangeEvent.class );
	}
}
