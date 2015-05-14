package com.ftloverdrive.model.incident.requirement;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.model.incident.PlotBranchCriteria.CriteriaResult;
import com.ftloverdrive.model.incident.PlotBranchRequirement;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.util.OVDConstants;


public class ShipRequirement implements PlotBranchRequirement {

	private String shipId = null;


	protected ShipRequirement() {
	}

	public ShipRequirement( String shipId ) {
		this.shipId = shipId;
	}

	@Override
	public CriteriaResult evaluate( OverdriveContext context ) {
		int gameRefId = context.getGameModelRefId();
		GameModel gameModel = context.getReferenceManager().getObject( gameRefId, GameModel.class );
		int playerRefId = context.getNetManager().getLocalPlayerRefId();
		int shipRefId = gameModel.getPlayerShip( playerRefId );
		ShipModel shipModel = context.getReferenceManager().getObject( shipRefId, ShipModel.class );
		String blueprintId = shipModel.getProperties().getString( OVDConstants.BLUEPRINT_NAME );

		if ( blueprintId.equals( shipId ) )
			return CriteriaResult.BLUE;
		else
			return CriteriaResult.INVISIBLE_UNAVAILABLE;
	}
}
