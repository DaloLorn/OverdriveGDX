package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.engine.ModelDestructionEvent;
import com.ftloverdrive.event.engine.ModelDestructionListener;
import com.ftloverdrive.event.incident.IncidentCreationEvent;
import com.ftloverdrive.model.OVDModel;


public class EngineEventHandler implements OVDEventHandler {

	private Class[] eventClasses;
	private Class[] listenerClasses;


	public EngineEventHandler() {
		eventClasses = new Class[] {
				ModelDestructionEvent.class
		};
		listenerClasses = new Class[] {
				ModelDestructionListener.class
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
		if ( e instanceof ModelDestructionEvent ) {
			ModelDestructionEvent event = (ModelDestructionEvent)e;

			int refId = event.getModelRefId();
			OVDModel model = context.getReferenceManager().getObject( refId, OVDModel.class );
			if ( model instanceof Disposable )
				( (Disposable)model ).dispose();
			context.getReferenceManager().forget( refId );

			for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
				if ( listeners[i] == ModelDestructionListener.class ) {
					( (ModelDestructionListener)listeners[i + 1] ).modelDestroyed( context, event );
				}
			}
		}
	}

	@Override
	public void disposeEvent( OVDEvent e ) {
		if ( e.getClass() == IncidentCreationEvent.class ) {
			Pools.get( IncidentCreationEvent.class ).free( (IncidentCreationEvent)e );
		}
	}
}
