package com.ftloverdrive.event.local;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractLocalEvent;

public class LocalPointerMoveEvent extends AbstractLocalEvent implements Poolable {
	
	protected Actor targetActor = null;
	protected int pointer = -1;
	protected float x = -1;
	protected float y = -1;

	public LocalPointerMoveEvent() {
		super();
	}

	public void init( InputEvent e ) {
		if ( e.getType() == Type.mouseMoved || e.getType() == Type.touchDragged ) {
			targetActor = e.getTarget();
			pointer = e.getPointer();
			x = e.getStageX();
			y = e.getStageY();
		} else {
			throw new IllegalArgumentException( "Tried to construct LocalPointerMoveEvent from non- move/drag event." );
		}
	}

	public Actor getTarget() {
		return targetActor;
	}

	public int getPointer() {
		return pointer;
	}

	public float getStageX() {
		return x;
	}

	public float getStageY() {
		return y;
	}

	@Override
	public void reset() {
		super.reset();
		
		targetActor = null;
		pointer = -1;
		x = -1;
		y = -1;
	}
}
