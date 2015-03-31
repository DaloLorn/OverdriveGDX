package com.ftloverdrive.event.engine;

import com.ftloverdrive.event.AbstractOVDEvent;


/**
 * A signal event telling the game to release the Model associated with the ref id.
 * If the model also implements the Disposable interface, then the dispose() method
 * will be called.
 */
public class ModelDestructionEvent extends AbstractOVDEvent {

	// TODO: Add info about the model's exact type, should listeners want to react to the event?
	protected int modelRefId = -1;


	public ModelDestructionEvent() {
	}

	/**
	 * Pseudo-constructor.
	 * 
	 * @param modelRefId
	 *            reference id of the model to be destroyed
	 */
	public void init( int modelRefId ) {
		this.modelRefId = modelRefId;
	}

	/**
	 * RefId of the destroyed model.
	 */
	public int getModelRefId() {
		return modelRefId;
	}

	@Override
	public void reset() {
		modelRefId = -1;
	}
}
