package com.ftloverdrive.model.incident;

import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.ui.incident.ConsequenceBox;


/**
 * Something that happens as an Incident triggers.
 *
 * TODO: Some possible default classes...
 *
 *   EncounterConsequence (ship event)
 *   RandomRewardConsequence
 *   AugmentConsequence
 *   WeaponConsequence
 *   DroneConsequence
 *   CrewConsequence
 *   DamageConsequence
 *   BoardersConsequence
 *   MapConsequence (revealMap)
 *   PursuitConsequence
 *   QuestConsequence
 *   ItemModificationConsequence
 *   SecretSectorConsequence
 *   StatusConsequence
 */
public interface Consequence {

	/**
	 * Places the actor representing this consequence in the ConsequenceBox passed in argument.
	 *
	 * In the original game, this was a consequence box displayed next to choices when hidden="false".
	 */
	public void placeConsequenceActor( OverdriveContext context, ConsequenceBox conseqBox );

	/**
	 * Allows the Consequence to optionally add requirements to the PlotBranch, eg. to add a
	 * scrap requirement to prevent the player from purchasing something via an incident
	 * without having the means to do so.
	 * 
	 * @param branch
	 *            the branch to which the requirements will be added
	 */
	public void addRequirements( PlotBranch branch );

	/**
	 * If true, then this consequence's spoiler actors should show up in the spoiler consequence box
	 * displayed next to plot branch when hidden="false".
	 */
	public boolean isSpoilable();

	public void execute( OverdriveContext context );
}
