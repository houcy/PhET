// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.MPImages;

/**
 * Cursor handler that shows a rotation cursor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RotateCursorHandler extends CursorHandler {

    private static final Point HOT_SPOT = new Point( MPImages.ROTATE_CURSOR.getWidth() / 2, MPImages.ROTATE_CURSOR.getHeight() / 2 );
    public static final Cursor CURSOR = Toolkit.getDefaultToolkit().createCustomCursor( MPImages.ROTATE_CURSOR, HOT_SPOT, "rotate" );

    public RotateCursorHandler() {
        super( CURSOR );
    }
}
