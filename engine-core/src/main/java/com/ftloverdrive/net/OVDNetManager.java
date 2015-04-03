package com.ftloverdrive.net;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.ftloverdrive.event.TickEvent;
import com.ftloverdrive.event.TickListenerEvent;
import com.ftloverdrive.event.engine.ModelDestructionEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
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
import com.ftloverdrive.model.incident.RequirementResource;
import com.ftloverdrive.model.incident.RequirementShip;
import com.ftloverdrive.model.ship.ShipCoordinate;


public class OVDNetManager {

	protected Array<Range> idRanges;
	protected int nextId = 0;
	protected int localPlayerModelRefId = -1;


	public OVDNetManager() {
		idRanges = new Array<Range>( true, 1 );
		idRanges.add( new Range( 0, Integer.MAX_VALUE ) ); // Default range.
	}


	/**
	 * Sets a range of assignable reference ids, clearing all others.
	 * The next id will be the start of this range.
	 *
	 * @param start
	 *            the start of the range, inclusive
	 * @param start
	 *            the end of the range, exclusive
	 */
	public void setRefIdRange( int start, int end ) {
		idRanges.clear();
		idRanges.add( new Range( start, end ) );
		nextId = start;
	}

	/**
	 * Adds an additional range of assignable reference ids.
	 *
	 * @param start
	 *            the start of the range, inclusive
	 * @param start
	 *            the end of the range, exclusive
	 */
	public void addRefIdRange( int start, int end ) {
		idRanges.add( new Range( start, end ) );
	}

	/**
	 * Reserves and returns the next available reference id.
	 *
	 * TODO: Have the server pre-assign each player a different large range
	 * of ids. When this is called, get the next id from the range; if that's
	 * been exhausted, make a synchronous RMI call to fetch a new range of
	 * ids.
	 */
	public int requestNewRefId() {
		while ( idRanges.size > 0 && nextId >= idRanges.get( 0 ).end ) {
			idRanges.removeIndex( 0 );
		}
		if ( idRanges.size == 0 ) {
			// TODO: Make a synchronous RMI call to fetch a new range.
		}
		return nextId++;
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
	 * KryoNet requires ahead-of-time knowledge about what classes will be transferred between
	 * the server and the client.
	 * 
	 * TODO: There's probably a better way to handle this, or at least elsewhere?
	 */
	public static void registerClasses( Kryo kryo ) {
		kryo.register( Object[].class );
		kryo.register( int[].class );
		kryo.register( Integer[].class );

		kryo.register( ImageSpec.class );
		kryo.register( AnimSpec.class );
		kryo.register( ShipCoordinate.class );
		kryo.register( ShipCoordinate[].class );
		kryo.register( PlotBranchRequirement.class );
		kryo.register( PlotBranchRequirement[].class );
		kryo.register( RequirementShip.class );
		kryo.register( RequirementResource.class );

		kryo.register( TickEvent.class );
		kryo.register( TickListenerEvent.class );
		kryo.register( ModelDestructionEvent.class );
		kryo.register( GamePlayerShipChangeEvent.class );
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


	public static class Range {

		public int start = 0;
		public int end = 0;


		public Range( int start, int end ) {
			this.start = start;
			this.end = end;
		}

		public Range() {
		}
	}
}
