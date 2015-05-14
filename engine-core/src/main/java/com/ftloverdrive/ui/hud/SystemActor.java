package com.ftloverdrive.ui.hud;

import java.util.Map;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.system.SystemPropertyEvent;
import com.ftloverdrive.event.system.SystemPropertyListener;
import com.ftloverdrive.model.system.SystemModel;
import com.ftloverdrive.ui.ModelActor;
import com.ftloverdrive.util.OVDConstants;


/**
 * Actor representing a system in the player's HUD.
 * 
 */
public class SystemActor extends ModelActor implements SystemPropertyListener {

	protected final NinePatch barEmpty;
	protected final NinePatch barDisabled;
	protected final NinePatch barFull;
	protected final NinePatchDrawable barDrawable;

	protected final Color colorHasPower;
	protected final Color colorNoPower;
	protected final Color colorIon;
	protected final Color colorDisabled;
	protected final Color colorSelfPower;

	protected final int sysBarWidth;
	protected final int sysBarHeight;

	private final Image icon;

	private float offsetX = 0;
	private float offsetY = 0;

	private int powerCap = 0;
	private int powerCurrent = 0;
	private int powerIncrement = 0;

	private boolean hasBeenReset = true;


	public SystemActor( OverdriveContext context, Map<String, Object> resourceMap ) {
		super( context );

		colorHasPower = (Color)resourceMap.get( "power-available" );
		colorNoPower = (Color)resourceMap.get( "power-none" );
		colorIon = (Color)resourceMap.get( "power-ion" );
		colorDisabled = (Color)resourceMap.get( "power-disabled" );
		colorSelfPower = (Color)resourceMap.get( "power-self" );
		sysBarWidth = (Integer)resourceMap.get( "system-bar-width" );
		sysBarHeight = (Integer)resourceMap.get( "system-bar-height" );

		barDrawable = (NinePatchDrawable)resourceMap.get( "bar-drawable" );
		barEmpty = (NinePatch)resourceMap.get( "bar-empty" );
		barFull = (NinePatch)resourceMap.get( "bar-full" );
		barDisabled = (NinePatch)resourceMap.get( "bar-disabled" );

		icon = new Image();
		addActor( icon );

		setOffsets( 0, 0 );
		setSize( 0, 0 );
	}


	public void drawChildren( Batch batch, float parentAlpha ) {
		super.drawChildren( batch, parentAlpha );

		// Draw power bars
		// TODO: Figure out how to align this stuff without using magic numbers.
		float x = 24;
		float y = 50;
		barDrawable.setPatch( barFull );
		for ( int i = 0; i < powerCap; ++i ) {
			if ( i >= powerCurrent && barDrawable.getPatch() != barEmpty )
				barDrawable.setPatch( barEmpty );
			barDrawable.draw( batch, x, y, sysBarWidth, sysBarHeight );
			y += sysBarHeight + 2;
			if ( powerIncrement > 1 && i % powerIncrement == powerIncrement - 1 )
				y += 4;
		}
	}

	public void setOffsetX( float x ) {
		offsetX = x;
	}

	public void setOffsetY( float y ) {
		offsetY = y;
	}

	public void setOffsets( float x, float y ) {
		offsetX = x;
		offsetY = y;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	@Override
	protected void updateInfo( OverdriveContext context ) {
		if ( modelRefId == -1 ) {
			offsetX = -1;
			offsetY = -1;
			powerCap = -1;
			powerCurrent = -1;
			powerIncrement = -1;

			icon.removeListener( icon.getListeners().get( 0 ) );
			hasBeenReset = true;
		}
		else {
			SystemModel systemModel = context.getReferenceManager().getObject( modelRefId, SystemModel.class );
			if ( hasBeenReset )
				updateIcon( context, systemModel );

			setSize( icon.getWidth() * 0.75f, icon.getHeight() );

			powerCap = systemModel.getPowerCapacity();
			powerCurrent = systemModel.getCurrentPower();
			powerIncrement = systemModel.getPowerIncrement();

			hasBeenReset = false;
		}
	}

	private void updateIcon( final OverdriveContext context, final SystemModel model ) {
		TextureAtlas iconAtlas = context.getAssetManager().get( OVDConstants.ICONS_ATLAS, TextureAtlas.class );
		TextureRegion iconRegion = iconAtlas.findRegion( model.getIconName() + "-green1" );
		icon.setDrawable( new TextureRegionDrawable( iconRegion ) );
		icon.setSize( icon.getPrefWidth(), icon.getPrefHeight() );

		icon.addListener( new ClickListener() {
			@Override
			public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
				if ( button == Buttons.LEFT ) {
					model.setCurrentPower( context, model.getCurrentPower() + model.getPowerIncrement() );
					return true;
				}
				else if ( button == Buttons.RIGHT ) {
					model.setCurrentPower( context, model.getCurrentPower() - model.getPowerIncrement() );
					return true;
				}
				return false;
			}
		} );
	}


	@Override
	public void systemPropertyChanged( OverdriveContext context, SystemPropertyEvent e ) {
		if ( e.getModelRefId() != modelRefId ) return;

		updateInfo( context );
	}
}
