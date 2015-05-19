package com.ftloverdrive.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.io.OVDSkin;


public class OVDTooltip extends ShaderLabel implements Disposable {

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/tooltip.json";

	private static final ShaderLabelStyle defaultStyle = new ShaderLabelStyle( new BitmapFont() );

	private OVDTooltipListener listener;
	private AssetManager assetManager;

	private final int gapX;
	private final int gapY;
	private final int maxWidth;


	public OVDTooltip( OverdriveContext context ) {
		this( context, "" );
	}

	public OVDTooltip( OverdriveContext context, String text ) {
		super( text, defaultStyle );

		assetManager = context.getAssetManager();
		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );
		ShaderLabelStyle style = skin.get( "style", ShaderLabelStyle.class );
		setStyle( style );

		listener = new OVDTooltipListener( this );
		listener.setOffset( skin.getInt( "offset-x" ), skin.getInt( "offset-y" ) );
		listener.setShowDelay( skin.getFloat( "show-delay" ) );
		listener.setHideDelay( skin.getFloat( "hide-delay" ) );
		listener.setFollowCursor( true );

		gapX = skin.getInt( "gap-x" );
		gapY = skin.getInt( "gap-y" );
		maxWidth = skin.getInt( "max-width" );

		style.background.setLeftWidth( gapX );
		style.background.setRightWidth( gapX );
		style.background.setTopHeight( gapY );
		style.background.setBottomHeight( gapY );

		setVisible( false );
		setWrap( true );

		setText( text );
	}

	@Override
	public void setText( CharSequence text ) {
		super.setText( text );
		TextBounds bounds = getStyle().font.getWrappedBounds( text, maxWidth );
		setSize( bounds.width + 2 * gapX, bounds.height + 2 * gapY );
	}

	public void addTo( Actor recipient ) {
		recipient.addListener( listener );
	}

	public void removeFrom( Actor recipient ) {
		recipient.removeListener( listener );
	}

	@Override
	public void dispose() {
		if ( getStage() != null ) {
			getStage().getRoot().removeActor( this );
		}
		assetManager.unload( SKIN_PATH );
	}
}
