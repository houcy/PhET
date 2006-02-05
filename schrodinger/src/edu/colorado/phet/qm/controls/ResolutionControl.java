/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jul 29, 2005
 * Time: 9:34:53 AM
 * Copyright (c) Jul 29, 2005 by Sam Reid
 */

public class ResolutionControl extends AdvancedPanel {
    public static final int DEFAULT_WAVE_SIZE = 60;
    private SchrodingerModule schrodingerModule;

    public ResolutionControl( SchrodingerModule schrodingerModule ) {
        super( "Resolution>>", "Resolution<<" );
        this.schrodingerModule = schrodingerModule;

        JLabel screenSizeLabel = new JLabel( "Grid Resolution" );
        addControl( screenSizeLabel );

        final JSpinner screenSize = new JSpinner( new SpinnerNumberModel( DEFAULT_WAVE_SIZE, 10, 200, 5 ) );
        getSchrodingerModule().setWaveSize( DEFAULT_WAVE_SIZE );

        screenSize.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer value = (Integer)screenSize.getValue();
                getSchrodingerModule().setWaveSize( value.intValue() );
            }
        } );
        addControl( screenSize );

        JLabel numSkip = new JLabel( "Time Step" );
        addControl( numSkip );
        final JSpinner frameSkip = new JSpinner( new SpinnerNumberModel( SchrodingerScreenNode.numIterationsBetwenScreenUpdate, 1, 20, 1 ) );
        frameSkip.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer val = (Integer)frameSkip.getValue();
                SchrodingerScreenNode.numIterationsBetwenScreenUpdate = val.intValue();
            }
        } );
        addControl( frameSkip );
    }

    private SchrodingerModule getSchrodingerModule() {
        return schrodingerModule;
    }
}
