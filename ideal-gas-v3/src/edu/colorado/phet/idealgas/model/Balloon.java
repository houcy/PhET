/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:53:09 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.util.ScalarDataRecorder;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

public class Balloon extends HollowSphere {

    private ScalarDataRecorder insidePressureRecorder;
    private ScalarDataRecorder outsidePressureRecorder;

    // Attributes for adjusting the size of the balloon
    private int timeStepsSinceLastRadiusAdjustment = 0;
    private int timeStepsBetweenRadiusAdjustments = 20;
    private double aveInOutPressureRatio = 0;
    // Exponent for the power function that adjusts the size of the balloon when
    // the internal or external pressure changes
    private double dampingExponent = 0.02;
    // Temporary varaibles, pre-allocated for performance
    private Vector2D momentumPre = new Vector2D.Double();
    private Vector2D momentumPost = new Vector2D.Double();
    private Box2D box;

    /**
     * @param center
     * @param velocity
     * @param acceleration
     * @param mass
     * @param radius
     */
    public Balloon( Point2D center,
                    Vector2D velocity,
                    Vector2D acceleration,
                    float mass,
                    float radius,
                    Box2D box,
                    AbstractClock clock ) {
        super( center, velocity, acceleration, mass, radius );
        this.box = box;
        insidePressureRecorder = new ScalarDataRecorder( clock );
        outsidePressureRecorder = new ScalarDataRecorder( clock );
    }

//    public void reInitialize() {
//        super.reInitialize();
//        insidePressureRecorder.clear();
//        outsidePressureRecorder.clear();
//    }

    /**
     * Records the impact on the inside or outside of the balloon
     */
    IdealGasParticle idealGasParticle = new IdealGasParticle( new Point2D.Double(),
                                                              new Vector2D.Double(),
                                                              new Vector2D.Double(),
                                                              0 );

    public void collideWithParticle( IdealGasParticle particle ) {

        // Get the momentum of the balloon before the collision
        momentumPre.setX( getVelocity().getX() );
        momentumPre.setY( getVelocity().getY() );
        momentumPre = momentumPre.scale( this.getMass() );

        // This bizarre copying from one object to another is a total hack that
        // was made neccessary by the creation of the CollisionGod class, and the
        // fact that some of the system uses Particles from the common code, and
        // some of it uses Particles from the ideal gas code. It is an embarassing
        // mess that ought to be straightened out.
        idealGasParticle.setPosition( particle.getPosition() );
        idealGasParticle.setVelocity( particle.getVelocity() );
        super.collideWithParticle( idealGasParticle );

        // Get the new momentum of the balloon
        momentumPost.setX( this.getVelocity().getX() );
        momentumPost.setY( this.getVelocity().getY() );
        momentumPost = momentumPost.scale( this.getMass() );

        // Compute the change in momentum and record it as pressure
        Vector2D momentumChange = momentumPost.subtract( momentumPre );
        double impact = momentumChange.getMagnitude();
        ScalarDataRecorder recorder = this.containsBody( particle )
                                      ? insidePressureRecorder
                                      : outsidePressureRecorder;
        recorder.addDataRecordEntry( impact );
    }

    public void recordImpact( float impact,
                              Body body ) {
        ScalarDataRecorder recorder = this.containsBody( body )
                                      ? insidePressureRecorder
                                      : outsidePressureRecorder;
        recorder.addDataRecordEntry( impact );
    }

    /**
     * @return
     */
    public double getInsidePressure() {
        return insidePressureRecorder.getDataTotal();
    }

    /**
     * @return
     */
    public double getOutsidePressure() {
        return outsidePressureRecorder.getDataTotal();
    }

    /**
     * @param dt
     */
    public void stepInTime( float dt ) {

        super.stepInTime( dt );

        // Compute average pressure differential
        double currInOutPressureRatio = insidePressureRecorder.getDataTotal() / outsidePressureRecorder.getDataTotal();
        if( !Double.isNaN( currInOutPressureRatio )
            && currInOutPressureRatio != 0
            && currInOutPressureRatio != Double.POSITIVE_INFINITY
            && currInOutPressureRatio != Double.NEGATIVE_INFINITY ) {
            aveInOutPressureRatio = ( ( aveInOutPressureRatio * timeStepsSinceLastRadiusAdjustment )
                                      + ( insidePressureRecorder.getDataTotal() / outsidePressureRecorder.getDataTotal() ) )
                                    / ( ++timeStepsSinceLastRadiusAdjustment );
        }

        // Adjust the radius of the balloon
        //Make sure the balloon doesn't expand beyond the box
//        Box2D box = ((IdealGasSystem)this.getPhysicalSystem()).getBox();
        double maxRadius = Math.min( ( box.getMaxX() - box.getMinX() ) / 2,
                                     ( box.getMaxY() - box.getMinY() ) / 2 );
        if( timeStepsSinceLastRadiusAdjustment >= timeStepsBetweenRadiusAdjustments ) {
            timeStepsSinceLastRadiusAdjustment = 0;

            float newRadius = (float)Math.min( this.getRadius() * Math.pow( aveInOutPressureRatio, dampingExponent ), maxRadius );
            if( !Double.isNaN( newRadius ) ) {
                newRadius = (float)Math.min( maxRadius, Math.max( newRadius, 20 ) );
                this.setRadius( newRadius );
            }
        }
    }

    /**
     * @param body
     * @return
     */
    public boolean isInContactWithBody( Body body ) {
        throw new RuntimeException( "Not implemented" );
    }
}

