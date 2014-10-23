package com.ftloverdrive.model.status;

import com.ftloverdrive.model.ship.Coordinatable;

/**
 * A common interface for effects that affect rooms (and by extension, the system
 * or crew located therein) -- for example fire, breach, stun, ion, power limit/div/set
 *
 * An effect can optionally have a timed life (eg. ion damage), or specify an
 * action that crew members can execute over the status (eg. extinguish fire,
 * repair breach)
 */
public interface StatusModel extends Coordinatable {

	// TODO: Come up with API that is generic enough to accomodate all usage cases...

	public void periodicEffect(); // or differentiate into PeriodicStatus subclass?

	public void applyEffect(); // ?

	public void removeEffect(); // ?
}
