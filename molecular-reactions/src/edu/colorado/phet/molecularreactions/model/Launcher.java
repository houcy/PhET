/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mechanics.DefaultBody;

import java.awt.geom.Point2D;

/**
 * Launcher
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Launcher extends DefaultBody implements ModelElement, PotentialEnergySource {

    public static class MovementType {
        private MovementType() {
        }
    }
    public static MovementType ONE_DIMENSIONAL = new MovementType();
    public static MovementType TWO_DIMENSIONAL = new MovementType();


    private Point2D restingTipLocation;
    private double maxPlungerDraw;
    private double springK = 1;
    private SimpleMolecule bodyToLaunch;
    private MovementType movementType = TWO_DIMENSIONAL;

    /**
     *
     * @param restingTipLocation
     */
    public Launcher( Point2D restingTipLocation ) {
        this.restingTipLocation = restingTipLocation;
    }

    public void setBodyToLaunch( SimpleMolecule bodyToLaunch ) {
        this.bodyToLaunch = bodyToLaunch;
        bodyToLaunch.setVelocity( 0,0 );
    }

    public void release() {
        if( bodyToLaunch != null ) {
            double s = Math.sqrt( 2 * getPE() / bodyToLaunch.getMass() );
            Vector2D v = new Vector2D.Double( 0, -s );
            v.rotate( getTheta() );
            bodyToLaunch.setVelocity( v );
            bodyToLaunch = null;
        }
        setTipLocation( restingTipLocation );
    }

    public double getPE() {
        double dl = getTipLocation().distance( restingTipLocation );
        double pe = springK * dl * dl / 2;
        return pe;
    }

    public Point2D getPivotPoint() {
        if( bodyToLaunch == null ) {
            return getRestingTipLocation();
        }
        else {
            return new Point2D.Double( getRestingTipLocation().getX() ,
                                getRestingTipLocation().getY() - bodyToLaunch.getRadius() );
        }
    }

    public Point2D getTipLocation() {
        return getPosition();
    }

    public void setTheta( double theta ) {
        super.setTheta( theta );
        notifyObservers();
    }

    public void stepInTime( double dt ) {
        // noop
    }

    public Point2D getRestingTipLocation() {
        return restingTipLocation;
    }

    public void setTipLocation( Point2D p ) {
        setTipLocation( p.getX(), p.getY() );
    }

    public void setTipLocation( double x, double y ) {
        setPosition( x, y );
    }

    public void setMovementType( MovementType movementType ) {
        this.movementType = movementType;
    }

    public MovementType getMovementType() {
        return movementType;
    }
}
