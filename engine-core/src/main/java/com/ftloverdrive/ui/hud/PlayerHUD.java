package com.ftloverdrive.ui.hud;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.event.system.SystemPropertyListener;
import com.ftloverdrive.event.system.SystemPropertySentinel;

public class PlayerHUD extends Group {
    private OverdriveContext context;

    private PlayerShipHullMonitor playerShipHullMonitor;
    private PlayerShipShieldMonitor playerShipShieldMonitor;
    private PlayerScrapMonitor playerScrapMonitor;
    private PlayerShipReactorUI playerShipReactor;
    private PlayerShipDoorHighlighter doorHighlighter;

    /**
     *
     * @param context
     */
    public PlayerHUD(OverdriveContext context){
        super();
        this.context = context;

        playerShipHullMonitor = new PlayerShipHullMonitor( context );
        playerShipHullMonitor.setPosition( 0, getHeight() - playerShipHullMonitor.getHeight() );
        addActor( playerShipHullMonitor );

        playerShipShieldMonitor = new PlayerShipShieldMonitor( context );
        playerShipShieldMonitor.setPosition( 0,
                getHeight() - playerShipHullMonitor.getHeight() - playerShipShieldMonitor.getHeight() );
        addActor( playerShipShieldMonitor );

        playerScrapMonitor = new PlayerScrapMonitor( context );
        playerScrapMonitor.setPosition( playerShipHullMonitor.getWidth(),
                getHeight() - playerScrapMonitor.getHeight() );
        addActor( playerScrapMonitor );

        playerShipReactor = new PlayerShipReactorUI( context );
        playerShipReactor.setPosition( 45, 5 );
        addActor( playerShipReactor );

        doorHighlighter = new PlayerShipDoorHighlighter( context );
        doorHighlighter.setVisible( false );
        addActor( doorHighlighter );
    }

    public PlayerShipDoorHighlighter getDoorHighlighter() {
        return doorHighlighter;
    }

    public PlayerScrapMonitor getPlayerScrapMonitor() {
        return playerScrapMonitor;
    }

    public PlayerShipHullMonitor getPlayerShipHullMonitor() {
        return playerShipHullMonitor;
    }

    public PlayerShipShieldMonitor getPlayerShipShieldMonitor() {
        return playerShipShieldMonitor;
    }

    public PlayerShipReactorUI getPlayerShipReactor() {
        return playerShipReactor;
    }

    /**
     * Register elements of HUD as event listeners.
     * @param eventManager Event manager to listen events from.
     */
    public void registerEventListeners(OVDEventManager eventManager){
        eventManager.addEventListener( playerShipHullMonitor, GamePlayerShipChangeListener.class );
        eventManager.addEventListener( playerShipHullMonitor, ShipPropertyListener.class );
        eventManager.addEventListener( playerShipShieldMonitor, GamePlayerShipChangeListener.class );
        eventManager.addEventListener( playerShipShieldMonitor, ShipPropertyListener.class );
        eventManager.addEventListener( playerScrapMonitor, GamePlayerShipChangeListener.class );
        eventManager.addEventListener( playerScrapMonitor, ShipPropertyListener.class );
        eventManager.addEventListener( playerShipReactor, GamePlayerShipChangeListener.class );
        eventManager.addEventListener( playerShipReactor, ShipPropertyListener.class );
        eventManager.addEventListener( playerShipReactor, SystemPropertyListener.class );
        eventManager.addEventListener( playerShipReactor, SystemPropertySentinel.class );
    }

    /**
     * Needs to be called everytime parent is resized to match it. Sets positions for children
     * @param width
     * @param height
     */
    public void resize(int width, int height){
        setWidth(width);
        setHeight(height);

        // HUD
        playerShipHullMonitor.setPosition( 0, getHeight() - playerShipHullMonitor.getHeight() );
        playerScrapMonitor.setPosition( playerShipHullMonitor.getWidth(),
                getHeight() - playerScrapMonitor.getHeight() );
        playerShipShieldMonitor.setPosition( 0,
                getHeight() - playerShipHullMonitor.getHeight() - playerShipShieldMonitor.getHeight() + 22 );
    }

    public void dispose(){
        playerShipHullMonitor.dispose();
        playerScrapMonitor.dispose();
        playerShipShieldMonitor.dispose();
        playerShipReactor.dispose();
    }
}
