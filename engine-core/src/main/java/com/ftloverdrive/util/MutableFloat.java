package com.ftloverdrive.util;


/**
 * An object that wraps a float.
 */
public class MutableFloat {
	protected float f = 0;

	public void set( float f ) {
		this.f = f;
	}

	public void increment( float f ) {
		this.f += f;
	}

	public float get() {
		return f;
	}
}
