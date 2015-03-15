package com.ftloverdrive.model.ship;

import java.util.Collection;


/**
 * An interface for checking the adjacency of various coordinates using different logic, eg.
 * - DiagonalAdjacencyContext
 * - OrthogonalAdjacencyContext
 * - DoorAdjacencyContext
 * etc
 */
public interface AdjacencyContext {

	public void getAdjacentCoords( ShipLayout layout, ShipCoordinate coord, Collection<ShipCoordinate> results );
}
