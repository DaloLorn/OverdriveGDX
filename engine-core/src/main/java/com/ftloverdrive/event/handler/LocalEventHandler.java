package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.local.LocalActorBoundaryEvent;
import com.ftloverdrive.event.local.LocalActorBoundaryListener;
import com.ftloverdrive.event.local.LocalActorClickedEvent;
import com.ftloverdrive.event.local.LocalActorClickedListener;

public class LocalEventHandler implements OVDEventHandler {
	private Class[] eventClasses;
	private Class[] listenerClasses;


	public LocalEventHandler() {
		eventClasses = new Class[] {
			LocalActorClickedEvent.class,
			LocalActorBoundaryEvent.class
		};
		listenerClasses = new Class[] {
			LocalActorClickedListener.class,
			LocalActorBoundaryListener.class
		};
	}

	@Override
	public Class[] getEventClasses() {
		return eventClasses;
	}

	@Override
	public Class[] getListenerClasses() {
		return listenerClasses;
	}

	@Override
	public void handle( OverdriveContext context, OVDEvent e, Object[] listeners ) {
		if ( e instanceof LocalActorClickedEvent ) {
			LocalActorClickedEvent event = (LocalActorClickedEvent) e;
			for ( int i = listeners.length-2; i >= 0; i-=2 ) {
				if ( listeners[i] == LocalActorClickedListener.class ) {
					((LocalActorClickedListener)listeners[i+1]).actorClicked( context, event );
				}
			}
		}
		else if ( e instanceof LocalActorBoundaryEvent ) {
			LocalActorBoundaryEvent event = (LocalActorBoundaryEvent) e;
			for ( int i = listeners.length-2; i >= 0; i-=2 ) {
				if ( listeners[i] == LocalActorBoundaryListener.class ) {
					((LocalActorBoundaryListener)listeners[i+1]).actorBoundaryCrossed( context, event );
				}
			}
		}
		// TODO other local events
	}

	@Override
	public void disposeEvent(OVDEvent e) {
		if ( e.getClass() == LocalActorClickedEvent.class ) {
			Pools.get( LocalActorClickedEvent.class ).free( (LocalActorClickedEvent)e );
		}
	}
}
