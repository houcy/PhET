// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;

/**
 * Button for moving backwards through the kits
 *
 * @author Sam Reid
 */
public class BackButton extends ArrowButton {
    public BackButton() {
        super( flipX( RESOURCES.getImage( "gray-arrow.png" ) ) );
    }
}