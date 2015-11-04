package com.ftloverdrive.model.ship;

import com.ftloverdrive.util.OVDConstants;

/**
 * Created by mateusz on 2015-11-04.
 */
public class DefaultShipModel extends AbstractShipModel{

    public DefaultShipModel(){
        super();

        this.getProperties().setInt( OVDConstants.HEALTH_MAX, 30 );
        this.getProperties().setInt( OVDConstants.HEALTH, 30 );
        this.getProperties().setInt( OVDConstants.POWER_DISABLED, 0 );
        this.getProperties().setInt( OVDConstants.POWER_MAX, 25 );
        this.getProperties().setInt( OVDConstants.POWER, 8 );
    }

}
