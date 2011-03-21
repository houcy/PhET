// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;

import edu.colorado.phet.bendinglight.model.LightRay;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class LightWaveNode extends PNode {

    public LightWaveNode( final ModelViewTransform transform, final LightRay lightRay ) {
        addChild( new PhetPPath( createPaint( transform, lightRay ) ) {{
            lightRay.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( lightRay.getWaveShape() ) );
                }
            } );
            lightRay.addStepListener( new VoidFunction0() {
                public void apply() {
                    setPaint( createPaint( transform, lightRay ) );
                }
            } );
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    private GradientPaint createPaint( ModelViewTransform transform, LightRay lightRay ) {
        double viewWavelength = transform.modelToViewDeltaX( lightRay.getWavelength() );
        final ImmutableVector2D directionVector = transform.modelToViewDelta( lightRay.toVector2D() ).getNormalizedInstance();
        final ImmutableVector2D waveVector = directionVector.times( viewWavelength );

        Color color = lightRay.getColor();
        final Color red = new Color( color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float) Math.sqrt( lightRay.getPowerFraction() ) );
        final Color black = new Color( 0, 0, 0, (float) Math.sqrt( lightRay.getPowerFraction() ) );

        //For debugging the phase of the transmitted wave
//        final Color red = new Color( 1f, 0, 0, 0.5f );
//        final Color black = new Color( 0, 0, 0, 0.5f );

        final double totalPhaseOffsetInNumberOfWavelengths = lightRay.getPhaseOffset() / 2 / Math.PI;
        ImmutableVector2D phaseOffset = directionVector.times( transform.modelToViewDeltaX( totalPhaseOffsetInNumberOfWavelengths * lightRay.getWavelength() ) );
        float x0 = (float) ( phaseOffset.getX() + transform.modelToViewX( lightRay.tail.getValue().getX() ) );//the rightmost term ensures that phase doesn't depend on angle of the beam.
        float y0 = (float) ( phaseOffset.getY() + transform.modelToViewY( lightRay.tail.getValue().getY() ) );
        return new GradientPaint( x0, y0, red,
                                  x0 + (float) waveVector.getX() / 2, y0 + (float) waveVector.getY() / 2, black, true );
    }
}