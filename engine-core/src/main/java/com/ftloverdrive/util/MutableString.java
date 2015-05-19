package com.ftloverdrive.util;

/**
 * An object that wraps a string.
 */
public class MutableString {
	protected String s = "";

	public void set( String s ) {
		if ( s == null ) s = "";
		this.s = s;
	}

	public String get() {
		return s;
	}
	
	public String toString() {
		return s;
	}
}
