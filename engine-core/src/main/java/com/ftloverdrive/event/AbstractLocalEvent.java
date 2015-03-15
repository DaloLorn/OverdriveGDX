package com.ftloverdrive.event;

/**
 * A marker class to distinguish between local and non-local events,
 * for exception throwing to detect erronoeusly posted events.
 * 
 * Local events are handled only by the local client.
 * 
 * Basically only useful for wrapped input events that are
 * handled via the event manager in order to expose them to
 * listeners, which in turn can enqueue non-local events that
 * the server can scrutinize.
 * 
 * Should be posted using OVDEventManager.postDelayedInboundEvent(),
 * since we don't want to inform the server of local events.
 */
public abstract class AbstractLocalEvent extends AbstractOVDEvent {
}
