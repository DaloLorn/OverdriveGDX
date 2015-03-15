package com.ftloverdrive.model.ship;

import java.util.Collection;
import java.util.Set;

import com.badlogic.gdx.utils.Pools;


/**
 * Includes orthogonal neighbours, as well as coordinates neighbouring orthogonally via doors.
 *
 */
public class DefaultAdjacencyContext implements AdjacencyContext {

	/**
	 * This coordinate gets recycled until it's a returnable result.
	 * Then this variable will point to a new instance to recycle.
	 */
	protected ShipCoordinate tmpCoord;
	private int i;


	public DefaultAdjacencyContext() {
		tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
	}


	/**
	 * Finds adjacent ShipCoordinates, then adds them to an existing
	 * Collection.
	 *
	 * The coordinate passed to this method will not be added to the results.
	 */
	public void getAdjacentCoords( ShipLayout layout, ShipCoordinate coord, Collection<ShipCoordinate> results ) {

		if ( coord.v == ShipCoordinate.TYPE_SQUARE ) {
			// If this is a square.

			int roomModelRefId = layout.getRoomRefIdOfCoords( coord );
			ShipCoordinate[] roomSquareCoords = layout.getRoomCoords( roomModelRefId );

			for ( i = 0; i < roomSquareCoords.length; i++ ) {
				ShipCoordinate square = roomSquareCoords[i];
				if ( square.equals( coord ) ) continue;

				// Orthogonal
				tmpCoord.init( coord.x - 1, coord.y, 0 );
				if ( square.equals( tmpCoord ) ) {
					results.add( square );
					continue;
				}

				tmpCoord.init( coord.x, coord.y - 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					results.add( square );
					continue;
				}

				tmpCoord.init( coord.x + 1, coord.y, 0 );
				if ( square.equals( tmpCoord ) ) {
					results.add( square );
					continue;
				}

				tmpCoord.init( coord.x, coord.y + 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					results.add( square );
					continue;
				}
			}

			// Check doors adjacent to this tile
			Set<ShipCoordinate> allCoords = layout.getAllShipCoords();

			for ( ShipCoordinate door : allCoords ) {
				if ( door.v != ShipCoordinate.TYPE_DOOR_H && door.v != ShipCoordinate.TYPE_DOOR_V ) {
					continue;
				}

				tmpCoord.init( coord.x, coord.y + 1, ShipCoordinate.TYPE_DOOR_H );
				if ( door.equals( tmpCoord ) ) {
					results.add( door );
					continue;
				}

				// Doors snap to the left/upper side of the tile, hence checking current tile
				tmpCoord.init( coord.x, coord.y, ShipCoordinate.TYPE_DOOR_H );
				if ( door.equals( tmpCoord ) ) {
					results.add( door );
					continue;
				}

				tmpCoord.init( coord.x + 1, coord.y, ShipCoordinate.TYPE_DOOR_V );
				if ( door.equals( tmpCoord ) ) {
					results.add( door );
					continue;
				}

				// Doors snap to the left/upper side of the tile, hence checking current tile
				tmpCoord.init( coord.x, coord.y, ShipCoordinate.TYPE_DOOR_V );
				if ( door.equals( tmpCoord ) ) {
					results.add( door );
					continue;
				}
			}
		}
		else if ( coord.v == ShipCoordinate.TYPE_WALL_H ) {
			// Horizontal Wall.
			Set<ShipCoordinate> allCoords = layout.getAllShipCoords();
			tmpCoord.init( coord.x, coord.y - 1, 0 );
			if ( allCoords.contains( tmpCoord ) ) {
				results.add( tmpCoord );
				tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			}
			tmpCoord.init( coord.x, coord.y + 1, 0 );
			if ( allCoords.contains( tmpCoord ) ) {
				results.add( tmpCoord );
				tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			}
		}
		else if ( coord.v == ShipCoordinate.TYPE_WALL_V ) {
			// Vertical Wall.
			Set<ShipCoordinate> allCoords = layout.getAllShipCoords();
			tmpCoord.init( coord.x - 1, coord.y, 0 );
			if ( allCoords.contains( tmpCoord ) ) {
				results.add( tmpCoord );
				tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			}
			tmpCoord.init( coord.x + 1, coord.y, 0 );
			if ( allCoords.contains( tmpCoord ) ) {
				results.add( tmpCoord );
				tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			}
		}
		else if ( coord.v == ShipCoordinate.TYPE_DOOR_H ) {
			// Horizontal Door.
			Set<ShipCoordinate> allCoords = layout.getAllShipCoords();
			tmpCoord.init( coord.x, coord.y - 1, 0 );
			if ( allCoords.contains( tmpCoord ) ) {
				results.add( tmpCoord );
				tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			}
			tmpCoord.init( coord.x, coord.y, 0 );
			if ( allCoords.contains( tmpCoord ) ) {
				results.add( tmpCoord );
				tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			}
		}
		else if ( coord.v == ShipCoordinate.TYPE_DOOR_V ) {
			// Vertical Door.
			Set<ShipCoordinate> allCoords = layout.getAllShipCoords();
			tmpCoord.init( coord.x - 1, coord.y, 0 );
			if ( allCoords.contains( tmpCoord ) ) {
				results.add( tmpCoord );
				tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			}
			tmpCoord.init( coord.x, coord.y, 0 );
			if ( allCoords.contains( tmpCoord ) ) {
				results.add( tmpCoord );
				tmpCoord = Pools.get( ShipCoordinate.class ).obtain();
			}
		}
	}
}
