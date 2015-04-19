package com.ftloverdrive.event;

/**
 * A marker class to distinguish between local and non-local events.
 * 
 * Local events are events that are not supposed to be sent to the
 * server, and are handled fully by the local client -- for instance
 * because we don't want to suffer the overhead involved in waiting
 * for the server's confirmation for an action that has no bearing on
 * the other player (eg. an input wrapper event, or an event telling
 * an actor to play an animation)
 */
public abstract class LocalEvent extends AbstractOVDEvent {
}
