/**
 * Class: StoveControlPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Oct 4, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PumpSpeciesSelectorPanel extends JPanel {
    private Color backgroundColor;

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
    }

    public void setBounds( int x, int y, int width, int height ) {
        super.setBounds( x, y, width, height );
    }

    public PumpSpeciesSelectorPanel( final IdealGasModule module ) {

        // This panel will be put on the ApparatusPanel, which has a null LayoutManager.
        // When a JPanel is added to a JPanel with a null LayoutManager, the nested panel
        // doesn't lay out properly if it is at all complicated. To get it to lay out properly,
        // it must be put into an intermediate JPanel with a simple layout manager (in this case
        // we use the default), and that intermediate panel is then added to the ApparatusPanel.
        JPanel basePanel = new JPanel();
        this.setOpaque( false );
        this.add( basePanel );

        JPanel buttonPanel = new SelectionPanel( module, module.getPump() );

        // Put the panel together
        basePanel.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = null;
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        basePanel.add( buttonPanel, gbc );
        Border border = new TitledBorder( new EtchedBorder( BevelBorder.RAISED,
                                                            new Color( 40, 20, 255 ),
                                                            Color.black ),
                                          SimStrings.get( "IdealGasControlPanel.Pump_Gas" ) );
        basePanel.setBorder( border );

        backgroundColor = new Color( 240, 230, 255 );
        basePanel.setBackground( backgroundColor );
        buttonPanel.setBackground( backgroundColor );
        basePanel.getLayout().layoutContainer( this );

        revalidate();
        repaint();
    }

    class SelectionPanel extends JPanel {
        private IdealGasModule module;
        private GasSource gasSource;
        private JRadioButton heavySpeciesRB;
        private JRadioButton lightSpeciesRB;


        SelectionPanel( final IdealGasModule module, final GasSource gasSource ) {
            this.module = module;
            this.gasSource = gasSource;

            // Radio buttons
            makeRadioButtons();

            // Lay out the panel
            setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 0, 0, 0 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                             GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                             insets, 0, 0 );
            add( heavySpeciesRB, gbc );
            add( lightSpeciesRB, gbc );
        }

        /**
         * Sets up the radio buttons for selecting a species
         */
        private void makeRadioButtons() {
            heavySpeciesRB = new JRadioButton( SimStrings.get( "Common.Heavy_Species" ) );
            heavySpeciesRB.setForeground( Color.blue );
            heavySpeciesRB.setBackground( backgroundColor );
            lightSpeciesRB = new JRadioButton( SimStrings.get( "Common.Light_Species" ) );
            lightSpeciesRB.setForeground( Color.red );
            lightSpeciesRB.setBackground( backgroundColor );
            final ButtonGroup speciesGroup = new ButtonGroup();
            speciesGroup.add( heavySpeciesRB );
            speciesGroup.add( lightSpeciesRB );

            heavySpeciesRB.setSelected( true );
            heavySpeciesRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    if( heavySpeciesRB.isSelected() ) {
                        gasSource.setCurrentGasSpecies( HeavySpecies.class );
                        module.setCurrentPumpImage( Color.blue );
                    }
                }
            } );

            lightSpeciesRB.addActionListener( new ActionListener() {
                public void actionPerformed
                        ( ActionEvent
                        event ) {
                    if( lightSpeciesRB.isSelected() ) {
                        gasSource.setCurrentGasSpecies( LightSpecies.class );
                        module.setCurrentPumpImage( Color.red );
                    }
                }
            } );
        }
    }    // class SelectionPanel
}
