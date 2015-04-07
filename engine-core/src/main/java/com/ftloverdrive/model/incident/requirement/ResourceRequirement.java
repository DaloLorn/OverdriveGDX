package com.ftloverdrive.model.incident.requirement;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.model.incident.PlotBranchCriteria;
import com.ftloverdrive.model.incident.PlotBranchRequirement;
import com.ftloverdrive.model.incident.PlotBranchCriteria.CriteriaResult;
import com.ftloverdrive.model.ship.ShipModel;


public class ResourceRequirement implements PlotBranchRequirement {

	private String resourceId = null;
	private int quantity = -1;


	protected ResourceRequirement() {
	}
	
	/**
	 * 
	 * @param resourceId
	 *            Id of the required resource
	 * @param quantity
	 *            the required amount
	 */
	public ResourceRequirement( String resourceId, int quantity ) {
		this.resourceId = resourceId;
		this.quantity = quantity;
	}

	@Override
	public CriteriaResult evaluate( OverdriveContext context ) {
		int gameRefId = context.getGameModelRefId();
		GameModel game = context.getReferenceManager().getObject( gameRefId, GameModel.class );
		int shipRefId = game.getPlayerShip( context.getNetManager().getLocalPlayerRefId() );
		ShipModel ship = context.getReferenceManager().getObject( shipRefId, ShipModel.class );

		int scrap = ship.getProperties().getInt( resourceId );
		if ( scrap < quantity )
			return CriteriaResult.VISIBLE_UNAVAILABLE;
		else
			return CriteriaResult.NORMAL;
	}
}
