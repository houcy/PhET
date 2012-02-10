// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources (images and translated strings) for "GeneExpressionBasics" are loaded eagerly to make sure everything exists on sim startup, see #2967.
 * Automatically generated by edu.colorado.phet.buildtools.preprocessor.ResourceGenerator
 */
public class GeneExpressionBasicsResources {
    public static final String PROJECT_NAME = "gene-expression-basics";
    public static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    //Strings
    public static class Strings {

    }

    //Images
    public static class Images {
        public static final BufferedImage ECOLI = RESOURCES.getImage( "ecoli.jpg" );
        public static final BufferedImage ECOLI_IMAGE = RESOURCES.getImage( "ecoli_image.gif" );
        public static final BufferedImage GRAY_ARROW = RESOURCES.getImage( "gray-arrow.png" );
        public static final BufferedImage SECOND_TAB_STATIC_PICTURE_VERSION_2 = RESOURCES.getImage( "second-tab-static-picture-version-2.png" );
        public static final BufferedImage SECOND_TAB_STATIC_PICTURE = RESOURCES.getImage( "second-tab-static-picture.png" );
    }
}