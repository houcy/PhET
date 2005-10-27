/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.ec3.common.LucidaSansFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 6, 2004
 * Time: 3:21:19 PM
 * Copyright (c) Nov 6, 2004 by Sam Reid
 */
public class TimeSeriesPlaybackPanel extends JPanel {
    private JButton record;
    private JButton play;
    private JButton pause;
    private JButton rewind;
    private JButton slowMotion;
    private JButton clear;
    private TimeSeriesModel timeSeriesModel;
    private JButton live;

    private JButton createButton( String name, String icon ) {
        JButton button = null;
        if( icon != null ) {
            button = new JButton( SimStrings.get( name ), new ImageIcon( loadImage( icon ) ) );
        }
        else {
            button = new JButton( SimStrings.get( name ) );
        }
        if( Toolkit.getDefaultToolkit().getScreenSize().width <= 1024 ) {
            button.setFont( new LucidaSansFont( 9, true ) );
        }
        return button;
    }


    public TimeSeriesPlaybackPanel( final TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;

        live = createButton( "Live", "images/icons/java/media/Play24.gif" );
        live.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startLiveMode();
            }
        } );
        record = createButton( "Record", "images/icons/java/media/Movie24.gif" );
        record.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startRecording();
            }
        } );

        pause = createButton( "Pause", "images/icons/java/media/Pause24.gif" );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //pausing from playback leaves it alone
                timeSeriesModel.setPaused( true );
            }
        } );
        play = createButton( "Playback", "images/icons/java/media/Forward24.gif" );
        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( 1.0 );
            }
        } );

        rewind = createButton( "Rewind", "images/icons/java/media/Rewind24.gif" );
        rewind.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
                timeSeriesModel.setPaused( true );
            }
        } );

        slowMotion = createButton( "Slow Motion", "images/icons/java/media/StepForward24.gif" );
        slowMotion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( .4 );
            }
        } );

//        clear = createButton( "Clear", "images/icons/java/media/Stop24.gif" );
        clear = createButton( "Clear", null );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.confirmAndApplyReset();
            }
        } );
        add( live );
        add( record );
        add( play );
        add( slowMotion );
        add( pause );
        add( rewind );
        add( clear );

        TimeSeriesModelListener timeListener = new TimeSeriesModelListener() {
            public void liveModeStarted() {
                setButtons( false, true, false, false, true, false );
            }

            public void recordingStarted() {
                setButtons( false, false, false, false, true, false );
            }

            public void recordingPaused() {
                setButtons( true, true, true, true, false, true );
            }

            public void recordingFinished() {
                setButtons( true, false, true, true, false, true );
            }

            public void playbackFinished() {
                setButtons( true, true, false, false, false, true );
            }

            public void playbackStarted() {
                setButtons( false, false, false, false, true, true );
            }

            public void playbackPaused() {
                setButtons( true, true, true, true, false, true );
            }

            public void reset() {
                setButtons( true, true, false, false, false, false );
            }

            public void rewind() {
                setButtons( true, true, true, true, false, false );
            }
        };
        timeSeriesModel.addListener( timeListener );
        timeListener.liveModeStarted();
    }

    private BufferedImage loadImage( String s ) {
        try {
            return new ImageLoader().loadImage( s );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    private void setButtons( boolean liveBtn, boolean recordBtn, boolean playBtn, boolean slowBtn, boolean pauseBtn, boolean rewindBtn ) {
        live.setEnabled( liveBtn );
        record.setEnabled( recordBtn );
        play.setEnabled( playBtn );
        slowMotion.setEnabled( slowBtn );
        pause.setEnabled( pauseBtn );
        rewind.setEnabled( rewindBtn );
    }

}
