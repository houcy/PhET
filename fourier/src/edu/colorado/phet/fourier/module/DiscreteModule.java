/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.module;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.control.DiscreteControlPanel;
import edu.colorado.phet.fourier.help.WiggleMeGraphic;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.Vector2D;
import edu.colorado.phet.fourier.view.*;


/**
 * DiscreteModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteModule extends FourierModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double AMPLITUDES_LAYER = 1;
    private static final double HARMONICS_LAYER = 2;
    private static final double SUM_LAYER = 3;
    private static final double HARMONICS_CLOSED_LAYER = 4;
    private static final double SUM_CLOSED_LAYER = 5;
    private static final double TOOLS_LAYER = 6;

    // Locations
    private static final Point AMPLITUDES_LOCATION = new Point( 0, 0 );
    private static final Point HARMONICS_LOCATION = new Point( 0, 195 );
    private static final Point SUM_LOCATION = new Point( 0, 195 + 200 );
    private static final Point WAVELENGTH_TOOL_LOCATION = new Point( 590, 230 );
    private static final Point PERIOD_TOOL_LOCATION = WAVELENGTH_TOOL_LOCATION;
    private static final Point PERIOD_DISPLAY_LOCATION = new Point( 655, 345 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 115, 155 );
    
    // Colors
    private static final Color APPARATUS_PANEL_BACKGROUND = new Color( 215, 215, 215 );;
    private static final Color WIGGLE_ME_COLOR = Color.RED;
    
    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_HARMONICS = 7;
  
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private AmplitudesGraph _amplitudesGraph;
    private HarmonicsGraph _harmonicsGraph;
    private GraphClosed _harmonicsGraphClosed;
    private SumGraph _sumGraph;
    private GraphClosed _sumGraphClosed;
    private WavelengthTool _wavelengthTool;
    private PeriodTool _periodTool;
    private PeriodDisplay _periodDisplay;
    private DiscreteControlPanel _controlPanel;
    private AnimationCycleController _animationCycleController;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public DiscreteModule( AbstractClock clock ) {
        
        super( SimStrings.get( "DiscreteModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Fourier Series
        _fourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_PANEL_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        
        // Amplitudes view
        _amplitudesGraph = new AmplitudesGraph( apparatusPanel, _fourierSeries );
        _amplitudesGraph.setLocation( AMPLITUDES_LOCATION );
        apparatusPanel.addGraphic( _amplitudesGraph, AMPLITUDES_LAYER );
        
        // Harmonics view
        _harmonicsGraph = new HarmonicsGraph( apparatusPanel, _fourierSeries );
        _harmonicsGraph.setLocation( HARMONICS_LOCATION );
        apparatusPanel.addGraphic( _harmonicsGraph, HARMONICS_LAYER );
        
        // Harmonics view (collapsed)
        _harmonicsGraphClosed = new GraphClosed( apparatusPanel, SimStrings.get( "HarmonicsGraph.title" ) );
        _harmonicsGraphClosed.setLocation( HARMONICS_LOCATION );
        apparatusPanel.addGraphic( _harmonicsGraphClosed, HARMONICS_CLOSED_LAYER );
        
        // Sum view
        _sumGraph = new SumGraph( apparatusPanel, _fourierSeries );
        _sumGraph.setLocation( SUM_LOCATION );
        apparatusPanel.addGraphic( _sumGraph, SUM_LAYER );
        
        // Sum view (collapsed)
        _sumGraphClosed = new GraphClosed( apparatusPanel, SimStrings.get( "SumGraph.title" ) );
        _sumGraphClosed.setLocation( SUM_LOCATION );
        apparatusPanel.addGraphic( _sumGraphClosed, SUM_CLOSED_LAYER );
        
        // Wavelength Tool
        _wavelengthTool = new WavelengthTool( apparatusPanel, _fourierSeries.getHarmonic(0), _harmonicsGraph.getChart() );
        apparatusPanel.addGraphic( _wavelengthTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _wavelengthTool );
        
        // Period Tool
        _periodTool = new PeriodTool( apparatusPanel, _fourierSeries.getHarmonic(0), _harmonicsGraph.getChart() );
        apparatusPanel.addGraphic( _periodTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodTool );
        
        // Period Display
        _periodDisplay = new PeriodDisplay( apparatusPanel, _fourierSeries.getHarmonic(0) );
        apparatusPanel.addGraphic( _periodDisplay, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodDisplay );
        
        // Animation controller
        _animationCycleController = new AnimationCycleController( FourierConfig.ANIMATION_STEPS_PER_CYCLE );
        clock.addClockTickListener( _animationCycleController );
        _animationCycleController.addAnimationCycleListener( _harmonicsGraph );
        _animationCycleController.addAnimationCycleListener( _sumGraph );
        _animationCycleController.addAnimationCycleListener( _periodDisplay );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new DiscreteControlPanel( this, 
                _fourierSeries, _harmonicsGraph, _sumGraph, 
                _wavelengthTool, _periodTool, _periodDisplay,
                _animationCycleController );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
        setControlPanel( _controlPanel );
        
        // Link horizontal zoom controls
        _harmonicsGraph.getHorizontalZoomControl().addZoomListener( _sumGraph );
        _sumGraph.getHorizontalZoomControl().addZoomListener( _harmonicsGraph );
        
        // Harmonic hightlighting
        _amplitudesGraph.addHarmonicFocusListener( _harmonicsGraph );
        _wavelengthTool.addHarmonicFocusListener( _harmonicsGraph );
        _periodTool.addHarmonicFocusListener( _harmonicsGraph );
        _periodDisplay.addHarmonicFocusListener( _harmonicsGraph );
        
        // Slider movement by the user
        _amplitudesGraph.addChangeListener( _controlPanel );
        
        // Open/close buttons on graphs
        {
            ActionListener listener = new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleOpenCloseButton( event );
                }
            };
            _harmonicsGraph.getCloseButton().addActionListener( listener );
            _harmonicsGraphClosed.getOpenButton().addActionListener( listener );
            _sumGraph.getCloseButton().addActionListener( listener );
            _sumGraphClosed.getOpenButton().addActionListener( listener );
        }
        
        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        addHelpItem( new HelpItem( apparatusPanel, "< Help goes here >", 200, 150 ) );//XXX to test help
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
        wiggleMe.setEnabled( true );
    }
    
    //----------------------------------------------------------------------------
    // FourierModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        
        _fourierSeries.setNumberOfHarmonics( NUMBER_OF_HARMONICS );
        _fourierSeries.setFundamentalFrequency( FUNDAMENTAL_FREQUENCY );
        _fourierSeries.setPreset( FourierConstants.PRESET_SINE_COSINE );
        _fourierSeries.setWaveType( FourierConstants.WAVE_TYPE_SINE );
        
        _amplitudesGraph.reset();
        _harmonicsGraph.reset();
        _sumGraph.reset();
        
        _harmonicsGraph.setVisible( true );
        _harmonicsGraphClosed.setVisible( false );
        _sumGraph.setVisible( true );
        _sumGraphClosed.setVisible( false ); 
        
        _wavelengthTool.setVisible( false );
        _wavelengthTool.setLocation( WAVELENGTH_TOOL_LOCATION );
        
        _periodTool.setVisible( false );
        _periodTool.setLocation( PERIOD_TOOL_LOCATION );
        
        _periodDisplay.setVisible( false );
        _periodDisplay.setLocation( PERIOD_DISPLAY_LOCATION );
        
        _controlPanel.reset();
    }
   
    //----------------------------------------------------------------------------
    // EventHandling
    //----------------------------------------------------------------------------
    
    private void handleOpenCloseButton( ActionEvent event ) {
        if ( event.getSource() == _harmonicsGraph.getCloseButton() ) {
            _harmonicsGraph.setVisible( false );
            _harmonicsGraphClosed.setVisible( true );
        }
        else if ( event.getSource() == _harmonicsGraphClosed.getOpenButton() ) {
            _harmonicsGraph.setVisible( true );
            _harmonicsGraphClosed.setVisible( false );
        }
        else if ( event.getSource() == _sumGraph.getCloseButton() ) {
            _sumGraph.setVisible( false );
            _sumGraphClosed.setVisible( true );
        }
        else if ( event.getSource() == _sumGraphClosed.getOpenButton() ) {
            _sumGraph.setVisible( true );
            _sumGraphClosed.setVisible( false );      
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic {

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         */
        public ThisWiggleMeGraphic( final Component component, BaseModel model ) {
            super( component, model );

            setText( SimStrings.get( "DiscreteModule.wiggleMe" ), WIGGLE_ME_COLOR );
            addArrow( WiggleMeGraphic.TOP_CENTER, new Vector2D( 0, -30 ), WIGGLE_ME_COLOR );
            setRange( 0, 10 );
            setCycleDuration( 5 );
            setEnabled( true );
            
            // Disable the wiggle me when the mouse is pressed.
            component.addMouseListener( new MouseInputAdapter() { 
                public void mousePressed( MouseEvent event ) {
                    // Disable
                    setEnabled( false );
                    // Unwire
                    component.removeMouseListener( this );
                }
             } );
        }
    }
}
