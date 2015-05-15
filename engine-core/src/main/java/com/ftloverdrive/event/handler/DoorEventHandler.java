package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.ship.DoorPropertyEvent;
import com.ftloverdrive.event.ship.DoorPropertyListener;
import com.ftloverdrive.event.ship.DoorPropertySentinel;
import com.ftloverdrive.model.ship.DoorModel;
import com.ftloverdrive.util.OVDConstants;


public class DoorEventHandler implements OVDEventHandler {
	private Class[] eventClasses;
	private Class[] listenerClasses;


	public DoorEventHandler() {
		eventClasses = new Class[] {
				DoorPropertyEvent.class
		};
		listenerClasses = new Class[] {
				DoorPropertyListener.class,
				DoorPropertySentinel.class
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
		if ( !scrutinize( context, e ) ) {
			// TODO: This part would normally be performed by the server when it received the event.
			e.cancel();
			return;
		}

		if ( e instanceof DoorPropertyEvent ) {
			DoorPropertyEvent event = (DoorPropertyEvent)e;

			for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
				if ( listeners[i] == DoorPropertySentinel.class ) {
					( (DoorPropertySentinel)listeners[i + 1] ).doorPropertyChanging( context, event );
				}
			}

			int doorRefId = event.getModelRefId();
			DoorModel doorModel = context.getReferenceManager().getObject( doorRefId, DoorModel.class );
			if ( event.getPropertyType() == PropertyEvent.BOOL_TYPE ) {
				if ( event.getAction() == PropertyEvent.SET_ACTION ) {
					boolean value = event.getBoolValue();
					String key = event.getPropertyKey();
					doorModel.getProperties().setBool( key, value );
				}
				else if ( event.getAction() == PropertyEvent.TOGGLE_ACTION ) {
					String key = event.getPropertyKey();
					doorModel.getProperties().toggleBool( key );
				}
			}
			else if ( event.getPropertyType() == PropertyEvent.INT_TYPE ) {
				if ( event.getAction() == PropertyEvent.SET_ACTION ) {
					int value = event.getIntValue();
					String key = event.getPropertyKey();
					doorModel.getProperties().setInt( key, value );
				}
				else if ( event.getAction() == PropertyEvent.INCREMENT_ACTION ) {
					int value = event.getIntValue();
					String key = event.getPropertyKey();
					doorModel.getProperties().incrementInt( key, value );
				}
			}

			for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
				if ( listeners[i] == DoorPropertyListener.class ) {
					( (DoorPropertyListener)listeners[i + 1] ).doorPropertyChanged( context, event );
				}
			}
		}
	}

	@Override
	public void disposeEvent( OVDEvent e ) {
		if ( e.getClass() == DoorPropertyEvent.class ) {
			Pools.get( DoorPropertyEvent.class ).free( (DoorPropertyEvent)e );
		}
	}

	private boolean scrutinize( OverdriveContext context, OVDEvent e ) {
		if ( e instanceof DoorPropertyEvent ) {
			DoorPropertyEvent event = (DoorPropertyEvent)e;

			if ( event.getPropertyType() == PropertyEvent.INT_TYPE ) {
				if ( event.getPropertyKey().equals( OVDConstants.DOOR_HEALTH ) ) {
					if ( event.getAction() == PropertyEvent.SET_ACTION ) {
					}
					else if ( event.getAction() == PropertyEvent.INCREMENT_ACTION ) {
						DoorModel doorModel = context.getReferenceManager().getObject( event.getModelRefId(), DoorModel.class );
						int max = doorModel.getProperties().getInt( OVDConstants.DOOR_HEALTH_MAX );
						int cur = doorModel.getProperties().getInt( OVDConstants.DOOR_HEALTH );
						int delta = event.getIntValue();

						return delta != 0 && delta > 0 ? cur < max : cur > 0;
					}
				}
			}
		}

		return true;
	}
}
