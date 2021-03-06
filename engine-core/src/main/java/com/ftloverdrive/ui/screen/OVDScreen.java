package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.Screen;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.script.OVDScriptManager;


public interface OVDScreen extends Screen {

	public OVDStageManager getStageManager();

	public OVDEventManager getEventManager();

	public OVDScriptManager getScriptManager();

	/** Returns the entire screen's current width. */
	public int getScreenWidth();

	/** Returns the entire screen's current height. */
	public int getScreenHeight();
}
