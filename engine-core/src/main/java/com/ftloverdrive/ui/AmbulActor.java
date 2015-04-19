package com.ftloverdrive.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.model.ship.Ambulator;
import com.ftloverdrive.model.ship.ShipCoordinate;


/**
 * An actor that supports movement for Models implementing the Ambulator interface.
 *
 */
public abstract class AmbulActor extends ModelActor implements EventListener {

	/**
	 * Cache the previous waypoint to fix some edge-cases with pathing.
	 */
	protected ShipCoordinate prevWaypoint = null;
	/**
	 * Cache the next waypoint, since path is stack-based -- polling the model for
	 * next waypoint removes it from the stack.
	 */
	protected ShipCoordinate nextWaypoint = null;
	/**
	 * Cache the goal waypoint so that we update the path immediately when the
	 * model's goal changes.
	 */
	protected ShipCoordinate goalWaypoint = null;

	protected Vector2 velocity = null;


	public AmbulActor( OverdriveContext context ) {
		super( context );
		velocity = new Vector2( 0, 0 );
	}

	public void setModelRefId( int modelRefId ) {
		Object o = context.getReferenceManager().getObject( modelRefId );
		if ( o instanceof Ambulator == false ) {
			throw new IllegalArgumentException( String.format( "Model referenced by ID %s does not implement %s interface.",
					modelRefId, Ambulator.class.getSimpleName() ) );
		}
		super.setModelRefId( modelRefId );
	}

	@Override
	public void act( float delta ) {
		if ( modelRefId != -1 ) {
			Ambulator amb = context.getReferenceManager().getObject( modelRefId, Ambulator.class );

			if ( amb.isAmbulating() ) {
				// If we don't have a waypoint, then take one.
				// Also check if our goal has changed (ie. we've been issued a new order),
				// so that we start moving toward it immediately, instead of completing
				// our previous move.
				boolean updateVelocity = false;
				if ( nextWaypoint == null || goalWaypoint != amb.getAmbulationGoal() ) {
					goalWaypoint = amb.getAmbulationGoal();
					nextWaypoint = amb.getNextWaypoint();
					updateVelocity = true;
				}

				if ( nextWaypoint == null ) {
					// If we haven't received a new waypoint, it means we've already reached our goal.
					setPosition( goalWaypoint.x * 35, getParent().getHeight() - goalWaypoint.y * 35 );
					amb.setAmbulationPath( null );
					velocity.set( 0, 0 );
				}
				else {
					boolean teleport = prevWaypoint != null && prevWaypoint.isTeleportPad() && nextWaypoint.isTeleportPad();
					// Compute the distance between current position and the target waypoint
					Vector2 d = new Vector2( nextWaypoint.x * 35 - getX(), getParent().getHeight() - nextWaypoint.y * 35 - getY() );
					float speed = amb.getAmbulationSpeed() * delta;
					if ( teleport || d.len2() <= speed * speed ) {
						// If the actor is close enough that it will complete the move this update, go grab the next waypoint
						setPosition( nextWaypoint.x * 35, getParent().getHeight() - nextWaypoint.y * 35 );
						prevWaypoint = nextWaypoint;
						nextWaypoint = null;
					}
					else if ( updateVelocity ) {
						// Normalize the difference vector to a unit vector, then multiply by the ambulator's speed
						velocity.set( d.nor().scl( speed ) );
						updateVelocity = false;
					}
				}

				if ( nextWaypoint != null )
					setPosition( getX() + velocity.x, getY() + velocity.y );
			}
		}
	}
}
