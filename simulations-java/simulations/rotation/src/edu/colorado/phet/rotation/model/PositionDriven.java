package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class PositionDriven implements UpdateStrategy {

    //todo: try 2nd order derivative directly from position data
    public void update( RotationModel model, double dt ) {
        double vel = RotationMath.estimateDerivative( model.getAvailablePositionTimeSeries( 10 ) );
        double acc = RotationMath.estimateDerivative( model.getAvailableVelocityTimeSeries( 10 ) );
        model.setAngularVelocity( vel );
        model.setAngularAcceleration( acc );
    }
}
