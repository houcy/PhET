/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.List;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A Piccolo2D node that monitors that average atomic mass of a set of
 * isotopes in a model and graphically displays it.
 *
 * @author John Blanco
 */
public class AverageAtomicMassIndicator extends PNode {

    private static double INDICATOR_WIDTH = 300; // In screen units, which is close to pixels.

    public AverageAtomicMassIndicator( final IsotopeMixturesModel model ){

        // Add the title.
        // TODO: i18n
        PText title = new PText("Average Atomic Mass"){{
            setFont( new PhetFont(20, true) );
        }};
        addChild( title );

        // Add the bar that makes up "spine" of the indicator.
        final double barOffsetY = title.getFullBoundsReference().getMaxY() + 40;
        DoubleGeneralPath barShape = new DoubleGeneralPath( 0, 0 );
        barShape.lineTo( INDICATOR_WIDTH, 0 );
        PNode barNode = new PhetPPath( barShape.getGeneralPath(), new BasicStroke(3), Color.BLACK ){{
            setOffset( 0, barOffsetY );
        }};
        addChild( barNode );

        // Add the layer where the tick marks will be maintained.
        final PNode tickMarkLayer = new PNode();
        addChild( tickMarkLayer );

        // Listen for changes to the list of possible isotopes and update the
        // tick marks when changes occur.
        model.getPossibleIsotopesProperty().addObserver( new SimpleObserver() {
            public void update() {
                tickMarkLayer.removeAllChildren();
                List< ImmutableAtom > possibleIsotopeList = model.getPossibleIsotopesProperty().getValue();
                double interTickSpacingX = INDICATOR_WIDTH / possibleIsotopeList.size();
                double tickMarkStartOffsetX = interTickSpacingX / 2;
                int tickMarkCount = 0;
                for ( ImmutableAtom isotope : model.getPossibleIsotopesProperty().getValue() ){
                    IsotopeTickMark tickMark = new IsotopeTickMark( isotope );
                    tickMark.setOffset( tickMarkStartOffsetX + interTickSpacingX * tickMarkCount, barOffsetY );
                    tickMarkCount++;
                    tickMarkLayer.addChild( tickMark );
                }
            }
        });
    }

    /**
     * Convenience class for creating tick marks.
     */
    private static class IsotopeTickMark extends PNode {

        // Constants that control overall appearance, tweak as needed.
        private static final double TICK_MARK_HEIGHT = 10;
        private static final Stroke TICK_MARK_STROKE = new BasicStroke( 5 );
        private static final Font LABEL_FONT = new PhetFont(18);

        public IsotopeTickMark( ImmutableAtom isotopeConfig ){
            // Create the tick mark itself.  It is positioned such that
            // (0,0) is the center of the mark.
            DoubleGeneralPath shape = new DoubleGeneralPath( 0, -TICK_MARK_HEIGHT / 2 );
            shape.lineTo( 0, TICK_MARK_HEIGHT / 2 );
            addChild( new PhetPPath(shape.getGeneralPath(), TICK_MARK_STROKE, Color.BLACK));
            // Create the label that goes above the tick mark.
            HTMLNode label = new HTMLNode( "<html><sup>" + isotopeConfig.getMassNumber() + "</sup>" + isotopeConfig.getSymbol() + "</html>" ){{
                setFont( LABEL_FONT );
                setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height - TICK_MARK_HEIGHT / 2 );
            }};
            addChild( label );
        }
    }
}
