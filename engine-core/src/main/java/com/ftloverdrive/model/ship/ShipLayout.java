package com.ftloverdrive.model.ship;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pools;


public class ShipLayout {

	protected Set<ShipCoordinate> allShipCoords;
	protected ObjectIntMap<ShipCoordinate> coordToRoomRefIdMap;
	protected IntMap<ShipCoordinate[]> roomRefIdToCoordsMap;
	protected ObjectIntMap<ShipCoordinate> coordToDoorRefIdMap;
	protected IntMap<ShipCoordinate> doorRefIdToCoordsMap;


	public ShipLayout() {
		allShipCoords = new HashSet<ShipCoordinate>();
		coordToRoomRefIdMap = new ObjectIntMap<ShipCoordinate>();
		roomRefIdToCoordsMap = new IntMap<ShipCoordinate[]>();
		coordToDoorRefIdMap = new ObjectIntMap<ShipCoordinate>();
		doorRefIdToCoordsMap = new IntMap<ShipCoordinate>();
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

	public void addDoor( int doorModelRefId, ShipCoordinate doorCoords ) {
		allShipCoords.add( doorCoords );
		doorRefIdToCoordsMap.put( doorModelRefId, doorCoords );
		coordToDoorRefIdMap.put (doorCoords, doorModelRefId );
	}


	/**
	 * Returns the ShipCoordinates for squares and walls of a room.
	 */
	public ShipCoordinate[] getRoomCoords( int roomModelRefId ) {
		return roomRefIdToCoordsMap.get( roomModelRefId );
	}

	/**
	 * Returns the ShipCoordinate for the door.
	 */
	public ShipCoordinate getDoorCoords( int doorModelRefId ) {
		return doorRefIdToCoordsMap.get( doorModelRefId );
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
	 * Returns a reference id for the door placed at the ShipCoordinate, or -1.
	 * 
	 * TODO: Two doors can be placed at single ShipCoordinate. As such this method is very reliable. Remove?
	 */
	public int getDoorRefIdOfCoords( ShipCoordinate coord ) {
		return coordToDoorRefIdMap.get( coord, -1 );
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
	 * Returns an interator for the DoorModel reference ids.
	 * 
	 * Usage:
	 * for ( IntMap.Keys it = layout.getAllRoomRefIds(); it.hasNext; ) {
	 *   int n = it.next();
	 * }
	 */
	public IntMap.Keys doorRefIds() {
		return doorRefIdToCoordsMap.keys();
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
	 * Creates ShipCoordinates of squares and walls as described by the char matrix.
	 * Position in the matrix specifies the cell's location relative to the room's
	 * coordinates (params x and y).
	 * 
	 * All rows of the matrix have to have the same length.
	 * 
	 * The char itself specifies the type of cell: (+ memorization tricks!)
	 * <     -- wWest
	 * >     -- wEast
	 * ^     -- wNorth
	 * v     -- wSouth (the sharp end points at the wall)
	 * "     -- wWestEast (kinda looks like two vertical walls next to each other)
	 * =     -- wNorthSouth (kinda looks like two horizontal walls next to each other)
	 * q     -- wNorthWest
	 * w     -- wNorthEast
	 * a     -- wSouthWest (these four chars are located in a more or --)
	 * s     -- wSouthEast (-- less diagonal manner on a QWERTY keyboard)
	 * d     -- wNorthSouthWest
	 * f     -- wNorthSouthEast
	 * r     -- wNorthWestEast (these four chars are located in a more or --)
	 * c     -- wSouthWestEast (-- less orthogonal manner on a QWERTY keyboard)
	 * #     -- square (no walls)
	 * space -- nothing (empty space)
	 * 
	 * For example, the following char matrix:
	 *   char[][] room = {
	 *       { ' ', 'r', ' ' },
	 *       { 'd', '#', 'f' },
	 *       { ' ', 'c', ' ' }
	 *   }
	 * ...would create a room the shape of a + sign, with appropriately placed walls.
	 */
	public static ShipCoordinate[] createRoomCoords( int x, int y, char[][] matrix ) {
		int w = matrix.length;
		int h = matrix[0].length;
		Array<ShipCoordinate> result = new Array<ShipCoordinate>();

		for ( int r=0; r < w; r++ ) {
			if ( matrix[r].length != h ) {
				throw new IllegalArgumentException( String.format( "Row %s width (%s) differs from matrix width (%s).",
						r, matrix[r].length, h ) );
			}

			for ( int c=0; c < h; c++ ) {
				ShipCoordinate[] tmp = createCell( x+c, y+r, matrix[r][c] );
				for ( int i=0; i < tmp.length; i++ )
					result.add( tmp[i] );
			}
		}

		return result.toArray( ShipCoordinate.class );
	}

	/**
	 * @see #createRoomCoords(int, int, char[][])
	 */
	public static ShipCoordinate[] createRoomCoords( int x, int y, String s ) {
		if ( s.isEmpty() )
			return new ShipCoordinate[]{};

		String[] rows = s.split( "\n" );
		char[][] chars = new char[rows.length][];

		for ( int r=0; r < rows.length; r++ )
			chars[r] = rows[r].toCharArray();
		
		return createRoomCoords( x, y, chars );
	}

	private static ShipCoordinate[] createCell( int x, int y, char c) {
		switch ( c ) {
			case 0:
			case ' ':
				return new ShipCoordinate[0];
			case '#':
				return ShipCoordinate.square( x, y );
			case '<':
				return ShipCoordinate.wWest( x, y );
			case '>':
				return ShipCoordinate.wEast( x, y );
			case '^':
				return ShipCoordinate.wNorth( x, y );
			case 'v':
				return ShipCoordinate.wSouth( x, y );
			case 'q':
				return ShipCoordinate.wNorthWest( x, y );
			case 'w':
				return ShipCoordinate.wNorthEast( x, y );
			case 'a':
				return ShipCoordinate.wSouthWest( x, y );
			case 's':
				return ShipCoordinate.wSouthEast( x, y );
			case '"':
				return ShipCoordinate.wWestEast( x, y );
			case '=':
				return ShipCoordinate.wNorthSouth( x, y );
			case 'r':
				return ShipCoordinate.wNorthWestEast( x, y );
			case 'c':
				return ShipCoordinate.wSouthWestEast( x, y );
			case 'd':
				return ShipCoordinate.wNorthSouthWest( x, y );
			case 'f':
				return ShipCoordinate.wNorthSouthEast( x, y );
			case '-':
				return ShipCoordinate.door( x, y, true ); // Horizontal door
			case '|':
				return ShipCoordinate.door( x, y, false ); // Vertical door
			default:
				throw new IllegalArgumentException("Invalid cell type: " + c);
		}
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
