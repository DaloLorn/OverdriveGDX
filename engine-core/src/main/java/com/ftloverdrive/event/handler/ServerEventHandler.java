package com.ftloverdrive.event.handler;

import java.util.ArrayList;
import java.util.List;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEvent;
import com.ftloverdrive.event.OVDEventHandler;
import com.ftloverdrive.event.engine.DelayedEvent;
import com.ftloverdrive.event.engine.RequestGameStateEvent;
import com.ftloverdrive.event.engine.SignalReadyEvent;
import com.ftloverdrive.event.engine.TickListenerEvent;
import com.ftloverdrive.model.DefaultGameModel;
import com.ftloverdrive.model.GameModel;


public class ServerEventHandler implements OVDEventHandler {

	private Class[] eventClasses;
	private Class[] listenerClasses;

	// TODO: This is temporary and for testing purposes, replace with proper
	// readiness registry or something
	private List<SignalReadyEvent> readyList = new ArrayList<SignalReadyEvent>();


	public ServerEventHandler() {
		eventClasses = new Class[] {
				TickListenerEvent.class,
				DelayedEvent.class,
				RequestGameStateEvent.class,
				SignalReadyEvent.class
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
		}
		else if ( e instanceof DelayedEvent ) {
			DelayedEvent event = (DelayedEvent)e;

			context.getGame().getServer().postDelayedEvent( event );
		}
		else if ( e instanceof RequestGameStateEvent ) {
			RequestGameStateEvent event = (RequestGameStateEvent)e;

			int gameModelRefId = context.getGameModelRefId();
			if ( gameModelRefId == -1 ) {
				gameModelRefId = context.getNetManager().requestNewRefId();
				GameModel gameModel = new DefaultGameModel();
				context.getReferenceManager().addObject( gameModel, gameModelRefId );
				context.setGameModelRefId( gameModelRefId );
			}

			event.setGameModelRefId( gameModelRefId );
			DefaultGameModel gameModel = context.getReferenceManager().getObject( gameModelRefId, DefaultGameModel.class );
			event.setGameModel( gameModel );

			context.getGame().getServer().getEventManager().postDelayedEvent( event );
		}
		else if ( e instanceof SignalReadyEvent ) {
			SignalReadyEvent event = (SignalReadyEvent)e;

			readyList.add( event );
			if ( readyList.size() == context.getGame().getServer().countConnectedClients() ) {
				while ( readyList.size() > 0 ) {
					context.getGame().getServer().getEventManager().postDelayedEvent( readyList.remove( 0 ) );
				}
			}
		}
	}

	@Override
	public void disposeEvent( OVDEvent e ) {
	}
}
