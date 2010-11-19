package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * @author Sam Reid
 */
public class MeterStick extends FluidPressureAndFlowRuler {
    public MeterStick( ModelViewTransform transform, final Property<Boolean> visible, Point2D.Double rulerModelOrigin ) {
        super( transform, visible, Math.abs( transform.modelToViewDeltaY( 5 ) ), new String[] { "0", "1", "2", "3", "4", "5" }, "m", rulerModelOrigin );
    }
}
