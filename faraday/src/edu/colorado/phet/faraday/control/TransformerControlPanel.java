/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.module.TransformerModule;

/**
 * TransformerControlPanel is the control panel for the "Transformer" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TransformerControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private TransformerModule _module;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * <p>
     * The structure of the code (the way that code blocks are nested)
     * reflects the structure of the panel.
     * 
     * @param module the module that this control panel is associated with.
     */
    public TransformerControlPanel( TransformerModule module ) {

        super( module );

        _module = module;

        Font defaultFont = this.getFont();
        Font titleFont = new Font( defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );

        JPanel fillerPanel = new JPanel();
        {
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            // WORKAROUND: Filler to set consistent panel width
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
        }

        // Add panels to control panel.
        addFullWidth( fillerPanel );

        // Wire up event handling
        EventListener listener = new EventListener();
    }
    
    //----------------------------------------------------------------------------
    // Controller methods
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Event Handling
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class EventListener implements ActionListener, ChangeListener {
        
        /** Sole constructor */
        public EventListener() {}
        
        /**
         * ActionEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void actionPerformed( ActionEvent e ) {
            throw new IllegalArgumentException( "unexpected event: " + e );
        }
        
        /**
         * ChangeEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void stateChanged( ChangeEvent e ) {
            throw new IllegalArgumentException( "unexpected event: " + e );
        }
    }
    
}