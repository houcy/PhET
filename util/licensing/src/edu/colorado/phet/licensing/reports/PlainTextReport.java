package edu.colorado.phet.licensing.reports;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.licensing.SimInfo;
import edu.colorado.phet.licensing.TrunkDirectory;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class PlainTextReport {

    // intended to be called via main
    private PlainTextReport() {
    }

    public static void main( String[] args ) throws IOException {
        if ( args.length != 1 ) {
            System.out.println( "usage: " + PlainTextReport.class.getName() + " absolute_path_to_trunk" );
            System.exit( 1 );
        }
        File trunk = new TrunkDirectory( args[0] );
        new PlainTextReport().start( trunk );
    }

    private void start( File trunk ) throws IOException {
        System.out.println( "PhET Java Software Dependencies\n" + new Date() + "\n" );

        File baseDir = new File( trunk, BuildToolsPaths.SIMULATIONS_JAVA );
        String[] simNames = PhetProject.getSimNames( baseDir );
        for ( int i = 0; i < simNames.length; i++ ) {
            visitSim( trunk, simNames[i] );
        }
    }

    private void visitSim( File trunk, String simName ) throws IOException {
        SimInfo sim = SimInfo.getSimInfo( trunk, simName );
        System.out.println( sim.getIssues() );
    }
}
