// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.PTP_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for all "Place the Points" (PTP) challenges.
 * In this challenge, the user is given an equation and must place 3 points on a graph to make the line.
 * If the 3 points do not form a line, the guess line will be null.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PTP_Challenge extends MatchChallenge {

    public final Property<Vector2D> p1, p2, p3; // the 3 points that the user places

    public PTP_Challenge( String description, Line answer, LineForm lineForm, IntegerRange xRange, IntegerRange yRange ) {
        super( Strings.PLACE_THE_POINTS, description,
               answer, Line.Y_EQUALS_X_LINE,
               lineForm, ManipulationMode.THREE_POINTS,
               xRange, yRange,
               new Point2D.Double( 700, 300 ), // origin offset
               new Vector2D( xRange.getMin() + ( 0.65 * xRange.getLength() ), yRange.getMin() - 1 ), // point tool location 1
               new Vector2D( xRange.getMin() + ( 0.95 * xRange.getLength() ), yRange.getMin() - 4 ) ); // point tool location 2

        // initial points do not form a line
        this.p1 = new Property<Vector2D>( new Vector2D( -3, 2 ) );
        this.p2 = new Property<Vector2D>( new Vector2D( 0, 0 ) );
        this.p3 = new Property<Vector2D>( new Vector2D( 3, 2 ) );

        // update the guess when the points change
        final RichSimpleObserver pointObserver = new RichSimpleObserver() {
            public void update() {
                updateGuess();
            }
        };
        pointObserver.observe( p1, p2, p3 );
    }

    @Override public void reset() {
        super.reset();
        p1.reset();
        p2.reset();
        p3.reset();
    }

    // Updates the guess to match the points.
    private void updateGuess() {
        Line line = new Line( p1.get().x, p1.get().y, p2.get().x, p2.get().y, LineGameConstants.GUESS_COLOR );
        if ( line.onLine( p3.get() ) ) {
            guess.set( line );
            if ( isCorrect() ) {
                guess.set( answer );
            }
        }
        else {
            guess.set( null );
        }
    }

    // Updates the collection of lines that are "seen" by the point tools.
    @Override protected void updatePointToolLines() {
        pointToolLines.clear();
        if ( answerVisible ) {
            pointToolLines.add( answer );
        }
        if ( guess.get() != null ) {
            pointToolLines.add( guess.get() );
        }
    }

    // Creates the view for this challenge.
    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new PTP_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}
