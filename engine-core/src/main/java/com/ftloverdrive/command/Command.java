package com.ftloverdrive.command;

import com.ftloverdrive.core.OverdriveContext;

/**
 * Commands bundle a set of actions that need to be performed
 * in order to achieve some effect in-game.
 * 
 * For example, clicking on a room with a crew member selected
 * issues a MoveCommand to the specified coordinates. The command's
 * execute() method changes the properties and creates and posts
 * events as needed.
 * 
 * The same system can later be used by AI, by putting Commands
 * in a queue.
 */
public interface Command {

	public void execute( OverdriveContext context );
}
