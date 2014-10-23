package com.ftloverdrive.model.system;

import com.ftloverdrive.model.Powerable;


public interface SystemModel extends Powerable {
	/*
	 * KF:
	 * - multiple systems of the same type (eg. pilot) assigned to different rooms,
	 *   whichever is manned gives the bonus, but destroyed/repaired separately?
	 *   (intuitive UI indicator of such multiple systems becomes tricky)
	 * - multiple stations per room/system --> stacking manning bonus?
	 * - multiple systems of different types in a single room? stations assigned to
	 *   system, not room?
	 * 
	 * example XML data structure:
	 *  <pilot room="1,2"> // assigned to rooms with ids 1 and 2, or multiple <pilot> tags
	 *    <room id="1">
	 *      <station slot=... dir=... /> // or x,y
	 *      <roomDecor path=... />
	 *      <icon pos="auto" /> // pos="auto" --> align the system icon at the center of the room
	 *      <icon x="0" y="0"/> // allow manual alignment of the icon for oddly shaped rooms
	 *      // assume pos="manual" by default if pos arg is missing, but pos="auto" if <icon> tag is missing
	 *    </room>
	 *    <room id="2">
	 *      ....
	 *    </room>
	 *  </pilot>
	 */

	//TODO: Getters for upgrade costs/descriptions/limits.
}
