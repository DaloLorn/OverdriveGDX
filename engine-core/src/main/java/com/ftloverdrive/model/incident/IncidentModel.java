package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.OVDModel;


/**
 * What the original game referred to as an event.
 */
public interface IncidentModel extends OVDModel {

	/** Returns a unique identifier for this Incident. */
	public String getIncidentId();


	/** Triggers Consequences and notifies the UI to represent this model, with a Window. */
	public void execute( OverdriveContext context );


	/**
	 * Returns text to show when triggered, or null.
	 *
	 * TODO: Allow for context-aware text (e.g., inserting crew names).
	 */
	public String getText();

	public void setText( String text );

	/** Returns a list of Consequences, all of which trigger at the start of this Incident. */
	public Integer[] consequenceRefIds();

	public void addConsequence( int cseqRefId );

	// public void removeConsequence( int cseqRefId );


	/** Returns choices which may lead to further Incidents. */
	public Integer[] branchRefIds();

	public void addPlotBranch( int branchRefId );

	// public void removePlotBranch( int branchRefId );
}
