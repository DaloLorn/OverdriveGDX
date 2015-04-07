package com.ftloverdrive.net;

import java.io.Serializable;


public class Range implements Serializable {

	public int start = 0;
	public int end = 0;


	public Range( int start, int end ) {
		this.start = start;
		this.end = end;
	}

	public Range() {
	}

	public String toString() {
		return getClass().getSimpleName() + " { " + start + ", " + end + " }";
	}
}
