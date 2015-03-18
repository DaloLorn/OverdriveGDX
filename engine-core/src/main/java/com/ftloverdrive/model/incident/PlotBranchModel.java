package com.ftloverdrive.model.incident;

import com.ftloverdrive.blueprint.incident.ConsequenceBlueprint;
import com.ftloverdrive.blueprint.incident.IncidentBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.AbstractOVDModel;


public class PlotBranchModel extends AbstractOVDModel implements PlotBranch {

	private String incidentId = null;
	private String choiceText = null;
	private boolean spoilerVisible = true;


	public PlotBranchModel() {
		setText( "Continue..." );
	}

	public PlotBranchModel( String incidentId ) {
		setIncidentId( incidentId );
	}

	public PlotBranchModel( String incidentId, String text ) {
		setIncidentId( incidentId );
		setText( text );
	}

	public void setText( String text ) {
		choiceText = text;
	}

	@Override
	public String getText() {
		return choiceText;
	}

	@Override
	public String getSpoilerText( OverdriveContext context ) {
		if ( incidentId == null ) {
			return null;
		}
		else {
			StringBuilder buf = new StringBuilder();
			IncidentBlueprint incidentBlueprint = (IncidentBlueprint)context.getBlueprintManager().getBlueprint( incidentId );
			for ( ConsequenceBlueprint conseq : incidentBlueprint.getConsequences() ) {
				buf.append( conseq.getSpoilerText() );
			}

			return buf.length() == 0 ? null : buf.toString();
		}
	}

	@Override
	public void setSpoilerVisible( boolean b ) {
		spoilerVisible = b;
	}

	@Override
	public boolean isSpoilerVisible() {
		return spoilerVisible;
	}

	public void setIncidentId( String id ) {
		incidentId = id;
	}

	@Override
	public String getIncidentId() {
		return incidentId;
	}
}
