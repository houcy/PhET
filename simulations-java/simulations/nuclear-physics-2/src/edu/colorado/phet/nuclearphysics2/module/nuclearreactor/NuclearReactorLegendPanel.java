/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.view.LabeledNucleusNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class displays the legend for the Nuclear Reactor tab.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class NuclearReactorLegendPanel extends JPanel {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public NuclearReactorLegendPanel() {
        
        // Add the border around the legend.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.LegendBorder" ),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridLayout(0, 2) );

        // Add the images and labels for the simple portion of the legend.
        
        addLegendItem( "Neutron.png", NuclearPhysics2Strings.NEUTRON_LEGEND_LABEL, 12 );
        
        // Add the Uranium 235 nucleus to the legend.
        // Add the Uranium 235 nucleus to the legend.
        PNode labeledU235Nucleus = new LabeledNucleusNode("Uranium Nucleus Small.png",
                NuclearPhysics2Strings.URANIUM_235_ISOTOPE_NUMBER, 
                NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL, 
                NuclearPhysics2Constants.URANIUM_235_LABEL_COLOR );
        
        Image u235Image = labeledU235Nucleus.toImage();
        ImageIcon icon = new ImageIcon(u235Image);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.URANIUM_235_LEGEND_LABEL ) );
    }
    
    /**
     * This method adds simple legend items, i.e. those that only include an
     * image and a label, to the legend.
     * 
     * @param imageName
     * @param labelName
     * @param width
     */
    private void addLegendItem( String imageName, String label, int width ) {
        Image im = NuclearPhysics2Resources.getImage( imageName );
        ImageIcon icon = new ImageIcon(im.getScaledInstance( width, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(icon));
        add(new JLabel( label ));
    }
}
