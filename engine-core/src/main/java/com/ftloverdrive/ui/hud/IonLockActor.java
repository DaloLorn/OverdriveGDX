package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Disposable;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.system.SystemModel;
import com.ftloverdrive.ui.ModelActor;
import com.ftloverdrive.ui.RadialSprite;
import com.ftloverdrive.util.OVDConstants;


/**
 * This actor represents the ion damage a system has taken, as well as
 * the remaining duration of the ion lock.
 * 
 * @author kartoFlane
 */
public class IonLockActor extends ModelActor
		implements Disposable {

	public static final String SKIN_PATH = "overdrive-assets/skins/player-hud/reactor-ui/system-ui.json";

	protected final RadialSprite octaTimer;
	protected final Sprite[] octaBase = new Sprite[9];
	protected final RadialSprite ringTimer;
	protected final Sprite[] ringBase = new Sprite[9];

	protected final float clipStep;

	private final AssetManager assetManager;

	protected int powerIoned = -1;
	protected float clipAngle = 0;
	protected boolean ringShaped = true;


	public IonLockActor( OverdriveContext context ) {
		super( context );
		assetManager = context.getAssetManager();

		assetManager.load( OVDConstants.ION_LOCK_ATLAS, TextureAtlas.class );
		assetManager.load( SKIN_PATH, OVDSkin.class );
		assetManager.finishLoading();

		OVDSkin skin = assetManager.get( SKIN_PATH, OVDSkin.class );
		TextureAtlas lockAtlas = assetManager.get( OVDConstants.ION_LOCK_ATLAS, TextureAtlas.class );

		TextureRegion region = lockAtlas.findRegion( "s-octa-timer" );
		octaTimer = new RadialSprite( region );
		octaTimer.setOrigin( region.getRegionWidth() * 0.5f, region.getRegionHeight() * 0.5f );
		octaTimer.setScale( -1, -1 );

		region = lockAtlas.findRegion( "s-ring-timer" );
		ringTimer = new RadialSprite( region );
		ringTimer.setOrigin( region.getRegionWidth() * 0.5f, region.getRegionHeight() * 0.5f );
		ringTimer.setScale( -1, -1 );

		for ( int i = 0; i < octaBase.length; ++i ) {
			octaBase[i] = lockAtlas.createSprite( "s-octa-" + ( i + 1 ) + "-base" );
			ringBase[i] = lockAtlas.createSprite( "s-ring-" + ( i + 1 ) + "-base" );
		}

		clipStep = skin.getInt( "ion-lock-step-angle" );

		setSize( region.getRegionWidth(), region.getRegionHeight() );
		setTouchable( Touchable.disabled );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		super.draw( batch, parentAlpha );

		if ( powerIoned > 0 ) {
			if ( ringShaped ) {
				ringBase[powerIoned - 1].setPosition( getX(), getY() );
				ringBase[powerIoned - 1].draw( batch, parentAlpha );
				ringTimer.draw( batch, getX(), getY(), clipAngle );
			}
			else {
				octaBase[powerIoned - 1].setPosition( getX(), getY() );
				octaBase[powerIoned - 1].draw( batch, parentAlpha );
				octaTimer.draw( batch, getX(), getY(), clipAngle );
			}
		}
	}

	@Override
	protected void updateInfo( OverdriveContext context ) {
		if ( modelRefId == -1 ) {
			powerIoned = -1;
		}
		else {
			SystemModel system = context.getReferenceManager().getObject( modelRefId, SystemModel.class );
			powerIoned = system.getProperties().getInt( OVDConstants.POWER_IONED );
			powerIoned = Math.min( powerIoned, octaBase.length );
			ringShaped = !system.isSelfPowered();

			float ionFraction = system.getProperties().getInt( OVDConstants.ION_FRACTION );
			float ionFractionMax = system.getProperties().getInt( OVDConstants.ION_FRACTION_MAX );
			clipAngle = 360 - ( ionFraction / ionFractionMax ) * 360;
			clipAngle -= clipAngle % clipStep;
		}
	}

	@Override
	public void dispose() {
		assetManager.unload( OVDConstants.ION_LOCK_ATLAS );
		assetManager.unload( SKIN_PATH );
	}
}
