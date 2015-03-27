package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.PlayerModel;
import com.ftloverdrive.model.incident.PlotBranchCriteria.CriteriaResult;


public class RequirementResource implements PlotBranchRequirement {

	private String resourceId = null;
	private int quantity = -1;


	/**
	 * 
	 * @param resourceId
	 *            Id of the required resource
	 * @param quantity
	 *            the required amount
	 */
	public RequirementResource( String resourceId, int quantity ) {
		this.resourceId = resourceId;
		this.quantity = quantity;
	}

	@Override
	public CriteriaResult evaluate( OverdriveContext context ) {
		int playerRefId = context.getNetManager().getLocalPlayerRefId();
		PlayerModel playerModel = context.getReferenceManager().getObject( playerRefId, PlayerModel.class );
		int playerScrap = playerModel.getProperties().getInt( resourceId );

		if ( playerScrap < quantity )
			return CriteriaResult.VISIBLE_UNAVAILABLE;
		else
			return CriteriaResult.NORMAL;
	}
}
