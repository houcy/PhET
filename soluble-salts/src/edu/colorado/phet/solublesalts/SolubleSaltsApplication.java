/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.util.DebugMenu;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.view.IonGraphic;
import edu.colorado.phet.piccolo.PhetPCanvas;
//import edu.colorado.phet.piccolo.PhetPCanvas;
//import edu.colorado.phet.piccolo.util.PMouseTracker;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * SolubleSaltsApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsApplication extends PhetApplication {


    private static AbstractClock CLOCK = new SwingTimerClock( SolubleSaltsConfig.DT, SolubleSaltsConfig.FPS, AbstractClock.FRAMES_PER_SECOND );

    public SolubleSaltsApplication( String[] args ) {
        super( args,
               SolubleSaltsConfig.TITLE,
               SolubleSaltsConfig.DESCRIPTION,
               SolubleSaltsConfig.VERSION,
               CLOCK,
               true,
               new FrameSetup.CenteredWithSize( 1000, 740 ) );

        Module module = new SolubleSaltsModule( CLOCK );
        setModules( new Module[]{module} );

        setUpDebugMenu();
    }

    private void setUpDebugMenu() {
        DebugMenu debugMenu = getPhetFrame().getDebugMenu();
        if( debugMenu != null ) {
            final JCheckBoxMenuItem showBondIndicatorMI = new JCheckBoxMenuItem( "Show bond indicators" );
            debugMenu.add( showBondIndicatorMI );
            showBondIndicatorMI.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    IonGraphic.showBondIndicators( showBondIndicatorMI.isSelected() );
                }
            } );
        }

    }

    public static void main( String[] args ) {
        SimStrings.init( args, SolubleSaltsConfig.STRINGS_BUNDLE_NAME );
        PhetApplication app = new SolubleSaltsApplication( args );
        app.startApplication();


        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( "-m" ) ) {
                PhetPCanvas simPanel = (PhetPCanvas)app.getModuleManager().getActiveModule().getSimulationPanel();
                if( simPanel != null ) {
//                    PMouseTracker mouseTracker = new PMouseTracker( simPanel );
//                    simPanel.addWorldChild( mouseTracker );
                }
            }
            if( arg.equals( "-b" )) {
                IonGraphic.showBondIndicators( true );
            }
            if( arg.equals( "-t" )) {
                SolubleSaltsConfig.LATTICE = SolubleSaltsConfig.twoToOneLattice;
            }
        }

    }

}
