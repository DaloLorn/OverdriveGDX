package com.ftloverdrive.ui;

import com.ftloverdrive.ui.screen.BaseScreen;


/**
 * TODO: Have an interface for HUDs, so that it is possible to swap them in & out?
 * Would be useful for changing between PC and mobile displays for various resolutions
 *
 */
public interface OverdriveHUD {

	// TODO: Stub.

	public void create( BaseScreen screen );

	public void render( float delta );

	public void resize( int width, int height );

	public void dispose();
}
