package edu.colorado.phet.unfuddletool.util;

import java.util.Date;

public class UpdateThread extends Thread {
    public void run() {
        try {
            while ( true ) {
                // every minute
                Thread.sleep( 1000 * 60 );

                System.out.println( "Requesting latest activity from the server at " + ( new Date() ) );

                //SwingUtilities.invokeLater( new Runnable() {
                //    public void run() {
                Activity.requestRecentActivity( 4 );
                //    }
                //} );
            }
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
