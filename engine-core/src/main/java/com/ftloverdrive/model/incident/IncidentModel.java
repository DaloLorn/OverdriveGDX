package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.OVDModel;


/**
 * What the original game referred to as an event.
 * 
 * Overview of the entire incident system, as currently implemented:
 * 
 * IncidentModels are created by IncidentBlueprints as a response to IncidentSelectionEvent.
 * When they are created, Incidents have no plot branches. Those are created and added to
 * the incident at the last moment, when it is selected.
 * When a plot branch is selected in the IncidentDialog, it enqueues IncidentSelectionEvent,
 * starting the process all over again. The old incident model (and its members) are freed and
 * forgotten by the reference manager.
 * 
 * This allows to only create the necessary incidents, which makes event loops possible.
 * Of course, we could only create a single incident -- the one currently being displayed -- but
 * then we would be unable to show the outcomes of random Consequences (ie. random hull damage)
 * in plot branch spoilers.
 */
public interface IncidentModel extends OVDModel {

	/** Returns the identifier of this Incident's blueprint. */
	public String getIncidentId();

	/** Triggers Consequences and notifies the UI to represent this model, with an IncidentDialog. */
	public void execute( OverdriveContext context );

	/**
	 * Returns text to show when triggered, or null.
	 *
	 * TODO: Allow for context-aware text (e.g., inserting crew names).
	 */
	public String getText();

	public void setText( String text );

	/** Returns a list of Consequences, all of which trigger at the start of this Incident. */
	public int[] consequenceRefIds();

	public void addConsequence( int cseqRefId );

	/** Returns choices which may lead to further Incidents. */
	public int[] branchRefIds();

	public void addPlotBranch( int branchRefId );
}
