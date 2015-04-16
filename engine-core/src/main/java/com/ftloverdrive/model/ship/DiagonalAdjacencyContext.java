package com.ftloverdrive.model.ship;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.utils.Pools;


public class DiagonalAdjacencyContext implements AdjacencyContext {

	/**
	 * This coordinate gets recycled until it's a returnable result.
	 * Then this variable will point to a new instance to recycle.
	 */
	protected ShipCoordinate tmpCoord;
	private int i;


	public DiagonalAdjacencyContext() {
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
			Set<ShipCoordinate> roomCoords = new HashSet<ShipCoordinate>();
			for ( ShipCoordinate c : roomSquareCoords )
				roomCoords.add( c );

			for ( i = 0; i < roomSquareCoords.length; i++ ) {
				ShipCoordinate square = roomSquareCoords[i];
				if ( square.equals( coord ) ) continue;

				// Orthogonal
				tmpCoord.init( coord.x - 1, coord.y - 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					if ( contains( roomCoords, coord.x - 1, coord.y ) &&
							contains( roomCoords, coord.x, coord.y - 1 ) ) {
						results.add( square );
					}
					continue;
				}

				tmpCoord.init( coord.x - 1, coord.y + 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					if ( contains( roomCoords, coord.x - 1, coord.y ) &&
							contains( roomCoords, coord.x, coord.y + 1 ) ) {
						results.add( square );
					}
					continue;
				}

				tmpCoord.init( coord.x + 1, coord.y - 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					if ( contains( roomCoords, coord.x + 1, coord.y ) &&
							contains( roomCoords, coord.x, coord.y - 1 ) ) {
						results.add( square );
					}
					continue;
				}

				tmpCoord.init( coord.x + 1, coord.y + 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					if ( contains( roomCoords, coord.x + 1, coord.y ) &&
							contains( roomCoords, coord.x, coord.y + 1 ) ) {
						results.add( square );
					}
					continue;
				}
			}
		}
	}

	private boolean contains( Set<ShipCoordinate> roomCoords, int x, int y ) {
		tmpCoord.init( x, y, 0 );
		return roomCoords.contains( tmpCoord );
	}
}
