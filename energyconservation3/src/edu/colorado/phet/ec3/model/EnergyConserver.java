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

        EC3Debug.debug( "body.getSpeed() = " + body.getSpeed() );
        EnergyDebugger.stepFinished( model, body );
        double speedThreshold = 1;//reduced from 20.
        for( int i = 0; i < 10; i++ ) {
            if( body.getSpeed() > speedThreshold ) {
                conserveEnergyViaV( model, body, desiredMechanicalEnergy );
            }
            else {
            }
        }
//        if( model.getGravity() != 0.0 ) {
//            conserveEnergyViaH( model, body, desiredMechanicalEnergy );
//        }
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
        return model.getTotalMechanicalEnergy( body ) - desiredMechanicalEnergy;
    }

    private void conserveEnergyViaH( EnergyConservationModel model, Body body, double origTotalEnergy ) {
        double dE = getDE( model, body, origTotalEnergy );
        EC3Debug.debug( "dE = " + dE );
        double dh = dE / body.getMass() / model.getGravity();
        if( model.getGravity() == 0 ) {
            dh = 0.0;
        }
        body.translate( 0, dh );
        double dEMod = getDE( model, body, origTotalEnergy );
        EC3Debug.debug( "dEModH = " + dEMod );
    }
}
