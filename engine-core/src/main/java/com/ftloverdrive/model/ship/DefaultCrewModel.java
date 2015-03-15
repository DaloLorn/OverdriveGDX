package com.ftloverdrive.model.ship;

import java.util.Collection;
import java.util.Stack;

import com.ftloverdrive.io.AnimSpec;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.util.OVDConstants;


public class DefaultCrewModel implements CrewModel {

	protected NamedProperties crewProperties;

	protected AnimSpec walkFrontAnimSpec;
	protected AnimSpec walkBackAnimSpec;
	protected AnimSpec walkLeftAnimSpec;
	protected AnimSpec walkRightAnimSpec;
	protected AnimSpec idleAnimSpec;

	protected ShipCoordinate ambulationGoal;
	protected ShipCoordinate nextWaypoint;
	protected Stack<ShipCoordinate> path;


	public DefaultCrewModel() {
		crewProperties = new NamedProperties();

		idleAnimSpec = new AnimSpec( OVDConstants.PEOPLE_ATLAS, "human-base", 35, 35, 1, 0, 0, 1.2f );
		walkFrontAnimSpec = new AnimSpec( OVDConstants.PEOPLE_ATLAS, "human-base", 35, 35, 4, 0, 0, 1.2f );
		walkBackAnimSpec = new AnimSpec( OVDConstants.PEOPLE_ATLAS, "human-base", 35, 35, 4, 0, 1, 1.2f );
		walkLeftAnimSpec = new AnimSpec( OVDConstants.PEOPLE_ATLAS, "human-base", 35, 35, 4, 4, 1, 1.2f );
		walkRightAnimSpec = new AnimSpec( OVDConstants.PEOPLE_ATLAS, "human-base", 35, 35, 4, 4, 0, 1.2f );
	}

	@Override
	public float getAmbulationSpeed() {
		return 70.0f;
	}

	@Override
	public void setAmbulationGoal( ShipCoordinate coord ) {
		ambulationGoal = coord;
	}

	public void setAmbulationPath( Collection<ShipCoordinate> path ) {
		this.path = (Stack<ShipCoordinate>)path;
	}

	@Override
	public ShipCoordinate getAmbulationGoal() {
		return ambulationGoal;
	}

	@Override
	public boolean isAmbulating() {
		return path != null;
	}

	@Override
	public ShipCoordinate getNextWaypoint() {
		if ( path.size() == 0 )
			return null;
		else
			return path.pop();
	}

	@Override
	public NamedProperties getProperties() {
		return crewProperties;
	}

	@Override
	public AnimSpec getIdleAnimSpec() {
		return idleAnimSpec;
	}

	@Override
	public AnimSpec getWalkFrontAnimSpec() {
		return walkFrontAnimSpec;
	}

	@Override
	public AnimSpec getWalkBackAnimSpec() {
		return walkBackAnimSpec;
	}

	@Override
	public AnimSpec getWalkLeftAnimSpec() {
		return walkLeftAnimSpec;
	}

	@Override
	public AnimSpec getWalkRightAnimSpec() {
		return walkRightAnimSpec;
	}
}
