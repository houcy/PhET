// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.@packagename@;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources (images and translated strings) for "@classname@" are loaded eagerly to make sure everything exists on sim startup, see #2967.
 * Automatically generated by @generator@
 */
public class @fullclassname@ {
    public static final String NAME = "@simname@";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    //Strings
    public static class Strings {
@strings@    }

    //Images
    public static class Images {
@images@    }
}