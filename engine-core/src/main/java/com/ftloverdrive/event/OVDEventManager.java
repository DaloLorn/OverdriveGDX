package com.ftloverdrive.event;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import com.ftloverdrive.core.OverdriveContext;


/**
 * Queues and dispatches OVDEvents.
 *
 * TODO: Maybe coalesce repeat events when posting, like in java.awt.EventQueue?
 */
public class OVDEventManager {

	private Deque<OVDEvent> outQueue = new LinkedBlockingDeque<OVDEvent>();
	private Deque<OVDEvent> inQueue = new LinkedBlockingDeque<OVDEvent>();
	OVDEventListenerList outListenerList = new OVDEventListenerList();
	OVDEventListenerList inListenerList = new OVDEventListenerList();
	Map<Class, OVDEventHandler> handlerMap = new HashMap<Class, OVDEventHandler>();

	private final boolean serverMode;


	public OVDEventManager() {
		this( false );
	}

	public OVDEventManager( boolean serverMode ) {
		this.serverMode = serverMode;
	}

	/**
	 * Dispatches any pending events and returns.
	 */
	public void processEvents( OverdriveContext context ) {
		OVDEvent event;
		while ( ( event = inQueue.poll() ) != null ) {
			OVDEventHandler h = handlerMap.get( event.getClass() );
			if ( h != null ) {
				h.handle( context, event, inListenerList.getListenerList() );
				h.disposeEvent( event );
			}
			else if ( serverMode ) {
				// TODO: Scrutinize the event
				postDelayedEvent( event );
			}
			else {
				System.out.println( "Unhandled event: " + event );
			}
		}
		while ( ( event = outQueue.poll() ) != null ) {
			if ( event.isCancelled() )
				continue; // TODO: Dispose the event?

			if ( event instanceof AbstractLocalEvent ) {
				// Local events are not sent to the server.
				postDelayedInboundEvent( event );
			}
			else {
				if ( serverMode ) {
					context.getGame().getServer().sendAllTCP( event );
				}
				else {
					context.getGame().sendTCP( event );
					// Pretend it went to the server and back.
					// postDelayedInboundEvent( event );
				}
			}
		}
	}


	/**
	 * Adds an event to the end of the inbound queue. (thread-safe)
	 * This should not normally be used.
	 */
	public void postDelayedInboundEvent( OVDEvent e ) {
		inQueue.addLast( e );
	}

	/**
	 * Adds an event to the end of the outbound queue. (thread-safe)
	 */
	public void postDelayedEvent( OVDEvent e ) {
		outQueue.addLast( e );
	}

	/**
	 * Enqueues a delayed event that will be processed only after the required amount
	 * of game ticks has passed.
	 * 
	 * @param ticks
	 *            how many ticks have to pass before the event is processed
	 */
	public void postDelayedEvent( OVDEvent e, int ticks ) {
		// TODO
	}

	/**
	 * Adds an event to the start of the outbound queue. (thread-safe)
	 */
	public void postPreemptiveEvent( OVDEvent e ) {
		outQueue.addFirst( e );
	}

	/**
	 * Sets the handler for a specific event class.
	 */
	public void setEventHandler( Class eventClass, OVDEventHandler h ) {
		handlerMap.put( eventClass, h );
	}

	/**
	 * Adds a listener for incoming events.
	 *
	 * @param l
	 *            a listener to be notified
	 * @param listenerClass
	 *            a class a handler expects that the listener can be cast as
	 */
	public <T extends OVDEventListener> void addEventListener( T l, Class<T> listenerClass ) {
		inListenerList.add( listenerClass, l );
	}

	/**
	 * TODO: This is just about incrementing an internal ref counter. No need to pose as
	 * listener registration?
	 * 
	 * @param tickCount
	 *            the tick count that the listener wants to react to
	 */
	public void addTickListener( int tickCount, TickListener l ) {
		if ( tickCount < 1 )
			throw new IllegalArgumentException( "Argument must be greater than 0." );

		if ( l != null )
			inListenerList.add( TickListener.class, l );
		if ( tickCount > 1 ) {
			// Inform the server's clock that it now needs to issue tick events for this tick count.
			TickListenerEvent ev = new TickListenerEvent( tickCount, true );
			postDelayedEvent( ev );
		}
	}

	/**
	 * 
	 * @param tickCount
	 *            the tick count that the listener was reacting to
	 */
	public void removeTickListener( int tickCount, TickListener l ) {
		if ( tickCount < 1 )
			throw new IllegalArgumentException( "Argument must be greater than 0." );

		if ( l != null )
			inListenerList.remove( TickListener.class, l );
		if ( tickCount > 1 ) {
			// Inform the server's clock that it no longer needs to issue tick events for this tick count.
			TickListenerEvent ev = new TickListenerEvent( tickCount, false );
			postDelayedEvent( ev );
		}
	}
}
