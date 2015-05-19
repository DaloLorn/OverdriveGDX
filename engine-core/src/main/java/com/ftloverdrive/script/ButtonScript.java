package com.ftloverdrive.script;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.ftloverdrive.core.OverdriveContext;


/**
 * An interface for scripts that are attached to buttons, and should execute some code
 * when the button is updated or clicked.
 */
public interface ButtonScript {

	/**
	 * Called when the button is created/updated.
	 * 
	 * @param context
	 * @param button
	 *            the button to which this script is attached
	 * @param modelRefId
	 *            the refId of the model to which this script is attached
	 */
	public void onUpdate( OverdriveContext context, Button button, int modelRefId );

	/**
	 * Called when the button is clicked.
	 * 
	 * @param context
	 * @param button
	 *            the button to which this script is attached. This is the button that was clicked.
	 */
	public void onClick( OverdriveContext context, Button button );
}
