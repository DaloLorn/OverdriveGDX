package com.ftloverdrive.model.incident;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Keys;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.AbstractOVDModel;


/**
 * What the original game referred to as an event.
 *
 * This is a WIP attempt to retrofit code to use reference ids and OVDEvents.
 */
public abstract class AbstractIncidentModel2 extends AbstractOVDModel implements IncidentModel2 {

	protected List<Consequence> consequenceList = new ArrayList<Consequence>( 2 );
	protected List<PlotBranch> branchList = new ArrayList<PlotBranch>( 3 );

	protected IntMap<Consequence> cseqRefIdMap;
	protected IntMap<PlotBranch> branchRefIdMap;


	public AbstractIncidentModel2() {
		super();
	}


	@Override
	public abstract String getIncidentId();


	@Override
	public void execute( OverdriveContext context ) {
		/*
		 * for ( Consequence cseq : getConsequences() ) {
		 * cseq.execute( context );
		 * }
		 */

		// TODO: Whatever it takes for the UI to notice and show a window.
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public Keys consequenceRefIds() {
		return cseqRefIdMap.keys();
	}

	public void addConsequence( int cseqRefId, Consequence cseq ) {
		// TODO ?
	}

	@Override
	public void removeConsequence( int cseqRefId ) {
		// TODO Auto-generated method stub

	}


	@Override
	public Keys branchRefIds() {
		return branchRefIdMap.keys();
	}

	@Override
	public void addPlotBranch( int branchRefId ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePlotBranch( int branchRefId ) {
		// TODO Auto-generated method stub

	}


	@Override
	public void addConsequence( int cseqRefId ) {
		// TODO Auto-generated method stub

	}
}
