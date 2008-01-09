/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.control.*;
import edu.colorado.phet.glaciers.model.GlaciersClock;


public class AdvancedControlPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color( 180, 158, 134 ); // tan
    private static final Font TITLE_FONT = new PhetDefaultFont( PhetDefaultFont.getDefaultFontSize(), true /* bold */ );
    private static final Font CONTROL_FONT = new PhetDefaultFont();

    public AdvancedControlPanel( GlaciersClock clock ) {
        super();
        
        ViewControlPanel viewControlPanel = new ViewControlPanel( TITLE_FONT, CONTROL_FONT );
        AdvancedClimateControlPanel climateControlPanel = new AdvancedClimateControlPanel( TITLE_FONT, CONTROL_FONT );
        GraphsControlPanel graphsControlPanel = new GraphsControlPanel(TITLE_FONT, CONTROL_FONT );
        GlaciersClockControlPanel clockControlPanel = new GlaciersClockControlPanel( clock );
        MiscControlPanel miscControlPanel = new MiscControlPanel();
        
        int row;
        int column;
        
        JPanel topPanel = new JPanel();
        EasyGridBagLayout topLayout = new EasyGridBagLayout( topPanel );
        topPanel.setLayout( topLayout  );
        row = 0;
        column = 0;
        topLayout.addFilledComponent( viewControlPanel, row, column++, GridBagConstraints.VERTICAL );
        topLayout.addFilledComponent( climateControlPanel, row, column++, GridBagConstraints.VERTICAL  );
        topLayout.addFilledComponent( graphsControlPanel, row, column++, GridBagConstraints.VERTICAL  );
        
        JPanel bottomPanel = new JPanel();
        EasyGridBagLayout bottomLayout = new EasyGridBagLayout( bottomPanel );
        bottomPanel.setLayout( bottomLayout );
        row = 0;
        column = 0;
        bottomLayout.addAnchoredComponent( clockControlPanel, row, column++, GridBagConstraints.WEST );
        bottomLayout.addComponent( Box.createHorizontalStrut( 10 ), row, column++ );
        bottomLayout.addFilledComponent( new JSeparator( SwingConstants.VERTICAL ), row, column++, GridBagConstraints.VERTICAL );
        bottomLayout.addComponent( Box.createHorizontalStrut( 10 ), row, column++ );
        bottomLayout.addAnchoredComponent( miscControlPanel, row, column++, GridBagConstraints.EAST );
        
        EasyGridBagLayout thisLayout = new EasyGridBagLayout( this );
        setLayout( thisLayout );
        row = 0;
        column = 0;
        thisLayout.addComponent( topPanel, row++, column );
        thisLayout.addComponent( bottomPanel, row++, column );
        
        Class[] excludedClasses = { ViewControlPanel.class, AdvancedClimateControlPanel.class, GraphsControlPanel.class, JTextComponent.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
    }
}
