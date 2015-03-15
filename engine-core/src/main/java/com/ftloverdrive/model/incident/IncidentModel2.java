package com.ftloverdrive.model.incident;

import com.badlogic.gdx.utils.IntMap;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.OVDModel;


/**
 * What the original game referred to as an event.
 *
 * This is a WIP attempt to retrofit code to use reference ids and OVDEvents.
 *
 * TODO: A DeferredIncidentModel class, which constructs a named Incident
 * at the last minute, probably from an IncidentBlueprint. That would make
 * event loops possible.
 */
public interface IncidentModel2 extends OVDModel {

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


	/** Returns a list of Consequences, all of which trigger at the start of this Incident. */
	public IntMap.Keys consequenceRefIds();

	public void addConsequence( int cseqRefId );

	public void removeConsequence( int cseqRefId );


	/** Returns choices which may lead to further Incidents. */
	public IntMap.Keys branchRefIds();

	public void addPlotBranch( int branchRefId );

	public void removePlotBranch( int branchRefId );
}
