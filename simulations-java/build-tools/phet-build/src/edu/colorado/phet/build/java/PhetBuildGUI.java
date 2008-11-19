package edu.colorado.phet.build.java;

import java.io.File;

import javax.swing.*;

/**
 * Provides a front-end user interface for building and deploying phet's java simulations.
 * This entry point has no ant xml dependencies.
 */
public class PhetBuildGUI {
    private JFrame frame = new JFrame();

    public PhetBuildGUI( File baseDir ) {

        this.frame = new JFrame( "PhET Build" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetBuildGUIPanel panel = new PhetBuildGUIPanel( baseDir );
        frame.setContentPane( panel );

        frame.setSize( 1024, 768 );
    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "Usage: args[0]=basedir" );
        }
        else {
            File basedir = new File( args[0] );
            new PhetBuildGUI( basedir ).start();
        }
    }
}