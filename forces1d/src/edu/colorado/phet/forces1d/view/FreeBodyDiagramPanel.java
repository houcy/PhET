/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.WiggleMe;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDevice;
import edu.colorado.phet.forces1d.model.Force1DModel;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 * Copyright (c) Jan 16, 2005 by Sam Reid
 */

public class FreeBodyDiagramPanel {

    private FreeBodyDiagram freeBodyDiagram;
    private ApparatusPanel2 fbdPanel;
    private WiggleMe fbdWiggleMe;
    private PlotDevice forcePlotDevice;
    private Force1DModule module;
//    private JPanel fakePanel;

    public FreeBodyDiagramPanel( final Force1DModule module ) {
        this.module = module;
        fbdPanel = new ApparatusPanel2( module.getModel(), module.getClock() ) {
            protected void init( BaseModel model, AbstractClock clock ) {
                super.init( model, clock );
                setAutoPaint( false );
            }
        };
        fbdPanel.setLayout( new BoxLayout( fbdPanel, BoxLayout.Y_AXIS ) );
//        fbdPanel.setau
        fbdPanel.addGraphicsSetup( new BasicGraphicsSetup() );
        int fbdWidth = 180;
        if( Toolkit.getDefaultToolkit().getScreenSize().width < 1280 ) {
//            fbdWidth = 155;
            fbdWidth = 157;
        }
        fbdPanel.setPreferredSize( new Dimension( fbdWidth, fbdWidth ) );
        freeBodyDiagram = new FreeBodyDiagram( fbdPanel, module );
        freeBodyDiagram.setComponent( fbdPanel );//todo is this necessary?
        fbdPanel.addGraphic( freeBodyDiagram );
//        fbdPanel.setBackground( Color.green );

        int fbdInset = 3;
        freeBodyDiagram.setBounds( fbdInset, fbdInset, fbdWidth - 2 * fbdInset, fbdWidth - 2 * fbdInset );

        WiggleMe.Target target = new WiggleMe.Target() {
            public Point getLocation() {
                return new Point( fbdPanel.getWidth() - 10, fbdPanel.getHeight() / 2 - fbdWiggleMe.getHeight() );
            }

            public int getHeight() {
                return 0;
            }
        };
        fbdWiggleMe = new WiggleMe( fbdPanel, module.getClock(), "Click to set Force", target );
        fbdWiggleMe.setArrowColor( new Color( 0, 30, 240, 128 ) );
        fbdWiggleMe.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        fbdWiggleMe.setArrowDirection( 0, 40 );
        fbdWiggleMe.setAmplitude( 10 );
        fbdWiggleMe.setFrequency( 5.0 );
        fbdWiggleMe.setOscillationAxis( new Vector2D.Double( 1, 0 ) );
        fbdWiggleMe.setVisible( false );

        module.getForceModel().addListener( new Force1DModel.Listener() {
            public void appliedForceChanged() {
                fbdWiggleMe.setVisible( false );
                fbdPanel.removeGraphic( fbdWiggleMe );
            }

            public void gravityChanged() {
            }
        } );
        forcePlotDevice = module.getForcePanel().getPlotDevice();
        MouseInputAdapter listener = new MouseInputAdapter() {
            public void mousePressed( MouseEvent e ) {
                forcePlotDevice.getPlotDeviceModel().setRecordMode();
                forcePlotDevice.getPlotDeviceModel().setPaused( false );
            }
        };
        freeBodyDiagram.addMouseInputListener( listener );
//        RepaintDebugGraphic.enable( fbdPanel, module.getClock() );//TODO optimize.


    }

    public FreeBodyDiagram getFreeBodyDiagram() {
        return freeBodyDiagram;
    }

    public ApparatusPanel2 getFBDPanel() {
        return fbdPanel;
//        return fakePanel;
    }

    public void updateGraphics() {
        freeBodyDiagram.updateAll();
        if( fbdPanel.isShowing() ) {
            fbdPanel.paint();
        }
    }

    public void setVisible( boolean visible ) {
        fbdPanel.setVisible( visible );
    }

    public void reset() {
        if( !freeBodyDiagram.isUserClicked() ) {//TODO maybe this should be smarter.
            fbdWiggleMe.setVisible( true );
            if( !containsGraphic( fbdWiggleMe ) ) {
                fbdPanel.addGraphic( fbdWiggleMe );
            }
        }
    }

    private boolean containsGraphic( PhetGraphic graphic ) {

        PhetGraphic[] g = fbdPanel.getGraphic().getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            PhetGraphic phetGraphic = g[i];
            if( phetGraphic == graphic ) {
                return true;
            }
        }
        return false;

    }
}
