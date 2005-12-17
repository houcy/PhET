/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 10:12:30 AM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class ConfigureHorizontalSlitPanel extends VerticalLayoutPanel {
    private HorizontalDoubleSlit slit;
    private boolean showPotentialValue;

    public ConfigureHorizontalSlitPanel( HorizontalDoubleSlit doubleSlitPotential ) {
        this( doubleSlitPotential, false );
    }

    public ConfigureHorizontalSlitPanel( HorizontalDoubleSlit doubleSlitPotential, boolean showPotentialValue ) {
        this.slit = doubleSlitPotential;
        this.showPotentialValue = showPotentialValue;
        addControls( slit );
    }

    private void addControls( final HorizontalDoubleSlit slit ) {
        double pow = slit.getPotential();
        if( showPotentialValue ) {
            SlitSpinner potentialSpinner = new SlitSpinner( "Potential",
                                                            new SpinnerNumberModel( pow, 0, Double.POSITIVE_INFINITY, pow / 10 ),
                                                            new ChangeHandler() {
                                                                public void valueChanged( Number value ) {
                                                                    slit.setPotential( value.doubleValue() );
                                                                }
                                                            } );
            addFullWidth( potentialSpinner );
        }
        SlitSpinner sizeSpinner = new SlitSpinner( "Size", new SpinnerNumberModel( slit.getSlitSize(), 0, slit.getGridWidth() / 2, 1 ), new ChangeHandler() {
            public void valueChanged( Number value ) {
                slit.setSlitSize( value.intValue() );
            }
        } );
        addFullWidth( sizeSpinner );

        SlitSpinner sepSpinner = new SlitSpinner( "Separation", new SpinnerNumberModel( slit.getSlitSeparation(), 0, slit.getGridHeight(), 1 ), new ChangeHandler() {
            public void valueChanged( Number value ) {
                slit.setSlitSeparation( value.intValue() );
            }
        } );
        addFullWidth( sepSpinner );

        SlitSpinner depth = new SlitSpinner( "Depth", new SpinnerNumberModel( slit.getHeight(), 0, slit.getGridHeight(), 1 ), new ChangeHandler() {
            public void valueChanged( Number value ) {
                slit.setHeight( value.intValue() );
            }
        } );
        addFullWidth( depth );

        SlitSpinner yval = new SlitSpinner( "Y", new SpinnerNumberModel( slit.getY(), 0, slit.getGridHeight(), 1 ), new ChangeHandler() {
            public void valueChanged( Number value ) {
                slit.setY( value.intValue() );
            }
        } );
        addFullWidth( yval );
    }

    private static interface ChangeHandler {
        public void valueChanged( Number value );
    }

    private static class SlitSpinner extends JPanel {
        public JSpinner spinner;

        public SlitSpinner( String name, SpinnerModel spinnerModel, final ChangeHandler changeHandler ) {
            spinner = new JSpinner( spinnerModel );
            spinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    Number val = (Number)spinner.getValue();
                    changeHandler.valueChanged( val );
                }
            } );
//            setBorder( BorderFactory.createTitledBorder( name ) );
            spinner.setPreferredSize( new Dimension( 150, spinner.getPreferredSize().height ) );
            setLayout( new BorderLayout() );
            JLabel label = new JLabel( name );
            add( label, BorderLayout.WEST );
            add( spinner, BorderLayout.EAST );
        }
    }
}
