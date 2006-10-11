/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 11:38:49 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class EnergyConserver {
    public void fixEnergy( EnergyConservationModel model, Body body, double desiredMechanicalEnergy ) {
        if( body.getThrust().getMagnitude() != 0 ) {
            return;
        }
        EnergyDebugger.stepFinished( model, body );
        double speedThreshold = 1;//reduced from 20.
        for( int i = 0; i < 10; i++ ) {
            if( body.getSpeed() > speedThreshold ) {
                conserveEnergyViaV( model, body, desiredMechanicalEnergy );
            }
            else {
            }
        }
        for( int i = 0; i < 3; i++ ) {
            if( Math.abs( model.getGravity() ) > 1.0 ) {
                conserveEnergyViaH( model, body, desiredMechanicalEnergy );
            }
        }
//        double mechEnergy = model.getMechanicalEnergy( body );
//        System.out.println( "requested mechEnergy = " + desiredMechanicalEnergy + ", obtained me=" + mechEnergy );
    }

    private void conserveEnergyViaV( EnergyConservationModel model, Body body, double desiredMechanicalEnergy ) {
        double dE = getDE( model, body, desiredMechanicalEnergy );
        //alter the velocity to account for this difference.
//        double dv = dE / body.getMass() / body.getSpeed();
        double dv = dE / body.getMass() / body.getSpeed();
        AbstractVector2D dvVector = body.getVelocity().getInstanceOfMagnitude( -dv );
        body.setVelocity( dvVector.getAddedInstance( body.getVelocity() ) );
    }

    private double getDE( EnergyConservationModel model, Body body, double desiredMechanicalEnergy ) {
        return model.getMechanicalEnergy( body ) - desiredMechanicalEnergy;
    }

    private void conserveEnergyViaH( EnergyConservationModel model, Body body, double desiredMechEnergy ) {
        double dE = getDE( model, body, desiredMechEnergy );
        double dh = dE / body.getMass() / model.getGravity();
        body.translate( 0, dh );
//        System.out.println( "------->requested mechEnergy = " + desiredMechEnergy+ ", obtained me=" + model.getMechanicalEnergy( body ));
    }
}
