package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.timeseries.model.TimeModelClock;

/**
 * Author: Sam Reid
 * Jun 20, 2007, 12:26:53 AM
 */
public class TestDerivativeOffsets {
    private SingleBodyMotionModel motionModel;
    private TimeModelClock clock;

    public TestDerivativeOffsets() {
        clock = new TimeModelClock( 30, 1 );
        motionModel = new SingleBodyMotionModel( clock );
        motionModel.setPositionDriven();
    }

    private void step( int i ) {
        for( int k = 0; k < i; k++ ) {
            clock.stepClockWhilePaused();
            showState( motionModel );
        }
    }

    private void showState( SingleBodyMotionModel motionModel ) {
//        System.out.println( "t=" + motionModel.getTime() + ", x=" + motionModel.getPosition() + ", v=" + motionModel.getVelocity() + ", a=" + motionModel.getAcceleration() );
        System.out.println( "x.t=" + motionModel.getTime() + ", x=" + motionModel.getMotionBodyState().getPosition() +
                            ", v.t=" + motionModel.getTime() + ", v=" + motionModel.getMotionBodyState().getVelocity() +
                            ", a.t=" + motionModel.getTime() + ", a=" + motionModel.getMotionBodyState().getAcceleration() );
    }

    private void start() {
        showState( motionModel );
        step( 100 );
        motionModel.getMotionBodyState().setPosition( 1.0 );
        step( 100 );
    }

    public static void main( String[] args ) {
        new TestDerivativeOffsets().start();
    }

}
