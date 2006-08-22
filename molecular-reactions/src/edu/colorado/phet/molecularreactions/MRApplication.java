package edu.colorado.phet.molecularreactions;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.MRConfig;

/**
 * edu.colorado.phet.molecularreactions.MRApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRApplication extends PhetApplication {

    public MRApplication( String[] args ) {
        super( args, SimStrings.get( "Application.title" ),
               SimStrings.get( "Application.description" ),
               MRConfig.VERSION,
               new FrameSetup.CenteredWithSize( 1000, 600 ) );

        addModule( new MRModule() );
    }


    public static void main( String[] args ) {
        SimStrings.init( args, MRConfig.LOCALIZATION_BUNDLE );

        MRApplication application = new MRApplication( args );
        application.startApplication();
    }
}
