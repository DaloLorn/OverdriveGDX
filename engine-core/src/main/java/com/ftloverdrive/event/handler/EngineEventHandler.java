package com.ftloverdrive.event.handler;

import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.engine.ModelDestructionEvent;
import com.ftloverdrive.event.engine.ModelDestructionListener;
import com.ftloverdrive.event.engine.RequestGameStateEvent;
import com.ftloverdrive.event.engine.SignalReadyEvent;
import com.ftloverdrive.model.OVDModel;
import com.ftloverdrive.ui.screen.ConnectScreen;


public class EngineEventHandler implements OVDEventHandler {

	private Class[] eventClasses;
	private Class[] listenerClasses;


	public EngineEventHandler() {
		eventClasses = new Class[] {
				ModelDestructionEvent.class,
				RequestGameStateEvent.class,
				SignalReadyEvent.class
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

			int[] refIds = event.getModelRefIds();
			for ( int refId : refIds ) {
				OVDModel model = context.getReferenceManager().getObject( refId, OVDModel.class );
				if ( model == null )
					continue;
				if ( model instanceof Disposable )
					( (Disposable)model ).dispose();
				context.getReferenceManager().forget( refId );
				context.getNetManager().returnRefId( refId );

				for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
					if ( listeners[i] == ModelDestructionListener.class ) {
						( (ModelDestructionListener)listeners[i + 1] ).modelDestroyed( context, event );
					}
				}
			}
		}
		else if ( e instanceof RequestGameStateEvent ) {
			RequestGameStateEvent event = (RequestGameStateEvent)e;

			context.getReferenceManager().addObject( event.getGameModel(), event.getGameModelRefId() );
			context.setGameModelRefId( event.getGameModelRefId() );
		}
		else if ( e instanceof SignalReadyEvent ) {
			SignalReadyEvent event = (SignalReadyEvent)e;

			if ( event.isAborted() ) {
				// TODO
			}
			else {
				ConnectScreen screen = (ConnectScreen)context.getGame().getScreen();
				screen.setCondition( null );
			}
		}
	}

	@Override
	public void disposeEvent( OVDEvent e ) {
	}
}
