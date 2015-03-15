package com.ftloverdrive.command;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.player.CrewMoveOrderEvent;
import com.ftloverdrive.model.ship.ShipCoordinate;


public class CrewMoveCommand implements PlayerCommand {

	private int sourceRefId = -1;
	private ShipCoordinate targetCoord = null;


	public void setSourcePlayer( int sourcePlayerRefId ) {
		sourceRefId = sourcePlayerRefId;
	}

	public void setTargetCoord( ShipCoordinate coord ) {
		targetCoord = coord;
	}

	public void execute( OverdriveContext context ) {
		CrewMoveOrderEvent ev = Pools.get( CrewMoveOrderEvent.class ).obtain();
		// TODO: Pass local player's current crew selection into initializer here!
		ev.init( sourceRefId, new int[] {}, targetCoord );
		ev.setSource( sourceRefId );
		context.getScreenEventManager().postDelayedEvent( ev );
	}
}
