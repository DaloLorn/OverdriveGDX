package com.ftloverdrive.model;

import com.ftloverdrive.core.OverdriveContext;


public interface Powerable {

	/**
	 * Sets the number of power bars this is currently using.
	 */
	public void setCurrentPower( OverdriveContext context, int n );

	public int getCurrentPower();


	/**
	 * Returns the total number of power bars this can currently hold.
	 */
	public int getPowerCapacity();


	/**
	 * Returns the number of power pars to add/remove with each power adjustment.
	 */
	public int getPowerIncrement();


	/**
	 * Returns true to draw reserve power, false to always be fully powered (subsystem).
	 */
	public boolean isSelfPowered();
}
