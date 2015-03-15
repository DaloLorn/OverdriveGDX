package com.ftloverdrive.command;

import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.ship.DoorPropertyEvent;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.model.ship.DoorModel;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.util.OVDConstants;

public class DoorToggleCommand implements ModelCommand {
	private int targetRefId = -1;
	private int sourceRefId = -1;


	public void setAffectedModel( int targetModelRefId ) {
		targetRefId = targetModelRefId;
	}

	public void setSourcePlayer( int sourcePlayerRefId ) {
		sourceRefId = sourcePlayerRefId;
	}

	public void execute( OverdriveContext context ) {
		// TODO: Verification should be performed by the server
		if ( !verify( context ) )
			return;
	
		DoorPropertyEvent ev = Pools.get( DoorPropertyEvent.class ).obtain();
		ev.init( targetRefId, DoorPropertyEvent.BOOL_TYPE,
				DoorPropertyEvent.TOGGLE_ACTION, OVDConstants.DOOR_OPEN, false );
		ev.setSource( sourceRefId );
		context.getScreenEventManager().postDelayedEvent( ev );
	}

	private boolean verify( OverdriveContext context ) {
		DoorModel doorModel = context.getReferenceManager().getObject( targetRefId, DoorModel.class );
		if ( doorModel.getProperties().getBool( OVDConstants.DOOR_LOCKED ) )
			return false;

		int localPlayerId = context.getNetManager().getLocalPlayerRefId();
		GameModel gameModel = context.getReferenceManager().getObject( context.getGameModelRefId(), GameModel.class );
		int shipId = gameModel.getPlayerShip( localPlayerId );
		ShipModel shipModel = context.getReferenceManager().getObject( shipId, ShipModel.class );

		return shipModel.getLayout().hasDoor( targetRefId );
	}
}
