// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.*;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class BuildAMoleculeConstants {

    /* Not intended for instantiation. */
    private BuildAMoleculeConstants() {
    }

    public static final String PROJECT_NAME = "build-a-molecule";

    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 ); // the size of our "view" coordinate area

    /*---------------------------------------------------------------------------*
    * colors
    *----------------------------------------------------------------------------*/

    public static final Color CANVAS_BACKGROUND_COLOR = new Color( 149, 188, 228 );
    public static final Color MOLECULE_COLLECTION_BACKGROUND = new Color( 202, 219, 42 );
    public static final Color MOLECULE_COLLECTION_BORDER = Color.BLACK;
    public static final Color MOLECULE_COLLECTION_BOX_HIGHLIGHT = Color.YELLOW;
}
