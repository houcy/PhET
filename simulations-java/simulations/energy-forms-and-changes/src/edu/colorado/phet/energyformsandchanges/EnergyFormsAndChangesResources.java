// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyformsandchanges;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources (images and translated strings) for "EnergyFormsAndChanges" are loaded eagerly to make sure everything exists on sim startup, see #2967.
 * Automatically generated by edu.colorado.phet.buildtools.preprocessor.ResourceGenerator
 */
public class EnergyFormsAndChangesResources {
    public static final String PROJECT_NAME = "energy-forms-and-changes";
    public static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    //Strings
    public static class Strings {
        public static final String ENERGY_SYSTEMS = RESOURCES.getLocalizedString( "energySystems" );
        public static final String INTRO = RESOURCES.getLocalizedString( "intro" );
    }

    //Images
    public static class Images {
        public static final BufferedImage FIRST_TAB_DRAWING = RESOURCES.getImage( "first-tab-drawing.png" );
        public static final BufferedImage SECOND_TAB_DRAWING = RESOURCES.getImage( "second-tab-drawing.png" );
        public static final BufferedImage SHELF_LONG = RESOURCES.getImage( "shelf_long.png" );
        public static final BufferedImage SHELF_SHORT = RESOURCES.getImage( "shelf_short.png" );
    }
}