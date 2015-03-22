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
	 * Returns spoiler text for what will happen.
	 *
	 * In the original game this was a parenthetical appended to choices
	 * when hidden="false".
	 */
	public void placeConsequenceActor( ConsequenceBox conseqBox );


	public void execute( OverdriveContext context );
}
