// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.graphtheline.GTL_Challenge;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * View component for a "Graph the Line" (GTL) challenge.
 * Given an equation in slope-intercept (SI) form, graph the line by manipulating the Intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_SI_Intercept_ChallengeNode extends GTL_SI_ChallengeNode {

    public GTL_SI_Intercept_ChallengeNode( final LineGameModel model, GTL_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    // Creates the graph portion of the view.
    @Override protected GTL_GraphNode createGraphNode( GTL_Challenge challenge ) {
        return new ThisGraphNode( challenge );
    }

    // Graph for this challenge
    private static class ThisGraphNode extends GTL_SI_GraphNode {

        private final LineNode answerNode;
        private final LineManipulatorNode interceptManipulatorNode;

        public ThisGraphNode( final GTL_Challenge challenge ) {
            super( challenge );

            // parent for the guess node, to maintain rendering order
            final PNode guessNodeParent = new PComposite();

            // the correct answer, initially hidden
            answerNode = createAnswerLineNode( challenge.answer, challenge.graph, challenge.mvt );
            answerNode.setEquationVisible( false );
            answerNode.setVisible( false );

            // point (y intercept) manipulator
            final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );
            interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT );
            interceptManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                                 interceptManipulatorNode, challenge.mvt, challenge.guess,
                                                                                 new Property<DoubleRange>( new DoubleRange( 0, 0 ) ),
                                                                                 new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ),
                                                                                 true /* constantSlope */ ) );
            // Rendering order
            addLineNode( guessNodeParent );
            addLineNode( answerNode );
            addManipulatorNode( interceptManipulatorNode );

            // Show the user's current guess
            challenge.guess.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // draw the line
                    guessNodeParent.removeAllChildren();
                    LineNode guessNode = createGuessLineNode( line, challenge.graph, challenge.mvt );
                    guessNode.setEquationVisible( false );
                    guessNodeParent.addChild( guessNode );

                    // move the manipulator
                    interceptManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
        }
    }
}
