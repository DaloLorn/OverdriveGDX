package com.ftloverdrive.event.local;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ftloverdrive.event.AbstractLocalEvent;

public class LocalPlayerInputEvent extends AbstractLocalEvent {

	protected InputEvent event;


	public LocalPlayerInputEvent() {
		super();
	}

	public void init( InputEvent e ) {
		event = e;
	}

	/** {@link InputEvent#getButton()} */
	public int getButton() {
		return event.getButton();
	}

	/** {@link InputEvent#getCharacter()} */
	public char getCharacter() {
		return event.getCharacter();
	}

	/** {@link InputEvent#getKeyCode()} */
	public int getKeyCode() {
		return event.getKeyCode();
	}

	/** {@link InputEvent#getPointer()} */
	public int getPointer() {
		return event.getPointer();
	}

	/** {@link InputEvent#getScrollAmount()} */
	public int getScrollAmount() {
		return event.getScrollAmount();
	}

	/** {@link InputEvent#getStage()} */
	public Stage getStage() {
		return event.getStage();
	}

	/** {@link InputEvent#getStageX()} */
	public float getStageX() {
		return event.getStageX();
	}

	/** {@link InputEvent#getStageY()} */
	public float getStageY() {
		return event.getStageY();
	}

	/** {@link InputEvent#getTarget()} */
	public Actor getTarget() {
		return event.getTarget();
	}

	/** {@link InputEvent#getType()} */
	public Type getType() {
		return event.getType();
	}

	@Override
	public void reset() {
		super.reset();
		event = null;
	}
}
