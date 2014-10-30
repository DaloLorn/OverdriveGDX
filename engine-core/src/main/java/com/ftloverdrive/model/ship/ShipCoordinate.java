package com.ftloverdrive.model.ship;

import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool.Poolable;


/**
 * An integer triple specifying the location in a ship's floorplan.
 *
 * Regular int arrays don't override the methods needed for use as Map
 * keys, so they'd only test for identity instead of equality.
 *
 * This class is not immutable, so be careful not to alter values. Maps get
 * confused when their keys change. Unless you *know* an instance isn't
 * used elsewhere, obtain a fresh object and set its values instead.
 *
 * Similarly, don't free() an instance unless you're sure it's unused.
 */
public class ShipCoordinate implements Poolable {
	public static final int TYPE_SQUARE = 0;
	public static final int TYPE_WALL_H = 1;
	public static final int TYPE_WALL_V = 2;

	public int x = 0;
	public int y = 0;
	public int v = 0;


	public ShipCoordinate() {
	}

	/**
	 * Pseudo constructor.
	 *
	 * @param x 0-based index from the left
	 * @param y 0-based index from the top
	 * @param v 0=square, 1=horizontal wall, 2=vertical wall
	 */
	public void init( int x, int y, int v ) {
		checkType(v);
		this.x = x;
		this.y = y;
		this.v = v;
	}

	/**
	 * Pseudo constructor to copy values from an existing coordinate.
	 */
	public void init( ShipCoordinate srcCoord ) {
		this.x = srcCoord.x;
		this.y = srcCoord.y;
		this.v = srcCoord.v;
	}

	private void checkType(int type) {
		if ( type != TYPE_SQUARE && type != TYPE_WALL_H && type != TYPE_WALL_V )
			throw new IllegalArgumentException( "Invalid coord type" );
	}


	/**
	 * A cell that has no walls to any side.
	 */
	public static ShipCoordinate[] square( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[1];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		return result;
	}

	/**
	 * A cell that only has a wall to the north.
	 */
	public static ShipCoordinate[] wNorth( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[2];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_H );
		return result;
	}

	/**
	 * A cell that only has a wall to the south.
	 */
	public static ShipCoordinate[] wSouth( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[2];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y + 1, TYPE_WALL_H );
		return result;
	}

	/**
	 * A cell that only has a wall to the west.
	 */
	public static ShipCoordinate[] wWest( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[2];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_V );
		return result;
	}

	/**
	 * A cell that only has a wall to the east.
	 */
	public static ShipCoordinate[] wEast( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[2];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x + 1, y, TYPE_WALL_V );
		return result;
	}

	/**
	 * A cell that has walls to the north and west.
	 */
	public static ShipCoordinate[] wNorthWest( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[3];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_H );
		result[2].init( x, y, TYPE_WALL_V );
		return result;
	}

	/**
	 * A cell that has walls to the north and east.
	 */
	public static ShipCoordinate[] wNorthEast( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[3];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_H );
		result[2].init( x + 1, y, TYPE_WALL_V );
		return result;
	}

	/**
	 * A cell that has walls to the south and west.
	 */
	public static ShipCoordinate[] wSouthWest( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[3];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y + 1, TYPE_WALL_H );
		result[2].init( x, y, TYPE_WALL_V );
		return result;
	}

	/**
	 * A cell that has walls to the south and east.
	 */
	public static ShipCoordinate[] wSouthEast( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[3];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y + 1, TYPE_WALL_H );
		result[2].init( x + 1, y, TYPE_WALL_V );
		return result;
	}

	/**
	 * A cell that has walls to the north and south.
	 */
	public static ShipCoordinate[] wNorthSouth( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[3];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_H );
		result[2].init( x, y + 1, TYPE_WALL_H );
		return result;
	}

	/**
	 * A cell that has walls to the west and east.
	 */
	public static ShipCoordinate[] wWestEast( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[3];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_V );
		result[2].init( x + 1, y, TYPE_WALL_V );
		return result;
	}

	/**
	 * A cell that has walls to the north, west and east.
	 */
	public static ShipCoordinate[] wNorthWestEast( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[4];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[3] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_V );
		result[2].init( x, y, TYPE_WALL_H );
		result[3].init( x + 1, y, TYPE_WALL_V );
		return result;
	}

	/**
	 * A cell that has walls to the north, south and east.
	 */
	public static ShipCoordinate[] wNorthSouthEast( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[4];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[3] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_H );
		result[2].init( x + 1, y, TYPE_WALL_V );
		result[3].init( x, y + 1, TYPE_WALL_H );
		return result;
	}

	/**
	 * A cell that has walls to the south, west and east.
	 */
	public static ShipCoordinate[] wSouthWestEast( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[4];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[3] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_V );
		result[2].init( x + 1, y, TYPE_WALL_V );
		result[3].init( x, y + 1, TYPE_WALL_H );
		return result;
	}

	/**
	 * A cell that has walls to the north, south and west.
	 */
	public static ShipCoordinate[] wNorthSouthWest( int x, int y ) {
		ShipCoordinate[] result = new ShipCoordinate[4];
		result[0] = Pools.get( ShipCoordinate.class ).obtain();
		result[1] = Pools.get( ShipCoordinate.class ).obtain();
		result[2] = Pools.get( ShipCoordinate.class ).obtain();
		result[3] = Pools.get( ShipCoordinate.class ).obtain();
		result[0].init( x, y, TYPE_SQUARE );
		result[1].init( x, y, TYPE_WALL_H );
		result[2].init( x, y, TYPE_WALL_V );
		result[3].init( x, y + 1, TYPE_WALL_H );
		return result;
	}

	@Override
	public boolean equals( Object o ) {
		if ( !(o instanceof ShipCoordinate) ) return false;
		ShipCoordinate other = (ShipCoordinate)o;
		return ( x == other.x && y == other.y && v == other.v );
	}

	@Override
	public int hashCode() {
		return mangle(x) | (mangle(y) << 1) | (mangle(v) << 2);
	}

	// Use Z-Order Curve to interleve coords' bits for uniqueness.
	// http://stackoverflow.com/questions/9858376/hashcode-for-3d-integer-coordinates-with-high-spatial-coherence
	// http://www.opensourcescripts.com/info/interleave-bits--aka-morton-ize-aka-z-order-curve-.html
	private int mangle( int n ) {
		n &= 0x000003ff;
		n = (n ^ (n << 16)) & 0xff0000ff;
		n = (n ^ (n <<  8)) & 0x0300f00f;
		n = (n ^ (n <<  4)) & 0x030c30c3;
		n = (n ^ (n <<  2)) & 0x09249249;
		return n;
	}


	@Override
	public void reset() {
		x = 0;
		y = 0;
		v = 0;
	}
}
