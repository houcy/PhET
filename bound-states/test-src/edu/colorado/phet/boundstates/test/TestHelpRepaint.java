/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.jfreechart.piccolo.XYPlotNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * TestHelpRepaint demonstrates a problem with help items on Macintosh.
 * When the simulation clock is running, turning help on results
 * in the help items being partially painted.  And help items 
 * (or parts of help items) that fall outside the PCanvas are
 * not painted. Other parts of the interface (eg, the Help button
 * in the control panel) are sometimes not properly painted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestHelpRepaint {

    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

    private static final Color BACKGROUND = new Color( 208, 255, 252 ); // light blue

    private static final double MIN_X = 0;
    private static final double MAX_X = 200;
    private static final double DX = 1;
    private static final double MIN_Y = -100;
    private static final double MAX_Y = 100;


    public static void main( final String[] args ) {
        try {
            TestApplication app = new TestApplication( args );
            app.startApplication();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    private static class TestApplication extends PiccoloPhetApplication {
    public TestApplication( String[] args ) throws InterruptedException {
        super( args, "TestHelpRepaint", "description", "0.1", new FrameSetup.CenteredWithSize( 1024, 768 ) );

        // Clock
        IClock clock = new SwingClock( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );

        // Modules
        Module moduleOne = new TestModule( "One", clock );
        addModule( moduleOne );
        Module moduleTwo = new TestModule( "Two", clock );
        addModule( moduleTwo );
    }
    }


    private static class TestModule extends PiccoloModule {

        private PhetPCanvas _canvas;
        private XYSeries _series;
        private JFreeChartNode _chartNode;
        private XYPlotNode _plotNode;

        public TestModule( String name, IClock clock ) {
            super( name, clock, false /* startsPaused */);

            // Canvas
            _canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
            setSimulationPanel( _canvas );
            _canvas.setBackground( BACKGROUND );
            _canvas.addComponentListener( new ComponentAdapter() {

                public void componentResized( ComponentEvent event ) {
                    // update the canvas layout when its size changes
                    updateLayout();
                }
            } );

            // Parent of all nodes, working is screen coordinates
            PNode parentNode = new PNode();
            _canvas.addScreenChild( parentNode );

            // JFreeChartNode, for drawing the chart's background, contains no data.
            XYPlot emptyPlot = createPlot();
            JFreeChart chart = new JFreeChart( emptyPlot );
            chart.setBackgroundPaint( BACKGROUND );
            _chartNode = new JFreeChartNode( chart );
            parentNode.addChild( _chartNode );

            // XYPlotNode, plots the actual data on top of the chart
            {
                // Series
                final int seriesIndex = 0;
                _series = new XYSeries( "Random data" );
                XYSeriesCollection dataset = new XYSeriesCollection();
                dataset.addSeries( _series );

                // Plot
                XYPlot plot = createPlot();
                plot.setDataset( seriesIndex, dataset );
                XYItemRenderer renderer = new StandardXYItemRenderer();
                renderer.setPaint( Color.RED );
                renderer.setStroke( new BasicStroke( 2f ) );
                plot.setRenderer( seriesIndex, renderer );

                // Plot node, draws data on top of the static chart
                _plotNode = new XYPlotNode( plot );
                parentNode.addChild( _plotNode );
            }

            // Blue square, draggable
            PPath blueSquare = new PPath();
            blueSquare.setPaint( Color.BLUE );
            blueSquare.setPathTo( new Rectangle2D.Double( 0, 0, 150, 150 ) );
            blueSquare.addInputEventListener( new PDragEventHandler() );
            blueSquare.addInputEventListener( new CursorHandler() );
            blueSquare.setOffset( 300, 200 );
            parentNode.addChild( blueSquare );

            // Green square, draggable             
            PPath greenSquare = new PPath();
            greenSquare.setPaint( Color.GREEN );
            greenSquare.setPathTo( new Rectangle2D.Double( 0, 0, 150, 150 ) );
            greenSquare.addInputEventListener( new PDragEventHandler() );
            greenSquare.addInputEventListener( new CursorHandler() );
            greenSquare.setOffset( 100, 200 );
            parentNode.addChild( greenSquare );

            // Control panel
            JCheckBox checkBox = new JCheckBox( name );;
            {
                ControlPanel controlPanel = new ControlPanel();
                setControlPanel( controlPanel );

                JPanel strut = new JPanel();
                strut.setLayout( new BoxLayout( strut, BoxLayout.X_AXIS ) );
                strut.add( Box.createHorizontalStrut( 150 ) );
                controlPanel.addControlFullWidth( strut );

                // Misc controls that do nothing
                controlPanel.addControl( checkBox );
            }

            // Help items
            {
                HelpPane helpPane = getDefaultHelpPane();

                HelpBalloon clockHelp = new HelpBalloon( helpPane, "Clock controls", HelpBalloon.BOTTOM_LEFT, 80 );
                helpPane.add( clockHelp );
                clockHelp.pointAt( getClockControlPanel() );

                HelpBalloon blueSquareHelp = new HelpBalloon( helpPane, "Drag me", HelpBalloon.LEFT_CENTER, 30 );
                helpPane.add( blueSquareHelp );
                blueSquareHelp.pointAt( blueSquare, _canvas );

                HelpBalloon greenSquareHelp = new HelpBalloon( helpPane, "Drag me", HelpBalloon.RIGHT_CENTER, 30 );
                helpPane.add( greenSquareHelp );
                greenSquareHelp.pointAt( greenSquare, _canvas );
                
                HelpBalloon checkBoxHelp = new HelpBalloon( helpPane, "Check me", HelpBalloon.RIGHT_CENTER, 30 );
                helpPane.add( checkBoxHelp );
                checkBoxHelp.pointAt( checkBox );
            }

            // update the data when the clock ticks
            getClock().addClockListener( new ClockAdapter() {

                public void clockTicked( ClockEvent event ) {
                    final double t = event.getSimulationTime();
                    updateData( t );
                }
            } );

            // Default layout and data set...
            updateLayout();
            updateData( 0 /* t=0 */);
        }

        public boolean hasHelp() {
            return true;
        }

        // Creates the plot used by both the static chart and the XYPlotNode
        private XYPlot createPlot() {
            XYPlot plot = new XYPlot();
            ValueAxis xAxis = new NumberAxis( "X" );
            xAxis.setRange( MIN_X, MAX_X );
            plot.setDomainAxis( xAxis );
            ValueAxis yAxis = new NumberAxis( "Y" );
            yAxis.setRange( MIN_Y, MAX_Y );
            plot.setRangeAxis( yAxis );
            plot.setRenderer( new StandardXYItemRenderer() );
            return plot;
        }

        // Called whenever the simulation's window size is changed
        private void updateLayout() {

            final double margin = 10;

            // Chart
            double x = margin;
            double y = margin;
            double w = _canvas.getWidth() - ( 2 * margin );
            double h = _canvas.getHeight() - ( 2 * margin ) - y;
            _chartNode.setBounds( 0, 0, w, h );
            _chartNode.setOffset( x, y );
            _chartNode.updateChartRenderingInfo();

            // Plot bounds
            ChartRenderingInfo chartInfo = _chartNode.getChartRenderingInfo();
            PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
            // Careful! getDataArea returns a direct reference!
            Rectangle2D dataAreaRef = plotInfo.getDataArea();
            Rectangle2D localBounds = new Rectangle2D.Double();
            localBounds.setRect( dataAreaRef );
            Rectangle2D plotBounds = _chartNode.localToGlobal( localBounds );

            // Plot node
            _plotNode.setOffset( 0, 0 );
            _plotNode.setDataArea( plotBounds );
        }

        // Called whenever the clock ticks
        private void updateData( final double t ) {
            // Generate some data for a time-varying function...
            _series.setNotify( false );
            _series.clear();
            for ( double x = MIN_X; x <= MAX_X + DX; x += DX ) {
                double y = MAX_Y * Math.sin( 3 * x - t );
                _series.add( x, y );
            }
            _series.setNotify( true );
        }
    } // class TestModule
}
