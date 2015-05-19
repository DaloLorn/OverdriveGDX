package com.ftloverdrive.io;

public class ButtonSpec {

	protected String skinPath;
	protected String scriptPath;


	public ButtonSpec() {
	}

	public ButtonSpec( String skin, String script ) {
		skinPath = skin;
		scriptPath = script;
	}

	public void setSkinPath( String path ) {
		skinPath = path;
	}

	public void setScriptPath( String path ) {
		scriptPath = path;
	}

	/**
	 * Path to a json skin file describing the button's appearance.
	 */
	public String getSkinPath() {
		return skinPath;
	}

	/**
	 * Path to a script implementing the Runnable interface.
	 */
	public String getScriptPath() {
		return scriptPath;
	}
}
