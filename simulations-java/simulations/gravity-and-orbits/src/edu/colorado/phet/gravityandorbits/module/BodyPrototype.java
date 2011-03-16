// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Class that facilitates configuration of body instances for a GravityAndOrbitsMode.
 *
 * @author Sam Reid
 */
public class BodyPrototype {
    public double mass;
    public double radius;
    public double x;
    public double y;
    public double vx;
    public double vy;

    public BodyPrototype( double mass, double radius, double x, double y, double vx, double vy ) {
        this.mass = mass;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public ImmutableVector2D getMomentum() {
        return new ImmutableVector2D( vx * mass, vy * mass );
    }
}
