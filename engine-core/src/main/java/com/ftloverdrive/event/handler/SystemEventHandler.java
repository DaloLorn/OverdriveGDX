package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyListener;
import com.ftloverdrive.event.system.SystemPropertySentinel;
import com.ftloverdrive.model.system.SystemModel;
import com.ftloverdrive.util.OVDConstants;


public class SystemEventHandler implements OVDEventHandler {
	private Class[] eventClasses;
	private Class[] listenerClasses;


	public SystemEventHandler() {
		eventClasses = new Class[] {
				SystemPropertyEvent.class
		};
		listenerClasses = new Class[] {
				SystemPropertyListener.class,
				SystemPropertySentinel.class
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
		if ( e instanceof SystemPropertyEvent ) {
			SystemPropertyEvent event = (SystemPropertyEvent)e;

			for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
				if ( listeners[i] == SystemPropertySentinel.class ) {
					( (SystemPropertySentinel)listeners[i + 1] ).systemPropertyChanging( context, event );
				}
			}
			
			if ( e.isCancelled() )
				return;

			int systemRefId = event.getModelRefId();
			SystemModel systemModel = context.getReferenceManager().getObject( systemRefId, SystemModel.class );
			if ( event.getPropertyType() == PropertyEvent.BOOL_TYPE ) {
				if ( event.getAction() == PropertyEvent.SET_ACTION ) {
					boolean value = event.getBoolValue();
					String key = event.getPropertyKey();
					systemModel.getProperties().setBool( key, value );
				}
				else if ( event.getAction() == PropertyEvent.TOGGLE_ACTION ) {
					String key = event.getPropertyKey();
					systemModel.getProperties().toggleBool( key );
				}
			}
			else if ( event.getPropertyType() == PropertyEvent.INT_TYPE ) {
				if ( event.getAction() == PropertyEvent.SET_ACTION ) {
					int value = event.getIntValue();
					String key = event.getPropertyKey();
					systemModel.getProperties().setInt( key, value );
				}
				else if ( event.getAction() == PropertyEvent.INCREMENT_ACTION ) {
					int value = event.getIntValue();
					String key = event.getPropertyKey();
					systemModel.getProperties().incrementInt( key, value );
				}

				if ( event.getPropertyKey().equals( OVDConstants.LEVEL ) ) {
					SystemPropertyEvent nEvent = Pools.get( SystemPropertyEvent.class ).obtain();
					nEvent.init( event.getModelRefId(), event.getAction(), OVDConstants.POWER_MAX, event.getIntValue() );
					context.getScreenEventManager().postDelayedEvent( nEvent );
				}
			}

			for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
				if ( listeners[i] == SystemPropertyListener.class ) {
					( (SystemPropertyListener)listeners[i + 1] ).systemPropertyChanged( context, event );
				}
			}
		}
	}

	@Override
	public void disposeEvent( OVDEvent e ) {
		if ( e.getClass() == SystemPropertyEvent.class ) {
			Pools.get( SystemPropertyEvent.class ).free( (SystemPropertyEvent)e );
		}
	}
}
