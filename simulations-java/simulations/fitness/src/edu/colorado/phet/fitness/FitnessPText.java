package edu.colorado.phet.fitness;

import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Created by: Sam
 * May 21, 2008 at 10:53:52 AM
 */
public class FitnessPText extends PText {
    public FitnessPText() {
        setFont( new PhetFont( ) );
    }

    public FitnessPText( String aText ) {
        super( aText );
        setFont( new PhetFont( ) );
    }
}
