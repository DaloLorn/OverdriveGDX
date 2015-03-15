package com.ftloverdrive.command;


public interface PlayerCommand extends Command {

	/**
	 * Use this method when the command is created to specify
	 * the player that issued the command.
	 */
	public void setSourcePlayer( int sourcePlayerRefId );
}
