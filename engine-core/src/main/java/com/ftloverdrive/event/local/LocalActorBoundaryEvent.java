package com.ftloverdrive.event.local;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractLocalEvent;

public class LocalActorBoundaryEvent extends AbstractLocalEvent implements Poolable {
	
	protected Actor targetActor = null;
	protected Actor relatedActor = null;
	protected int pointer = -1;
	protected float x = -1;
	protected float y = -1;
	protected boolean enter = false;

	public LocalActorBoundaryEvent() {
		super();
	}

	public void init( InputEvent e ) {
		if ( e.getType() == Type.enter || e.getType() == Type.exit ) {
			targetActor = e.getTarget();
			relatedActor = e.getRelatedActor();
			pointer = e.getPointer();
			x = e.getStageX();
			y = e.getStageY();
			enter = e.getType() == Type.enter;
		} else {
			throw new IllegalArgumentException( "Tried to construct LocalActorBoundaryEvent from non- enter/exit event." );
		}
	}

	public Actor getTarget() {
		return targetActor;
	}

	public Actor getRelatedActor() {
		return relatedActor;
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

	public boolean isEnter() {
		return enter;
	}

	public boolean isExit() {
		return !enter;
	}

	@Override
	public void reset() {
		super.reset();
		
		targetActor = null;
		relatedActor = null;
		pointer = -1;
		x = -1;
		y = -1;
	}
}
