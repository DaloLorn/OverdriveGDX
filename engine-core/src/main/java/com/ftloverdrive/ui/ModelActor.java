package com.ftloverdrive.ui;

import com.ftloverdrive.core.OverdriveContext;


/**
 * An actor that represents a Model it is associated with.
 *
 */
public abstract class ModelActor extends LocalActor {

	protected int modelRefId = -1;


	public ModelActor( OverdriveContext context ) {
		super( context );
	}

	/**
	 * Updates the actor to match the model it is associated with, and accurately represent it.
	 *
	 * TODO: Keep context in argument; will be needed once context is no longer passed in constructor.
	 */
	protected abstract void updateInfo( OverdriveContext context );

	public void setModelRefId( int modelRefId ) {
		this.modelRefId = modelRefId;
		updateInfo( context );
	}

	public int getModelRefId() {
		return modelRefId;
	}
}
