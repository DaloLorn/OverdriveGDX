package com.ftloverdrive.event.engine;

import com.ftloverdrive.event.AbstractOVDEvent;


/**
 * A signal event telling the game to release the Model associated with the ref id.
 * If the model also implements the Disposable interface, then the dispose() method
 * will be called.
 */
public class ModelDestructionEvent extends AbstractOVDEvent {

	// TODO: Add info about the model's exact type, should listeners want to react to the event?
	protected int[] refIds;


	public ModelDestructionEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param modelRefIds
	 *            reference ids of the models to be destroyed
	 */
	public void init( int... modelRefIds ) {
		refIds = new int[modelRefIds.length];
		for ( int i = 0; i < refIds.length; ++i )
			refIds[i] = modelRefIds[i];
	}

	/**
	 * RefId of the destroyed model.
	 */
	public int[] getModelRefIds() {
		return refIds;
	}

	@Override
	public void reset() {
		refIds = null;
	}
}
