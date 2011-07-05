// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.umd.cs.piccolo.nodes.PText;

import static java.awt.Color.white;

/**
 * Graphical display of the beaker, including tick marks and labels along the side
 *
 * @author Sam Reid
 */
public class BeakerNodeWithTicks extends BeakerNode {
    public BeakerNodeWithTicks( ModelViewTransform transform, Beaker beaker ) {
        super( transform, beaker );

        //Add Tick marks and labels
        double viewX = transform.modelToViewX( beaker.getX() - beaker.getWallThickness() / 2 );

        //only show labels (and tick marks) for 0 L, 1 L, and 2 L, and tick marks for 0.5 L and 1.5 L

        //Show the minor ticks (behind the major ticks since we just draw over them).  Show one tick every 0.5L
        final double scale = Math.sqrt( 8E-23 );
        double dMinorTick = 0.0005 * scale;//meters cubed
        for ( double minorTick = 0;
              minorTick <= 0.002 * scale;//Go up to the max fluid volume of 2L
              minorTick += dMinorTick ) {
            //Width of the tick line in stage coordinates
            double lineWidth = 5.0;

            //Find where the tick mark should be in the vertical direction
            double viewY = transform.modelToViewY( beaker.getHeightForVolume( 1E-34 ) + beaker.getY() );

            //Add the tick mark
            final Line2D.Double line = new Line2D.Double( viewX - lineWidth, viewY, viewX, viewY );
            addChild( new PhetPPath( line, new BasicStroke( 2 ), white ) );
        }

        //Show the major tick marks, using formatting that suppresses the decimal point for round numbers, like 0.5L, 1L, etc.
        for ( Value tick : new Value[] { new Value( 0, "0" ), new Value( 0.001 * scale, "0" ), new Value( 0.002 * scale, "0" ) } ) {
            //Width of the tick mark line in stage coordinates
            double lineWidth = 10.0;

            //Location of the tick mark in the vertical direction
            double viewY = transform.modelToViewY( beaker.getHeightForVolume( tick.volume ) + beaker.getY() );

            //Create and add the tick mark
            final PhetPPath tickMark = new PhetPPath( new Line2D.Double( viewX - lineWidth, viewY, viewX, viewY ), new BasicStroke( 4 ), white );
            addChild( tickMark );

            //Create and add a tick mark label to the left of the tick mark, like "0.5L"
            addChild( new PText( tick.formatLiters() + "L" ) {{
                setTextPaint( white );
                setFont( new PhetFont( 26, true ) );
                setOffset( tickMark.getFullBounds().getX() - getFullBounds().getWidth(), tickMark.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
            }} );
        }
    }

    //Storage class for showing a tick mark value and its label
    private static class Value {
        private final double volume;
        private final String format;

        Value( double volume, String format ) {
            this.format = format;
            this.volume = volume;
        }

        //Converts the SI unit to liters and formats it according to the specified format (such as 0.5L, 1L,...)
        public String formatLiters() {
            return new DecimalFormat( format ).format( volume * 1000 );
        }
    }
}