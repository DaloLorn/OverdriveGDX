package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.engine.TickEvent;
import com.ftloverdrive.event.engine.TickListener;


public class TickEventHandler implements OVDEventHandler {

	private Class[] eventClasses;
	private Class[] listenerClasses;


	public TickEventHandler() {
		eventClasses = new Class[] {
				TickEvent.class
		};
		listenerClasses = new Class[] {
				TickListener.class
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
		if ( e instanceof TickEvent ) {
			TickEvent event = (TickEvent)e;

			for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
				if ( listeners[i] == TickListener.class ) {
					( (TickListener)listeners[i + 1] ).ticksAccumulated( event );
				}
			}
		}
	}

	@Override
	public void disposeEvent( OVDEvent e ) {
		if ( e.getClass() == TickEvent.class ) {
			Pools.get( TickEvent.class ).free( (TickEvent)e );
		}
	}
}
