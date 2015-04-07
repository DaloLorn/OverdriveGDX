package com.ftloverdrive.event.engine;

import com.ftloverdrive.event.OVDEventListener;
import com.ftloverdrive.event.engine.TickEvent;


public interface TickListener extends OVDEventListener {

	/**
	 * A number of units of game-time have elapsed.
	 */
	public void ticksAccumulated( TickEvent e );
}
