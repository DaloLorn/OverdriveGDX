package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.ship.DoorPropertyEvent;
import com.ftloverdrive.event.ship.DoorPropertyListener;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.model.ship.DoorModel;

public class DoorEventHandler implements OVDEventHandler {
	private Class[] eventClasses;
	private Class[] listenerClasses;

	public DoorEventHandler() {
		eventClasses = new Class[] {
			DoorPropertyEvent.class
		};
		listenerClasses = new Class[] {
			DoorPropertyListener.class,
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
		if ( e instanceof DoorPropertyEvent ) {
			DoorPropertyEvent event = (DoorPropertyEvent)e;

			int doorRefId = event.getDoorRefId();
			DoorModel doorModel = context.getReferenceManager().getObject( doorRefId, DoorModel.class );
			if ( event.getPropertyType() == DoorPropertyEvent.BOOL_TYPE ) {
				if ( event.getAction() == DoorPropertyEvent.SET_ACTION ) {
					boolean value = event.getBoolValue();
					String key = event.getPropertyKey();
					doorModel.getProperties().setBool( key, value );
				}
				else if ( event.getAction() == ShipPropertyEvent.TOGGLE_ACTION ) {
					String key = event.getPropertyKey();
					doorModel.getProperties().toggleBool( key );
				}
			}

			for ( int i = listeners.length-2; i >= 0; i-=2 ) {
				if ( listeners[i] == DoorPropertyListener.class ) {
					((DoorPropertyListener)listeners[i+1]).doorPropertyChanged( context, event );
				}
			}
		}
	}

	@Override
	public void disposeEvent(OVDEvent e) {
		if ( e.getClass() == DoorPropertyEvent.class ) {
			Pools.get( DoorPropertyEvent.class ).free( (DoorPropertyEvent)e );
		}
	}
}
