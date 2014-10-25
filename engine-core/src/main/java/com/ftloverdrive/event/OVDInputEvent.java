package com.ftloverdrive.event;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

/**
 * OVD wrapper for libgdx's InputEvent
 *
 */
public class OVDInputEvent extends AbstractOVDEvent implements Poolable {
	
	private InputEvent event;

	/**
	 * This constructor must take no args, and only set up variables.
	 * Call init() afterward, as if that were the normal constructor.
	 *
	 * @see com.badlogic.gdx.utils.ReflectionPool
	 */
	public OVDInputEvent() {
		super();
	}

	public InputEvent getInputEvent() {
		return event;
	}

	/**
	 * Resets this object for Pool reuse, setting all variables to defaults.
	 * This is automatically called by the Pool's free() method.
	 *
	 * @see com.badlogic.gdx.utils.Pool
	 * @see com.badlogic.gdx.utils.Pool.Poolable
	 */
	@Override
	public void reset() {
		sourceRefId = -1;
		cancelled = false;
		if ( event != null )
			Pools.get( InputEvent.class ).free( event );
		event = null;
	}

	/**
	 * Pseudo constructor for new and reused objects.
	 * This is capable of accepting args to set initial values.
	 *
	 * Always call this after obtaining a reused object from a Pool.
	 * Subclasses overriding this must call super.init();
	 */
	public void init(InputEvent event) {
		this.event = event;
	}
}
