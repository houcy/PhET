/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;
import edu.colorado.phet.ec3.model.spline.SegmentPath;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:54 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeSplineMode extends ForceMode {
    private AbstractSpline spline;
    private Body body;
    private double lastDA;
    private boolean lastGrabState = false;
    private boolean bounced = false;
    private boolean grabbed = false;
    private Segment lastSegment;
    private double bounceThreshold = 4;
    private final double flipTimeThreshold = 1.0;

    public FreeSplineMode( AbstractSpline spline, Body body ) {
        this.spline = spline;
        this.body = body;
    }

    private static class State {
        private EnergyConservationModel model;
        private Body body;

        public State( EnergyConservationModel model, Body body ) {
            this.model = model.copyState();
            this.body = body.copyState();
        }

        public EnergyConservationModel getModel() {
            return model;
        }

        public Body getBody() {
            return body;
        }

        public Point2D.Double getPosition() {
            return body.getPosition();
        }

        public double getMechanicalEnergy() {
            return model.getMechanicalEnergy( body );
        }

        public double getTotalEnergy() {
            return model.getTotalMechanicalEnergy( body ) + model.getThermalEnergy();
        }

        public double getHeat() {
            return model.getThermalEnergy();
        }
    }

    public void debug( String type, State originalState, EnergyConservationModel model, Body body ) {
        double dE = new State( model, body ).getTotalEnergy() - originalState.getTotalEnergy();
        if( Math.abs( dE ) > 1 ) {
            System.out.println( type + ", dE = " + dE );
        }
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        State originalState = new State( model, body );
        Segment segment = getSegment( body );
        if( segment == null ) {
            flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
            EC3Debug.debug( "Fly off surface!" );
            return;
        }
        rotateBody( body, segment, dt, getMaxRotDTheta( dt ) * 2 );
        AbstractVector2D netForce = computeNetForce( model, segment );
        super.setNetForce( netForce );
        super.stepInTime( model, body, dt ); //apply newton's laws
        debug( "newton's laws", originalState, model, body );
        segment = getSegment( body );
        if( segment == null ) {
            flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
            return;
        }
        setupBounce( body, segment );
        debug( "setup bounce", originalState, model, body );
        if( bounced && !grabbed && !lastGrabState ) {
            handleBounceAndFlyOff( body, model, dt, originalState );
        }
        else {
            double v = body.getVelocity().dot( segment.getUnitNormalVector() );
            if( v > 0.01 ) {
//                System.out.println( "v = " + new DecimalFormat( "0.000" ).format( v ) + ": flying off the track." );
                flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
                return;
            }
            rotateBody( body, segment, dt, Double.POSITIVE_INFINITY );
            segment = getSegment( body );//need to find our new segment after rotation.

            debug( "We just rotated body", originalState, model, body );
            setBottomAtZero( segment, body );
            debug( "set bottom to zero", originalState, model, body );
            AbstractVector2D dx = body.getPositionVector().getSubtractedInstance( new Vector2D.Double( originalState.getPosition() ) );
            double frictiveWork = bounced ? 0.0 : Math.abs( getFrictionForce( model, segment ).dot( dx ) );
            if( frictiveWork == 0 ) {//can't manipulate friction, so just modify v/h
                new EnergyConserver().fixEnergy( model, body, originalState.getMechanicalEnergy() );
            }
            else {
                patchEnergyInclThermal( frictiveWork, model, body, originalState );
            }
        }
        lastGrabState = grabbed;
        lastSegment = segment;
    }

    private void patchEnergyInclThermal( double frictiveWork, EnergyConservationModel model, Body body, State originalState ) {
        //modify the frictive work slightly so we don't have to account for all error energy in V and H.
        double allowedToModifyHeat = Math.abs( frictiveWork * 0.75 );
        model.addThermalEnergy( frictiveWork );
        debug( "Added thermal energy", originalState, model, body );
        double finalEnergy = model.getTotalMechanicalEnergy( body ) + model.getThermalEnergy();
        double energyError = finalEnergy - originalState.getTotalEnergy();

        double energyErrorSign = MathUtil.getSign( energyError );
        if( Math.abs( energyError ) > Math.abs( allowedToModifyHeat ) ) {//big problem
            System.out.println( "error was too large to fix only with heat" );
            model.addThermalEnergy( allowedToModifyHeat * energyErrorSign * -1 );

            double origTotalEnergyAll = originalState.getMechanicalEnergy() + originalState.getHeat();
            double desiredMechEnergy = origTotalEnergyAll - model.getThermalEnergy();
            new EnergyConserver().fixEnergy( model, body, desiredMechEnergy );//todo enhance energy conserver with thermal changes.
            debug( "FixEnergy", originalState, model, body );
            //This may be causing other problems
        }
        else {
            model.addThermalEnergy( -energyError );
            debug( "AddThermalEnergy", originalState, model, body );
        }
    }

    private Segment getSegment( Body body ) {
        Segment seg = getAttachmentSegment( body );
        if( seg == null ) {
            seg = getCollisionSegment( body );
        }
        return seg;
    }

    private Segment getCollisionSegment( Body body ) {
        try {
            return new SplineLogic( body ).guessSegment( spline );
        }
        catch( NullIntersectionException e ) {
            return null;
        }
    }

    //maybe problem is from discontinuity here
    private Segment getAttachmentSegment( Body body ) {
        Point2D.Double attachPoint = body.getAttachPoint();
        SegmentPath segPath = spline.getSegmentPath();
        double bestDist = Double.POSITIVE_INFINITY;
        Segment bestSeg = null;
        for( int i = 0; i < segPath.numSegments(); i++ ) {
            Segment seg = segPath.segmentAt( i );
            double dist = attachPoint.distance( seg.getCenter2D() );
            if( lastSegment != null ) {  //only pick a nearby segment
                int indexOffset = Math.abs( seg.getID() - lastSegment.getID() );
                if( indexOffset > 5 ) {
                    dist += indexOffset * 5.0;
                }
            }
            if( dist < bestDist ) {
                bestSeg = seg;
                bestDist = dist;
            }
        }

        if( bestDist > body.getShape().getBounds2D().getWidth() / 2.0 ) {//too far away
            return null;
        }
        return bestSeg;
    }

    private void handleBounceAndFlyOff( Body body, EnergyConservationModel model, double dt, State originalState ) {
//        System.out.println( "DIDBOUNCE" );
        //coeff of restitution
        double coefficientOfRestitution = body.getCoefficientOfRestitution();
        double finalVelocity = coefficientOfRestitution * body.getVelocity().getMagnitude();
        AbstractVector2D vec = body.getVelocity().getInstanceOfMagnitude( finalVelocity );
        double initKE = body.getKineticEnergy();
        body.setVelocity( vec );
        double finalKE = body.getKineticEnergy();
        if( finalKE > initKE ) {
            System.out.println( "Something is very wrong." );
        }

        double dE = initKE - finalKE;
        model.addThermalEnergy( dE );

        flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() - dE );
    }

    //just kill the perpendicular part of velocity, if it is through the track.
    // this should be lost to friction or to a bounce.
    private void setupBounce( Body body, Segment segment ) {
        this.bounced = false;
        this.grabbed = false;

        double angleOffset = body.getVelocity().dot( segment.getUnitDirectionVector() ) < 0 ? Math.PI : 0;
        AbstractVector2D newVelocity = Vector2D.Double.parseAngleAndMagnitude( body.getVelocity().getMagnitude(), segment.getAngle() + angleOffset );
        body.setVelocity( newVelocity );
    }

    private void rotateBody( Body body, Segment segment, double dt, double maxRotationDTheta ) {
        double bodyAngle = body.getAngle();
        double dA = segment.getAngle() - bodyAngle;
//        System.out.println( "seg=" + segment.getAngle() + ", body=" + bodyAngle + ", da=" + dA );

        if( dA > Math.PI ) {
            dA -= Math.PI * 2;
        }
        else if( dA < -Math.PI ) {
            dA += Math.PI * 2;
        }
//        double maxRotationDTheta = Math.PI / 16 * dt / 0.2;
        if( dA > maxRotationDTheta ) {
            dA = maxRotationDTheta;
        }
        else if( dA < -maxRotationDTheta ) {
            dA = -maxRotationDTheta;
        }
        body.rotate( dA );
        this.lastDA = dA;
    }

    private double getMaxRotDTheta( double dt ) {
        return Math.PI / 16 * dt / 0.2;
    }

    private void flyOffSurface( Body body, EnergyConservationModel model, double dt, double origTotalEnergy ) {
//        System.out.println( "FreeSplineMode.flyOffSurface" );
        double vy = body.getVelocity().getY();
        double timeToReturnToThisHeight = model.getGravity() != 0 ? Math.abs( 2 * vy / model.getGravity() ) : 1000;

        double numTimeSteps = timeToReturnToThisHeight / dt;
        double dTheta = Math.PI * 2 / numTimeSteps / dt;
//        System.out.println( "timeToReturnToThisHeight = " + timeToReturnToThisHeight );
        if( timeToReturnToThisHeight > flipTimeThreshold ) {
            body.setFreeFallRotation( dTheta );
//            System.out.println( "Flipping!: dTheta=" + dTheta );
        }
        else {
            double rot = lastDA;
            if( rot > getMaxRotDTheta( dt ) ) {
                rot = getMaxRotDTheta( dt );
            }
            if( rot < -getMaxRotDTheta( dt ) ) {
                rot = -getMaxRotDTheta( dt );
            }
            body.setFreeFallRotation( rot );
        }
        body.setFreeFallMode();
        super.setNetForce( new Vector2D.Double( 0, 0 ) );
        super.stepInTime( model, body, dt );
        new EnergyConserver().fixEnergy( model, body, origTotalEnergy );
    }

    private void setBottomAtZero( Segment segment, Body body ) {
        double overshoot = getWolfDepthInSegment( segment, body );
        if( Math.abs( overshoot ) > 1E-4 ) {
            System.out.println( "overshoot = " + overshoot );
        }
        double scale = 1.0;
//        if (body.getVelocity().getMagnitude()<0.1){
//            scale=0.00001;
//        }
        AbstractVector2D tx = segment.getUnitNormalVector().getScaledInstance( -overshoot * scale );
        body.translate( tx.getX(), tx.getY() );
    }

    private double getWolfDepthInSegment( Segment segment, Body body ) {
        Point2D p1 = segment.getP0();
        Point2D p2 = segment.getP1();
        Line2D l = new Line2D.Double( p1, p2 );
        Point2D p0 = body.getAttachPoint();
        double dist = l.ptLineDist( p0 );

        Vector2D.Double x = new Vector2D.Double( segment.getMidpoint(), p0 );
        dist *= MathUtil.getSign( x.dot( segment.getUnitNormalVector() ) );
        return dist;
    }

    private AbstractVector2D computeNetForce( EnergyConservationModel model, Segment segment ) {
        AbstractVector2D[] forces = new AbstractVector2D[]{
                getGravityForce( model ),
                getNormalForce( model, segment ),
                getThrustForce(),
                getFrictionForce( model, segment )
        };
        Vector2D.Double sum = new Vector2D.Double();
        for( int i = 0; i < forces.length; i++ ) {
            AbstractVector2D force = forces[i];
            sum.add( force );
        }
        return sum;
    }

    private AbstractVector2D getGravityForce( EnergyConservationModel model ) {
        return new Vector2D.Double( 0, getFGy( model ) );
    }

    private AbstractVector2D getThrustForce() {
        return body.getThrust();
    }

    private double getFGy( EnergyConservationModel model ) {
        return model.getGravity() * body.getMass();
    }

    private AbstractVector2D getFrictionForce( EnergyConservationModel model, Segment segment ) {
        double fricMag = getFrictionCoefficient() * getNormalForce( model, segment ).getMagnitude();
        return body.getVelocity().getInstanceOfMagnitude( -fricMag * 5 );
    }

    private AbstractVector2D getNormalForce( EnergyConservationModel model, Segment segment ) {
//        if( segment.getUnitNormalVector().dot( getGravityForce( model ) ) < 0 ) {//todo is this correct?
        return segment.getUnitNormalVector().getScaledInstance( getFGy( model ) * Math.cos( segment.getAngle() ) );
//        }
//        else {
//            return new ImmutableVector2D.Double();
//        }
    }

    private double getFrictionCoefficient() {
        return body.getFrictionCoefficient();
    }

    public AbstractSpline getSpline() {
        return spline;
    }

}
