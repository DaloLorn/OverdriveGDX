package com.ftloverdrive.event.handler;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.TickListenerEvent;


public class ServerEventHandler implements OVDEventHandler {

	private Class[] eventClasses;
	private Class[] listenerClasses;


	public ServerEventHandler() {
		eventClasses = new Class[] {
				TickListenerEvent.class
		};
		listenerClasses = new Class[] {
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
		if ( e instanceof TickListenerEvent ) {
			TickListenerEvent event = (TickListenerEvent)e;

			if ( event.isIncrement() )
				context.getGame().getServer().incrTick( event.getTickCount() );
			else
				context.getGame().getServer().decrTick( event.getTickCount() );
			event.cancel();
		}
	}

	@Override
	public void disposeEvent( OVDEvent e ) {
	}
}
