package com.ftloverdrive.event.local;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.ftloverdrive.event.AbstractLocalEvent;

public class LocalActorClickedEvent extends AbstractLocalEvent implements Poolable {
	
	protected Actor targetActor = null;
	protected int button = -1;
	protected int pointer = -1;
	//protected boolean release = false; // differentiate between click / release

	public LocalActorClickedEvent() {
		super();
	}

	public void init( InputEvent e ) {
		if ( e.getType() == Type.touchDown || e.getType() == Type.touchUp ) {
			targetActor = e.getTarget();
			button = e.getButton();
			pointer = e.getPointer();
			//release = e.getType() == Type.touchUp;
		} else {
			throw new IllegalArgumentException( "Tried to construct LocalActorClickedEvent from non-click event." );
		}
	}

	public Actor getTarget() {
		return targetActor;
	}

	public int getButton() {
		return button;
	}
	
	public int getPointer() {
		return pointer;
	}

	@Override
	public void reset() {
		super.reset();
		
		targetActor = null;
		button = -1;
		pointer = -1;
		//release = false;
	}
}
