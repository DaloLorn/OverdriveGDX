package com.ftloverdrive.command;


public interface ModelCommand extends PlayerCommand {

	/**
	 * Use this method when the command is created to specify
	 * the model that should be affected by this command.
	 */
	public void setAffectedModel( int targetModelRefId );
}
