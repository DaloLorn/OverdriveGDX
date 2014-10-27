package com.ftloverdrive.model.ship;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pools;


public class ShipLayout {

	protected Set<ShipCoordinate> allShipCoords;
	protected ObjectIntMap<ShipCoordinate> coordToRoomRefIdMap;
	protected IntMap<ShipCoordinate[]> roomRefIdToCoordsMap;


	public ShipLayout() {
		allShipCoords = new HashSet<ShipCoordinate>();
		coordToRoomRefIdMap = new ObjectIntMap<ShipCoordinate>();
		roomRefIdToCoordsMap = new IntMap<ShipCoordinate[]>();
	}


	/**
	 * Associates a RoomModel with ShipCoordinates of squares and walls.
	 */
	public void addRoom( int roomModelRefId, ShipCoordinate[] roomCoords ) {
		allShipCoords.addAll( Arrays.asList( roomCoords ) );
		for ( ShipCoordinate tmpCoord : roomCoords ) {
			coordToRoomRefIdMap.put( tmpCoord, roomModelRefId );
		}
		roomRefIdToCoordsMap.put( roomModelRefId, roomCoords );
	}


	/**
	 * Returns the ShipCoordinates for squares and walls of a room.
	 */
	public ShipCoordinate[] getRoomCoords( int roomModelRefId ) {
		return roomRefIdToCoordsMap.get( roomModelRefId );
	}

	/**
	 * Returns a reference id for the room containing a ShipCoordinate, or -1.
	 *
	 * Caution: Wall coordinates may border two rooms.
	 */
	public int getRoomRefIdOfCoords( ShipCoordinate coord ) {
		return coordToRoomRefIdMap.get( coord, -1 );
	}


	/**
	 * Returns all the ShipCoordinates, including walls.
	 */
	public Set<ShipCoordinate> getAllShipCoords() {
		return allShipCoords;
	}


	/**
	 * Returns an iterator for the RoomModel reference ids.
	 *
	 * Usage:
	 * for ( IntMap.Keys it = layout.getAllRoomRefIds(); it.hasNext; ) {
	 *   int n = it.next();
	 * }
	 */
	public IntMap.Keys roomRefIds() {
		return roomRefIdToCoordsMap.keys();
	}



	/**
	 * Creates ShipCoordinates of squares and walls in an area, for room building.
	 *
	 * @param x ship coordinate of the top-left square
	 * @param y ship coordinate of the top-left square
	 * @param w columns of squares in the room
	 * @param h rows of squares in the room
	 */
	public static ShipCoordinate[] createRoomCoords( int x, int y, int w, int h ) {
		ShipCoordinate[] result = new ShipCoordinate[ w*h + w*2 + h*2 ];
		int n = 0;
		for ( int r=y; r < y+h; r++ ) {
			for ( int c=x; c < x+w; c++ ) {
				ShipCoordinate tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
				tmpCoord.init( c, r, 0 );
				result[n++] = tmpCoord;
			}
		}

		// Horizontal walls.
		for ( int c=x; c < x+w; c++ ) {
			ShipCoordinate tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			tmpCoord.init( c, y, 1 );
			result[n++] = tmpCoord;

			tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			tmpCoord.init( c, y+h, 1 );
			result[n++] = tmpCoord;
		}

		// Vertical walls.
		for ( int r=y; r < y+h; r++ ) {
			ShipCoordinate tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			tmpCoord.init( x, r, 2 );
			result[n++] = tmpCoord;

			tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			tmpCoord.init( x+w, r, 2 );
			result[n++] = tmpCoord;
		}

		return result;
	}

	/**
	 * Computes and returns the current size of the room layout.
	 */
	public Vector2 getSize() {
		int wMax = 0;
		int wMin = Integer.MAX_VALUE;
		int hMax = 0;
		int hMin = Integer.MAX_VALUE;

		for ( ShipCoordinate coord : allShipCoords ) {
			// Only count square coords
			if ( coord.v != 0 )
				continue;
			
			if ( coord.x > wMax )
				wMax = coord.x;
			else if ( coord.x < wMin )
				wMin = coord.x;

			if ( coord.y > hMax )
				hMax = coord.y;
			else if ( coord.y < hMin )
				hMin = coord.y;
		}

		wMin--;
		hMin--;

		return new Vector2( wMax - wMin, hMax - hMin );
	}
}
