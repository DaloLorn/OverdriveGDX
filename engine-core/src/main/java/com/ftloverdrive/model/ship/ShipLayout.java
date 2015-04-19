package com.ftloverdrive.model.ship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;


public class ShipLayout {

	protected Set<ShipCoordinate> allShipCoords;
	protected ObjectIntMap<ShipCoordinate> coordToRoomRefIdMap;
	protected IntMap<ShipCoordinate[]> roomRefIdToCoordsMap;
	protected ObjectIntMap<ShipCoordinate> coordToDoorRefIdMap;
	protected IntMap<ShipCoordinate> doorRefIdToCoordsMap;
	protected ObjectIntMap<ShipCoordinate> coordToTpadRefIdMap;
	protected IntMap<ShipCoordinate> tpadRefIdToCoordsMap;
	protected ObjectIntMap<ShipCoordinate> coordToCrewRefIdMap;
	protected IntMap<ShipCoordinate> crewRefIdToCoordsMap;


	public ShipLayout() {
		allShipCoords = new HashSet<ShipCoordinate>();
		coordToRoomRefIdMap = new ObjectIntMap<ShipCoordinate>();
		roomRefIdToCoordsMap = new IntMap<ShipCoordinate[]>();
		coordToDoorRefIdMap = new ObjectIntMap<ShipCoordinate>();
		doorRefIdToCoordsMap = new IntMap<ShipCoordinate>();
		coordToTpadRefIdMap = new ObjectIntMap<ShipCoordinate>();
		tpadRefIdToCoordsMap = new IntMap<ShipCoordinate>();
		coordToCrewRefIdMap = new ObjectIntMap<ShipCoordinate>();
		crewRefIdToCoordsMap = new IntMap<ShipCoordinate>();
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
		coordToDoorRefIdMap.put( doorCoords, doorModelRefId );
	}

	public void addTeleportPad( int tpadModelRefId, ShipCoordinate tpadCoords ) {
		allShipCoords.add( tpadCoords );
		tpadRefIdToCoordsMap.put( tpadModelRefId, tpadCoords );
		coordToTpadRefIdMap.put( tpadCoords, tpadModelRefId );
	}

	public void placeCrew( int crewModelRefId, ShipCoordinate crewCoords ) {
		crewRefIdToCoordsMap.put( crewModelRefId, crewCoords );
		coordToCrewRefIdMap.put( crewCoords, crewModelRefId );
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
	 * Returns the ShipCoordinate for the teleport pad.
	 */
	public ShipCoordinate getTeleportPadCoords( int tpadModelRefId ) {
		return tpadRefIdToCoordsMap.get( tpadModelRefId );
	}

	/**
	 * Returns the ShipCoordinate for the crew member.
	 */
	public ShipCoordinate getCrewCoords( int crewModelRefId ) {
		return crewRefIdToCoordsMap.get( crewModelRefId );
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
	 * TODO: Two doors can be placed at single ShipCoordinate. As such this method is not very reliable. Remove?
	 */
	public int getDoorRefIdOfCoords( ShipCoordinate coord ) {
		return coordToDoorRefIdMap.get( coord, -1 );
	}

	/**
	 * Returns a reference id for the teleport pad placed at the ShipCoordinate, or -1.
	 */
	public int getTeleportPadRefIdOfCoords( ShipCoordinate coord ) {
		return coordToTpadRefIdMap.get( coord, -1 );
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
	 * int n = it.next();
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
	 * int n = it.next();
	 * }
	 */
	public IntMap.Keys doorRefIds() {
		return doorRefIdToCoordsMap.keys();
	}

	public IntMap.Keys tpadRefIds() {
		return tpadRefIdToCoordsMap.keys();
	}

	public IntMap.Keys crewRefIds() {
		return crewRefIdToCoordsMap.keys();
	}

	/**
	 * Returns true if this layout contains the specified room model.
	 * Useful when determining the "owner" of the room.
	 */
	public boolean hasRoom( int roomRefId ) {
		return roomRefIdToCoordsMap.containsKey( roomRefId );
	}

	/**
	 * Returns true if this layout contains the specified door model.
	 * Useful when determining the "owner" of the door.
	 */
	public boolean hasDoor( int doorRefId ) {
		return doorRefIdToCoordsMap.containsKey( doorRefId );
	}

	/**
	 * Returns true if this layout contains the specified teleport pad model.
	 * Useful when determining the "owner" of the teleport pad.
	 */
	public boolean hasTeleportPad( int tpadRefId ) {
		return tpadRefIdToCoordsMap.containsKey( tpadRefId );
	}

	/**
	 * Returns true if the crew member is currently on board of this ship.
	 */
	public boolean hasCrew( int crewRefId ) {
		return crewRefIdToCoordsMap.containsKey( crewRefId );
	}


	/**
	 * Creates ShipCoordinates of squares and walls in an area, for room building.
	 *
	 * @param x
	 *            ship coordinate of the top-left square
	 * @param y
	 *            ship coordinate of the top-left square
	 * @param w
	 *            columns of squares in the room
	 * @param h
	 *            rows of squares in the room
	 */
	public static ShipCoordinate[] createRoomCoords( int x, int y, int w, int h ) {
		ShipCoordinate[] result = new ShipCoordinate[w * h + w * 2 + h * 2];
		int n = 0;
		for ( int r = y; r < y + h; r++ ) {
			for ( int c = x; c < x + w; c++ ) {
				ShipCoordinate tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
				tmpCoord.init( c, r, 0 );
				result[n++] = tmpCoord;
			}
		}

		// Horizontal walls.
		for ( int c = x; c < x + w; c++ ) {
			ShipCoordinate tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			tmpCoord.init( c, y, 1 );
			result[n++] = tmpCoord;

			tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			tmpCoord.init( c, y + h, 1 );
			result[n++] = tmpCoord;
		}

		// Vertical walls.
		for ( int r = y; r < y + h; r++ ) {
			ShipCoordinate tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			tmpCoord.init( x, r, 2 );
			result[n++] = tmpCoord;

			tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			tmpCoord.init( x + w, r, 2 );
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
	 * < -- wWest
	 * > -- wEast
	 * ^ -- wNorth
	 * v -- wSouth (the sharp end points at the wall)
	 * " -- wWestEast (kinda looks like two vertical walls next to each other)
	 * = -- wNorthSouth (kinda looks like two horizontal walls next to each other)
	 * 
	 * q -- wNorthWest
	 * w -- wNorthEast
	 * a -- wSouthWest (these four chars are located in a more or --)
	 * s -- wSouthEast (-- less diagonal manner on a QWERTY keyboard)
	 * 
	 * d -- wNorthSouthWest
	 * f -- wNorthSouthEast
	 * r -- wNorthWestEast (these four chars are located in a more or --)
	 * c -- wSouthWestEast (-- less orthogonal manner on a QWERTY keyboard)
	 * 
	 * # -- square (no walls)
	 * space -- nothing (empty space)
	 * 
	 * For example, the following char matrix:
	 * char[][] room = {
	 * { ' ', 'r', ' ' },
	 * { 'd', '#', 'f' },
	 * { ' ', 'c', ' ' }
	 * }
	 * ...would create a room the shape of a + sign, with appropriately placed walls.
	 */
	public static ShipCoordinate[] createRoomCoords( int x, int y, char[][] matrix ) {
		int h = matrix.length;
		int w = matrix[0].length;
		Array<ShipCoordinate> result = new Array<ShipCoordinate>();

		for ( int r = 0; r < h; r++ ) {
			if ( matrix[r].length != w ) {
				throw new IllegalArgumentException( String.format( "Row %s width (%s) differs from matrix width (%s).",
						r, matrix[r].length, h ) );
			}

			for ( int c = 0; c < w; c++ ) {
				ShipCoordinate[] tmp = createCell( x + c, y + r, matrix[r][c] );
				for ( int i = 0; i < tmp.length; i++ )
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
			return new ShipCoordinate[] {};

		String[] rows = s.replace( "\r", "" ).split( "\n" );
		char[][] chars = new char[rows.length][];

		for ( int r = 0; r < rows.length; r++ )
			chars[r] = rows[r].toCharArray();

		return createRoomCoords( x, y, chars );
	}

	private static ShipCoordinate[] createCell( int x, int y, char c ) {
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
				return ShipCoordinate.doorHorizontal( x, y );
			case '|':
				return ShipCoordinate.doorVertical( x, y );
			default:
				throw new IllegalArgumentException( "Invalid cell type: " + c );
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

	/**
	 * Return a collection of waypoints that an ambulator has to follow in order to get
	 * from start point to end point, or null if no path exists between the two points.
	 * 
	 * TODO: Could possibly extract this function to a pathfinding manager (or something), and
	 * have it take ShipLayout as argument.
	 * 
	 * @param start
	 *            the starting coordinate, the one the crew currently stands on.
	 * @param end
	 *            the end coordinate, the one the crew has been ordered to move to.
	 * @param comingFrom
	 *            the previous waypoint. Null, except when interrupting an earlier move order.
	 * @param movingTo
	 *            the current waypoint. Null, except when interrupting an earlier move order.
	 */
	public Stack<ShipCoordinate> findPath( OverdriveContext context, ShipCoordinate start, ShipCoordinate end,
			ShipCoordinate comingFrom, ShipCoordinate movingTo ) {
		Stack<ShipCoordinate> path = findPathDijkstra( context, start, end );
		if ( path == null ) return null;

		// Remove the waypoints equal to the starting position if the crew was already moving towards
		// the second waypoint.
		// Doing so prevents double-backing to the tile the actor is currently standing on, when it
		// could've just kept going the way it was.
		// However, we don't want to remove the first waypoint if we're going to be moving through
		// a door or a teleport pad, since doing so would cause the crew to ghost through walls / doors.
		while ( path.size() > 1 ) {
			ShipCoordinate first = path.peek();
			ShipCoordinate second = path.get( path.size() - 2 );

			// TODO: Fix all edge cases and bugs....
			boolean remove = first.equalsLocation( comingFrom ) || second.equalsLocation( movingTo );
			boolean keep = ( first.isDoor() && !first.equals( comingFrom ) ) || !second.equalsLocation( movingTo );

			if ( !remove )
				break;
			path.pop();
		}

		// XXX: Debug
		// System.out.println( "Path:" );
		// for ( int i = path.size() - 1; i >= 0; --i ) {
		// System.out.println( "   " + path.get( i ) );
		// }
		// System.out.println( "( Coming from " + comingFrom + " )" );
		// System.out.println( "( Moving to   " + movingTo + " )" );

		return path;
	}

	private Stack<ShipCoordinate> findPathDijkstra( OverdriveContext context, ShipCoordinate start, ShipCoordinate end ) {
		Map<ShipCoordinate, Float> distMap = new HashMap<ShipCoordinate, Float>();
		Map<ShipCoordinate, ShipCoordinate> prevMap = new HashMap<ShipCoordinate, ShipCoordinate>();
		List<ShipCoordinate> pending = new ArrayList<ShipCoordinate>();

		final float orthogonalWeight = 1.0f;
		final float diagonalWeight = 1.4f; // ~sqrt(2)
		// Half of the intended weight (1.4), since teleport pads go in pairs.
		final float tpadWeight = 0.7f;
		// Give doors 0 weight so that they're not ignored, but don't affect the resulting
		// path in any way
		final float doorWeight = 0f;

		// TODO: Rewrite as a series of non-user Orders, so that crew will automatically try
		// to break down locked doors / crystal lockdowns?

		for ( ShipCoordinate v : allShipCoords ) {
			if ( !v.isWall() ) {
				distMap.put( v, Float.MAX_VALUE );
				prevMap.put( v, null );
				pending.add( v );
			}
		}
		distMap.put( start, 0.0f );

		AdjacencyContext diagonalAdj = new DiagonalAdjacencyContext();
		AdjacencyContext defaultAdj = new DefaultAdjacencyContext( context );
		List<ShipCoordinate> neighbours = new ArrayList<ShipCoordinate>();

		while ( !pending.isEmpty() ) {
			ShipCoordinate current = null;
			float dist = Float.MAX_VALUE;
			for ( ShipCoordinate candidate : pending ) {
				// Distance check has to be inclusive, otherwise we'll end up
				// with no candidate
				if ( distMap.get( candidate ) <= dist ) {
					dist = distMap.get( candidate );
					current = candidate;
				}
			}
			pending.remove( current );

			// If we've reached the end coordinate, then don't bother going further.
			// TODO: Remove this and allow the loop to complete, since it might find a better path?
			if ( current == end ) {
				break;
			}

			// Compute distances and predecessors for neighbours of current node,
			// and update them if we've found a shorter path

			// Diagonal movement
			neighbours.clear();
			diagonalAdj.getAdjacentCoords( this, current, neighbours );
			for ( ShipCoordinate neigh : neighbours ) {
				if ( pending.contains( neigh ) && neigh.isSquare() ) {
					dist = distMap.get( current ) + diagonalWeight;

					if ( dist < distMap.get( neigh ) ) {
						distMap.put( neigh, dist );
						prevMap.put( neigh, current );
					}
				}
			}

			// Orthogonal movement
			neighbours.clear();
			defaultAdj.getAdjacentCoords( this, current, neighbours );
			for ( ShipCoordinate neigh : neighbours ) {
				if ( pending.contains( neigh ) ) {
					if ( neigh.isSquare() ) {
						dist = distMap.get( current ) + orthogonalWeight;
					}
					else if ( neigh.isTeleportPad() ) {
						dist = distMap.get( current ) + tpadWeight;
					}
					else {
						// Door, either H or V
						dist = distMap.get( current ) + doorWeight;
					}

					if ( dist < distMap.get( neigh ) ) {
						distMap.put( neigh, dist );
						prevMap.put( neigh, current );
					}
				}
			}
		}

		// Construct the path
		Stack<ShipCoordinate> path = new Stack<ShipCoordinate>();
		ShipCoordinate predecessor = path.push( end );
		while ( !predecessor.equals( start ) ) {
			predecessor = prevMap.get( path.peek() );
			if ( predecessor == null ) {
				// If we encounter a node with no predecessor (before we've reached start node),
				// then that means that there's no valid path
				return null;
			}
			else {
				path.push( predecessor );
			}
		}

		return path;
	}
}
