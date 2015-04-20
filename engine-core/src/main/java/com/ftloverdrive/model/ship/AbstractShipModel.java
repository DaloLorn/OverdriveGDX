package com.ftloverdrive.model.ship;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.PropertyEvent;
import com.ftloverdrive.event.ship.ShipPropertyEvent;
import com.ftloverdrive.io.ImageSpec;
import com.ftloverdrive.model.AbstractOVDModel;
import com.ftloverdrive.model.NamedProperties;
import com.ftloverdrive.util.OVDConstants;


public class AbstractShipModel extends AbstractOVDModel implements ShipModel {

	protected NamedProperties shipProperties = new NamedProperties();

	protected ShipLayout shipLayout;

	protected Array<Integer> crewArray;

	protected float shipOffsetX = 0f;
	protected float shipOffsetY = 0f;

	protected float hullOffsetX = 0f;
	protected float hullOffsetY = 0f;
	// TODO hull width/height are not really used for anything in AE anymore,
	// remove them and just use hull image dimensions instead?
	protected float hullWidth = 0f;
	protected float hullHeight = 0f;
	protected float floorOffsetX = 0f;
	protected float floorOffsetY = 0f;
	protected float cloakOffsetX = 0f;
	protected float cloakOffsetY = 0f;

	protected float shieldEllipseOffsetX = 0f;
	protected float shieldEllipseOffsetY = 0f;
	protected float shieldEllipseSemiMajorAxis = 0f;
	protected float shieldEllipseSemiMinorAxis = 0f;

	protected ImageSpec baseImageSpec = null;
	protected ImageSpec cloakImageSpec = null;
	protected ImageSpec floorImageSpec = null;
	protected ImageSpec shieldImageSpec = null;


	public AbstractShipModel() {
		super();
		shipProperties.setInt( OVDConstants.HULL_MAX, 0 );
		shipProperties.setInt( OVDConstants.HULL, 0 );
		shipProperties.setInt( OVDConstants.SHIELD_MAX, 0 );
		shipProperties.setInt( OVDConstants.SHIELD, 0 );
		shipProperties.setInt( OVDConstants.POWER_MAX, 0 );
		shipProperties.setInt( OVDConstants.POWER, 0 );
		shipProperties.setInt( OVDConstants.SCRAP, 0 );
		shipProperties.setInt( OVDConstants.FUEL, 0 );
		shipProperties.setInt( OVDConstants.MISSILES, 0 );
		shipProperties.setInt( OVDConstants.DRONES, 0 );
		shipProperties.setInt( OVDConstants.WEAPON_SLOTS, 0 );
		shipProperties.setInt( OVDConstants.DRONE_SLOTS, 0 );
		shipProperties.setInt( OVDConstants.AUGMENT_SLOTS, 0 );
		shipProperties.setInt( OVDConstants.CREW_SLOTS, 8 );
		shipProperties.setString( OVDConstants.BLUEPRINT_NAME, "DEFAULT" );

		shipLayout = new ShipLayout();
		crewArray = new Array<Integer>( true, shipProperties.getInt( OVDConstants.CREW_SLOTS ) );
	}


	/**
	 * Returns a collection of arbitrarily named values.
	 */
	@Override
	public NamedProperties getProperties() {
		return shipProperties;
	}


	@Override
	public ShipLayout getLayout() {
		return shipLayout;
	}


	@Override
	public void setShipOffset( float x, float y ) {
		shipOffsetX = x;
		shipOffsetY = y;
	}

	@Override
	public float getShipOffsetX() {
		return shipOffsetX;
	}

	@Override
	public float getShipOffsetY() {
		return shipOffsetY;
	}


	@Override
	public void setHullOffset( float x, float y ) {
		hullOffsetX = x;
		hullOffsetY = y;
	}

	@Override
	public float getHullOffsetX() {
		return hullOffsetX;
	}

	@Override
	public float getHullOffsetY() {
		return hullOffsetY;
	}

	@Override
	public void setHullSize( float width, float height ) {
		hullWidth = width;
		hullHeight = height;
	}

	@Override
	public float getHullWidth() {
		return hullWidth;
	}

	@Override
	public float getHullHeight() {
		return hullHeight;
	}

	@Override
	public void setFloorOffset( float x, float y ) {
		floorOffsetX = x;
		floorOffsetY = y;
	}

	@Override
	public float getFloorOffsetX() {
		return floorOffsetX;
	}

	@Override
	public float getFloorOffsetY() {
		return floorOffsetY;
	}

	@Override
	public void setCloakOffset( float x, float y ) {
		cloakOffsetX = x;
		cloakOffsetY = y;
	}

	@Override
	public float getCloakOffsetX() {
		return cloakOffsetX;
	}

	@Override
	public float getCloakOffsetY() {
		return cloakOffsetY;
	}

	@Override
	public void setShieldEllipse( float x, float y, float semiMajorAxis, float semiMinorAxis ) {
		shieldEllipseOffsetX = x;
		shieldEllipseOffsetY = y;
		shieldEllipseSemiMajorAxis = semiMajorAxis;
		shieldEllipseSemiMinorAxis = semiMinorAxis;
	}

	@Override
	public float getShieldEllipseOffsetX() {
		return shieldEllipseOffsetX;
	}

	@Override
	public float getShieldEllipseOffsetY() {
		return shieldEllipseOffsetY;
	}

	@Override
	public float getShieldEllipseSemiMajorAxis() {
		return shieldEllipseSemiMajorAxis;
	}

	@Override
	public float getShieldEllipseSemiMinorAxis() {
		return shieldEllipseSemiMinorAxis;
	}


	@Override
	public void setBaseImageSpec( ImageSpec imageSpec ) {
		baseImageSpec = imageSpec;
	}

	@Override
	public ImageSpec getBaseImageSpec() {
		return baseImageSpec;
	}


	@Override
	public void setCloakImageSpec( ImageSpec imageSpec ) {
		cloakImageSpec = imageSpec;
	}

	@Override
	public ImageSpec getCloakImageSpec() {
		return cloakImageSpec;
	}


	@Override
	public void setFloorImageSpec( ImageSpec imageSpec ) {
		floorImageSpec = imageSpec;
	}

	@Override
	public ImageSpec getFloorImageSpec() {
		return floorImageSpec;
	}


	@Override
	public void setShieldImageSpec( ImageSpec imageSpec ) {
		shieldImageSpec = imageSpec;
	}

	@Override
	public ImageSpec getShieldImageSpec() {
		return shieldImageSpec;
	}


	@Override
	public Integer[] crewRefIds() {
		return crewArray.toArray();
	}

	@Override
	public boolean addCrew( int crewRefId ) {
		if ( crewArray.size >= shipProperties.getInt( OVDConstants.CREW_SLOTS ) ) {
			return false;
		}

		crewArray.add( crewRefId );
		return true;
	}

	@Override
	public void damage( OverdriveContext context, int value ) {
		int selfRefId = context.getReferenceManager().getId( this );

		// TODO: Handle shield piercing & ion damage here, and others...
		// TODO: Implement DamageHandlers that handle damage, so that Models can just reference them?
		int shields = getProperties().getInt( OVDConstants.SHIELD );
		if ( shields > 0 ) {
			if ( shields == getProperties().getInt( OVDConstants.SHIELD_MAX ) ) {
				ShipPropertyEvent event = Pools.get( ShipPropertyEvent.class ).obtain();
				event.init( selfRefId, PropertyEvent.INCREMENT_ACTION, OVDConstants.SHIELD, 1 );
				context.getScreenEventManager().postDelayedEvent( event, 3 );
			}

			ShipPropertyEvent event = Pools.get( ShipPropertyEvent.class ).obtain();
			event.init( selfRefId, PropertyEvent.INCREMENT_ACTION, OVDConstants.SHIELD, -1 );
			context.getScreenEventManager().postDelayedEvent( event );
		}
		else {
			ShipPropertyEvent event = Pools.get( ShipPropertyEvent.class ).obtain();
			event.init( selfRefId, PropertyEvent.INCREMENT_ACTION, OVDConstants.HULL, -value );
			context.getScreenEventManager().postDelayedEvent( event );
		}
	}
}
