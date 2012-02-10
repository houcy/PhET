// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.tests.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.bucket.BucketNode;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationControlPanel;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends AbstractFractionsCanvas {

    public FractionsIntroCanvas( final FractionsIntroModel model ) {

        final RepresentationControlPanel representationControlPanel = new RepresentationControlPanel( model.representation ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() / 2, INSET );
        }};
        addChild( representationControlPanel );

        final RepresentationArea representationArea = new RepresentationArea( model.representation, model.numerator, model.denominator, model.containerState ) {{
            setOffset( INSET, representationControlPanel.getFullBounds().getMaxY() + 100 );
        }};
        addChild( representationArea );

        addChild( new PieSetNode( model.pieSetState ) );

        ZeroOffsetNode fractionEqualityPanel = new ZeroOffsetNode( new FractionEqualityPanel( model ) ) {{
            setOffset( 35, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( fractionEqualityPanel );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
        }};
        addChild( resetAllButtonNode );

        addChild( new BucketNode( STAGE_SIZE, model, representationArea ) {{
            //Hide the bucket for number line
            model.representation.valueEquals( ChosenRepresentation.NUMBER_LINE ).addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean numberLine ) {
                    setVisible( !numberLine );
                }
            } );
        }} );

        resetAllButtonNode.setOffset( STAGE_SIZE.width - resetAllButtonNode.getFullBounds().getWidth() - INSET, STAGE_SIZE.height - resetAllButtonNode.getFullBounds().getHeight() - INSET );
    }
}