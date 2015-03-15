package com.ftloverdrive.model.ship;

import java.util.Collection;

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

			for ( i = 0; i < roomSquareCoords.length; i++ ) {
				ShipCoordinate square = roomSquareCoords[i];
				if ( square.equals( coord ) ) continue;

				// Orthogonal
				tmpCoord.init( coord.x - 1, coord.y - 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					results.add( square );
					continue;
				}

				tmpCoord.init( coord.x - 1, coord.y + 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					results.add( square );
					continue;
				}

				tmpCoord.init( coord.x + 1, coord.y - 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					results.add( square );
					continue;
				}

				tmpCoord.init( coord.x + 1, coord.y + 1, 0 );
				if ( square.equals( tmpCoord ) ) {
					results.add( square );
					continue;
				}
			}
		}
	}
}
