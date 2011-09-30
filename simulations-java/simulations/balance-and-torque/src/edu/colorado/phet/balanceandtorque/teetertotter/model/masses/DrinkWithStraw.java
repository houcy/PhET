// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a drink with a straw in it.
 *
 * @author John Blanco
 */
public class DrinkWithStraw extends ImageMass {

    private static final double MASS = 1; // in kg
    private static final double HEIGHT = 0.2; // In meters.

    public DrinkWithStraw() {
        super( MASS, Images.DRINK_WITH_STRAW, HEIGHT, new Point2D.Double( 0, 0 ), true );
    }
}
