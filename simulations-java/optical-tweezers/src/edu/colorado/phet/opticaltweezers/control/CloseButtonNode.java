/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * CloseButtonNode is a node that displays a close button.
 * ActionListeners can be registered with the close button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CloseButtonNode extends PhetPNode {

    private JButton _closeButton;

    public CloseButtonNode() {
        _closeButton = new CloseButton();
        PSwing closeButtonWrapper = new PSwing( _closeButton );
        closeButtonWrapper.addInputEventListener( new CursorHandler() );
        addChild( closeButtonWrapper );
    }

    public void addActionListener( ActionListener listener ) {
        _closeButton.addActionListener( listener );
    }

    public void removeActionListener( ActionListener listener ) {
        _closeButton.removeActionListener( listener );
    }

    public static class CloseButton extends JButton {

        public CloseButton() {
            super();
            Icon closeIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_CLOSE_BUTTON ) );
            setIcon( closeIcon );
            setOpaque( false );
            setMargin( new Insets( 0, 0, 0, 0 ) );
        }
    }
}
