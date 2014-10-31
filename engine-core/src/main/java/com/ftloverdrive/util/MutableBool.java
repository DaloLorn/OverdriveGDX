package com.ftloverdrive.util;

/**
 * An object that wraps a boolean.
 */
public class MutableBool {
	protected boolean b = false;

	public void set( boolean b ) {
		this.b = b;
	}

	public void toggle() {
		b = !b;
	}

	public boolean get() {
		return b;
	}
}
