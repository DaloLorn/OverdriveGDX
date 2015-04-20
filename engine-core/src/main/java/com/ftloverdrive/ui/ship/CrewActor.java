package com.ftloverdrive.ui.ship;

import java.util.Stack;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.local.LocalActorClickedEvent;
import com.ftloverdrive.event.local.LocalActorClickedListener;
import com.ftloverdrive.event.player.AbstractOrderEvent;
import com.ftloverdrive.event.player.CrewMoveOrderEvent;
import com.ftloverdrive.event.player.OrderListener;
import com.ftloverdrive.event.ship.CrewPropertyEvent;
import com.ftloverdrive.event.ship.CrewPropertyListener;
import com.ftloverdrive.io.AnimSpec;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.model.ship.Ambulator;
import com.ftloverdrive.model.ship.CrewModel;
import com.ftloverdrive.model.ship.ShipCoordinate;
import com.ftloverdrive.model.ship.ShipModel;
import com.ftloverdrive.ui.AmbulActor;


/**
 * TODO: If the game window is resized / moved while the crew is moving,
 * weird shit sometimes happens (crew moves slowly until it reaches the next
 * waypoint, or beelines off the screen)
 */
public class CrewActor extends AmbulActor
		implements Disposable, CrewPropertyListener, LocalActorClickedListener, OrderListener {

	protected Animation idleAnim;
	protected Animation walkAnimFront;
	protected Animation walkAnimBack;
	protected Animation walkAnimLeft;
	protected Animation walkAnimRight;

	protected Animation currentAnim;

	private float elapsed = 0;


	public CrewActor( OverdriveContext context ) {
		super( context );
	}

	@Override
	public void draw( Batch batch, float parentAlpha ) {
		if ( modelRefId != -1 ) {
			batch.draw( currentAnim.getKeyFrame( elapsed ), getX(), getY(), 0, 0,
					getWidth(), getHeight(), 1, 1, getRotation() );
		}
	}

	@Override
	public void act( float delta ) {
		if ( modelRefId != -1 ) {
			elapsed += delta;
			super.act( delta );

			Object o = context.getReferenceManager().getObject( modelRefId );
			Ambulator amb = (Ambulator)o;

			/*
			 * In FTL, crew moving diagonally:
			 * - from top-left to bottom-right uses walk-right animation
			 * - from from bottom-right to top-left uses walk-back animation
			 * - from bottom-left to top-right uses walk-right animation
			 * - from top-right to bottom-left uses walk-front animation
			 * Adjust if-cases with an epsilon to emulate that.
			 */
			float rad = velocity.angleRad();
			float quarterPi = (float)Math.PI / 4;
			float epsilon = (float)Math.PI / 30;
			// Sometimes on the first frame since issuing move order, the crew's velocity is still 0.
			if ( !amb.isAmbulating() || velocity.len2() == 0 ) {
				currentAnim = idleAnim;
			}
			else if ( rad > quarterPi + epsilon && rad <= 3 * quarterPi + epsilon ) {
				currentAnim = walkAnimBack;
			}
			else if ( rad > 3 * quarterPi + epsilon || rad < -3 * quarterPi - epsilon ) {
				currentAnim = walkAnimLeft;
			}
			else if ( rad < -quarterPi - epsilon && rad >= -3 * quarterPi - epsilon ) {
				currentAnim = walkAnimFront;
			}
			else if ( rad >= -quarterPi - epsilon || rad <= quarterPi + epsilon ) {
				currentAnim = walkAnimRight;
			}
		}
	}

	/**
	 * Updates everything to match the current CrewModel.
	 */
	protected void updateInfo( OverdriveContext context ) {
		if ( modelRefId == -1 ) {
		}
		else {
			CrewModel crewModel = context.getReferenceManager().getObject( modelRefId, CrewModel.class );
			AnimSpec spec = null;

			spec = crewModel.getIdleAnimSpec();
			idleAnim = spec.create( context );
			idleAnim.setPlayMode( PlayMode.LOOP );

			spec = crewModel.getWalkBackAnimSpec();
			walkAnimBack = spec.create( context );
			walkAnimBack.setPlayMode( PlayMode.LOOP );

			spec = crewModel.getWalkFrontAnimSpec();
			walkAnimFront = spec.create( context );
			walkAnimFront.setPlayMode( PlayMode.LOOP );

			spec = crewModel.getWalkLeftAnimSpec();
			walkAnimLeft = spec.create( context );
			walkAnimLeft.setPlayMode( PlayMode.LOOP );

			spec = crewModel.getWalkRightAnimSpec();
			walkAnimRight = spec.create( context );
			walkAnimRight.setPlayMode( PlayMode.LOOP );

			currentAnim = idleAnim;
			setSize( spec.getFrameWidth(), spec.getFrameHeight() );
		}
	}

	@Override
	public Actor hit( float x, float y, boolean touchable ) {
		if ( touchable && getTouchable() != Touchable.enabled ) return null;
		return x >= 0 && x < 35 && y >= 0 && y < 35 ? this : null;
	}

	@Override
	public void crewPropertyChanged( OverdriveContext context, CrewPropertyEvent e ) {
		if ( e.getModelRefId() != modelRefId ) return;

		updateInfo( context );
	}

	@Override
	public void actorClicked( OverdriveContext context, LocalActorClickedEvent e ) {
		if ( e.getTarget() instanceof CrewActor ) {
			CrewActor target = (CrewActor)e.getTarget();

			if ( target.getModelRefId() != modelRefId ) return;

			if ( e.getButton() == Buttons.LEFT ) {
				// TODO: Select
			}
		}
	}

	@Override
	public void dispose() {
		idleAnim = null;
		walkAnimBack = null;
		walkAnimFront = null;
		walkAnimLeft = null;
		walkAnimRight = null;

		elapsed = 0;
	}

	@Override
	public void orderIssued( OverdriveContext context, AbstractOrderEvent e ) {
		if ( e instanceof CrewMoveOrderEvent ) {
			CrewMoveOrderEvent ev = (CrewMoveOrderEvent)e;

			int playerRefId = ev.getPlayerRefId();
			GameModel gameModel = context.getReferenceManager().getObject( context.getGameModelRefId(), GameModel.class );
			int shipId = gameModel.getPlayerShip( playerRefId );
			ShipModel shipModel = context.getReferenceManager().getObject( shipId, ShipModel.class );

			if ( shipModel.getLayout().hasCrew( modelRefId ) ) {
				CrewModel crewModel = context.getReferenceManager().getObject( modelRefId, CrewModel.class );
				ShipCoordinate start = Pools.get( ShipCoordinate.class ).obtain();
				int x = (int)( getX() + getWidth() / 2 ) / 35;
				int y = (int)( getParent().getHeight() - ( getY() - getHeight() / 2 ) ) / 35;
				start.init( x, y, ShipCoordinate.TYPE_SQUARE );
				ShipCoordinate end = ev.getTargetCoordinate();

				// Don't update the path if the crew member is already heading there
				if ( !end.equals( crewModel.getAmbulationGoal() ) ) {
					Stack<ShipCoordinate> path = shipModel.getLayout().findPath( context, start, end, prevWaypoint, nextWaypoint );
					// Don't set the path if it doesn't exist. Prevents cancelling of movement in progress
					// (due to overwriting the old, valid path with new, null path)
					if ( path != null ) {
						crewModel.setAmbulationPath( path );
						crewModel.setAmbulationGoal( end );
					}
				}
				else {
					System.out.printf( "Already moving towards %s; order ignored.%n", end );
				}
			}
			else {
				// If the crew is not currently aboard the ship (ie. is boarding), then there's no way they can get there,
				// so just ignore the order.
			}
		}
	}
}
