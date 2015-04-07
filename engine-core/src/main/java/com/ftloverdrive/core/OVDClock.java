package com.ftloverdrive.core;

import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.event.engine.DelayedEvent;
import com.ftloverdrive.event.engine.TickEvent;


public class OVDClock {

	private OVDEventManager eventManager;

	/** Milliseconds per tick of game-time. */
	private final int tickRate = 1000;

	private int spareTime;
	// Micro-optimization to avoid redeclaring a variable.
	private int elapsedTicks;

	private final Pool<TickEvent> tickEventPool;
	/** Remembers for which ticks to check. */
	private final IntIntMap tickRefMap;
	private final IntIntMap tickCountMap;
	private final ObjectMap<DelayedEvent, Integer> delayedEventMap;


	public OVDClock( OVDEventManager eventManager ) {
		this.eventManager = eventManager;

		tickEventPool = Pools.get( TickEvent.class );
		tickRefMap = new IntIntMap();
		tickCountMap = new IntIntMap();
		delayedEventMap = new ObjectMap<DelayedEvent, Integer>();

		// 1-length ticks are always performed.
		tickRefMap.put( 1, 1 );
	}

	/**
	 * Signals that real-world time has elapsed since the last call.
	 *
	 * As enough time accumulates, TickEvents may be generated.
	 */
	public void secondsElapsed( float t ) {
		spareTime += (int)( t * 1000 ); // Add as milliseconds.
		elapsedTicks = spareTime / tickRate;

		if ( elapsedTicks > 0 ) {
			for ( IntIntMap.Entry entry : tickRefMap ) {
				tickCountMap.getAndIncrement( entry.key, 0, elapsedTicks );
				int i = tickCountMap.get( entry.key, 0 );

				if ( i >= entry.key ) {
					tickCountMap.put( entry.key, i - entry.key );
					TickEvent tickEvent = tickEventPool.obtain();
					tickEvent.init();
					tickEvent.setTickCount( i );

					eventManager.postDelayedEvent( tickEvent );
				}
			}

			for ( DelayedEvent de : delayedEventMap.keys() ) {
				int ticks = delayedEventMap.get( de ) + elapsedTicks;
				if ( ticks >= de.getTickDelay() ) {
					delayedEventMap.remove( de );
					eventManager.postDelayedInboundEvent( de.getEvent() );
				}
				else
					delayedEventMap.put( de, ticks );
			}

			spareTime = spareTime % tickRate;
		}
	}

	protected void postDelayedEvent( DelayedEvent de ) {
		delayedEventMap.put( de, 0 );
	}

	/**
	 * Increments an internal ref counter for how many listeners there are for each tick count.
	 * This should not normally be used.
	 */
	protected void incrTick( int tickCount ) {
		if ( tickCount < 2 )
			return;
		tickRefMap.getAndIncrement( tickCount, 0, 1 );
	}

	/**
	 * Decrements an internal ref counter for how many listeners there are for each tick count.
	 * This should not normally be used.
	 */
	protected void decrTick( int tickCount ) {
		if ( tickCount < 2 || !tickRefMap.containsKey( tickCount ) )
			return;
		tickRefMap.getAndIncrement( tickCount, 0, -1 );
		if ( tickRefMap.get( tickCount, 0 ) == 0 ) {
			tickRefMap.remove( tickCount, 0 );
			tickCountMap.remove( tickCount, 0 );
		}
	}
}
