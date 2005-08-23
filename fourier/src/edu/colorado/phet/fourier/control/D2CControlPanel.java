/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.control.sliders.DeltaKSlider;
import edu.colorado.phet.fourier.control.sliders.DeltaXSlider;
import edu.colorado.phet.fourier.control.sliders.K0Slider;
import edu.colorado.phet.fourier.control.sliders.K1Slider;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.view.D2CAmplitudesGraph;
import edu.colorado.phet.fourier.view.D2CHarmonicsGraph;
import edu.colorado.phet.fourier.view.D2CSumGraph;
import edu.colorado.phet.fourier.view.tools.WavePacketDeltaKTool;
import edu.colorado.phet.fourier.view.tools.WavePacketDeltaXTool;
import edu.colorado.phet.fourier.view.tools.WavePacketPeriodTool;
import edu.colorado.phet.fourier.view.tools.WavePacketSpacingTool;



/**
 * D2CControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CControlPanel extends FourierControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int TITLED_BORDER_WIDTH = 1;
    
    private static final int SPACE_BETWEEN_SUBPANELS = 10;
    
    private static final int MIN_SPACING = 0;
    private static final int MAX_SPACING = 100;
    private static final int MIN_X_WIDTH = 0;
    private static final int MAX_X_WIDTH = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private GaussianWavePacket _wavePacket;
    private D2CAmplitudesGraph _amplitudesGraph;
    private D2CHarmonicsGraph _harmonicsGraph;
    private D2CSumGraph _sumGraph;
    private WavePacketSpacingTool _spacingTool;
    private WavePacketDeltaKTool _deltaKTool;
    private WavePacketDeltaXTool _deltaXTool;
    private WavePacketPeriodTool _periodTool;

    // UI components
    private FourierComboBox _domainComboBox;
    private K1Slider _k1Slider;
    private K0Slider _k0Slider;
    private JCheckBox _continuousCheckBox;
    private DeltaKSlider _deltaKSlider;
    private DeltaXSlider _deltaXSlider;
    private FourierComboBox _waveTypeComboBox;
    
    // Choices
    private ArrayList _domainChoices;
    private ArrayList _waveTypeChoices;
    
    private EventListener _listener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     * @param wavePacket
     * @param amplitudesGraph
     * @param harmonicsGraph
     */
    public D2CControlPanel( FourierModule module, 
            GaussianWavePacket wavePacket, 
            D2CAmplitudesGraph amplitudesGraph,
            D2CHarmonicsGraph harmonicsGraph,
            D2CSumGraph sumGraph,
            WavePacketSpacingTool spacingTool,
            WavePacketDeltaKTool deltaKTool,
            WavePacketDeltaXTool deltaXTool,
            WavePacketPeriodTool periodTool ) {
        
        super( module );
        
        assert( wavePacket != null );
        assert( amplitudesGraph != null );
        assert( harmonicsGraph != null );
        assert( sumGraph != null );
        assert( spacingTool != null );
        assert( deltaKTool != null );
        assert( deltaXTool != null );
        assert( periodTool != null );
        
        _wavePacket = wavePacket;
        _amplitudesGraph = amplitudesGraph;
        _harmonicsGraph = harmonicsGraph;
        _sumGraph = sumGraph;
        _spacingTool = spacingTool;
        _deltaKTool = deltaKTool;
        _deltaXTool = deltaXTool;
        _periodTool = periodTool;
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "D2CControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );

        JPanel miscPanel = new JPanel();
        {
            miscPanel.setBorder( new TitledBorder( "" ) );
            
            // Domain
            {
                // Label
                String label = SimStrings.get( "D2CControlPanel.domain" );

                // Choices
                _domainChoices = new ArrayList();
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_SPACE, SimStrings.get( "domain.space" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_TIME, SimStrings.get( "domain.time" ) ) );
 
                // Function combo box
                _domainComboBox = new FourierComboBox( label, _domainChoices );
            }

            // Wave Type
            {
                // Label
                String label = SimStrings.get( "D2CControlPanel.waveType" );

                // Choices
                _waveTypeChoices = new ArrayList();
                _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_SINE, SimStrings.get( "waveType.sines" ) ) );
                _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_COSINE, SimStrings.get( "waveType.cosines" ) ) );

                // Wave Type combo box
                _waveTypeComboBox = new FourierComboBox( label, _waveTypeChoices );
            }
            
            // Continuous checkbox
            _continuousCheckBox = new JCheckBox( SimStrings.get( "D2CControlPanel.continuous" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( miscPanel );
            miscPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _domainComboBox, row++, 0 );
            layout.addComponent( _waveTypeComboBox, row++, 0 );
            layout.addComponent( _continuousCheckBox, row++, 0 );
        }
       
        // Packet width panel
        JPanel packetPanel = new JPanel();
        {
            String title = SimStrings.get( "D2CControlPanel.gaussianWavePacket" );
            packetPanel.setBorder( new TitledBorder( title ) );
            
            // k1 (spacing)
            _k1Slider = new K1Slider();
            
            // k0 (center point)
            _k0Slider = new K0Slider();
            
            // dk (k-space width)
            _deltaKSlider = new DeltaKSlider();

            // dx (x-space width)
            _deltaXSlider = new DeltaXSlider();
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( packetPanel );
            packetPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _k1Slider, row++, 0 );
            layout.addComponent( _k0Slider, row++, 0 );
            layout.addComponent( _deltaKSlider, row++, 0 );
            layout.addComponent( _deltaXSlider, row++, 0 );
        }

        // Layout
        addFullWidth( miscPanel );
        addVerticalSpace( SPACE_BETWEEN_SUBPANELS );
        addFullWidth( packetPanel );
        
        // Set the state of the controls.
        reset();
        
        // Wire up event handling (after setting state with reset).
        {
            _listener = new EventListener();
            _domainComboBox.addItemListener( _listener );
            _k1Slider.addChangeListener( _listener );
            _continuousCheckBox.addActionListener( _listener );
            _k0Slider.addChangeListener( _listener );
            _deltaKSlider.addChangeListener( _listener );
            _deltaXSlider.addChangeListener( _listener );
            _waveTypeComboBox.addItemListener( _listener );
        }
    }
    
    public void reset() {
        
        _continuousCheckBox.setSelected( _amplitudesGraph.isContinuousEnabled() );
        
        _domainComboBox.setSelectedKey( FourierConstants.DOMAIN_SPACE );
        handleDomain();
        
        _k1Slider.setValue( _wavePacket.getK1() );
        _k0Slider.setValue( _wavePacket.getK0() );
        _deltaKSlider.setValue( _wavePacket.getDeltaK() );
        _deltaXSlider.setValue( _wavePacket.getDeltaX() );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {

        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _continuousCheckBox ) {
                handleContinuous();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {

            if ( event.getSource() == _k1Slider ) {
                handleK1();
            }
            else if ( event.getSource() == _k0Slider ) {
                handleK0();
            }
            else if ( event.getSource() == _deltaKSlider ) {
                handleDeltaK();
            }
            else if ( event.getSource() == _deltaXSlider ) {
                handleDeltaX();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _domainComboBox.getComboBox() ) {
                    handleDomain();
                }
                else if ( event.getSource() == _waveTypeComboBox.getComboBox() ) {
                    handleWaveType();
                }
                else {
                    throw new IllegalArgumentException( "unexpected event: " + event );
                }
            }
        } 
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * Handles changes to the domain combo box.
     */
    private void handleDomain() {
        int domain = _domainComboBox.getSelectedKey();
        
        _amplitudesGraph.setDomain( domain );
        _harmonicsGraph.setDomain( domain );
        _sumGraph.setDomain( domain );
        _spacingTool.setDomain( domain );
        _deltaKTool.setDomain( domain );
        _deltaXTool.setDomain( domain );
        _periodTool.setDomain( domain );
        
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            _k1Slider.setFormat( SimStrings.get( "K1Slider.format.space" ) );
            _k0Slider.setFormat( SimStrings.get( "K0Slider.format.space" ) );
            _deltaKSlider.setFormat( SimStrings.get( "DeltaKSlider.format.space" ) );
            _deltaXSlider.setFormat( SimStrings.get( "DeltaXSlider.format.space" ) );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            _k1Slider.setFormat( SimStrings.get( "K1Slider.format.time" ) );
            _k0Slider.setFormat( SimStrings.get( "K0Slider.format.time" ) );
            _deltaKSlider.setFormat( SimStrings.get( "DeltaKSlider.format.time" ) );
            _deltaXSlider.setFormat( SimStrings.get( "DeltaXSlider.format.time" ) );   
        }
    }
    
    /*
     * Handles changes to the wave type combo box.
     */
    private void handleWaveType() {
        int waveType = _waveTypeComboBox.getSelectedKey();
        _harmonicsGraph.setWaveType( waveType );
        _sumGraph.setWaveType( waveType );
    }
    
    /*
     * Handles changes to the "continuous" check box.
     */
    private void handleContinuous() {
        _amplitudesGraph.setContinuousEnabled( _continuousCheckBox.isSelected() );
    }
    
    /*
     * Handles changes to the "k1" slider.
     */
    private void handleK1() {
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_k1Slider.isAdjusting() ) {
            setWaitCursorEnabled( true );
            double k1 = _k1Slider.getValue();
            _wavePacket.setK1( k1 );
            setWaitCursorEnabled( false );
        }
    }
    
    /*
     * Handles changes to the "k0" slider.
     */
    private void handleK0() {
        // Update the wave packet if the user is done dragging the slider.
        if ( !_k0Slider.isAdjusting() ) {
            setWaitCursorEnabled( true );
            double k0 = _k0Slider.getValue();
            System.out.println( "k0 = " + k0 );//XXX
//            _wavePacket.setK0( k0 );
            setWaitCursorEnabled( false );
        }
    }
    
    /*
     * Handles changes to the "delta k" slider.
     */
    private void handleDeltaK() {
        
        setWaitCursorEnabled( true );
        
        double deltaK = _deltaKSlider.getValue();
        
        // Update the delta x slider.
        _deltaXSlider.removeChangeListener( _listener );
        _deltaXSlider.setValue( 1 / deltaK );
        _deltaXSlider.addChangeListener( _listener );
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_deltaKSlider.isAdjusting() ) {
            _wavePacket.setDeltaK( deltaK );
        }
        
        setWaitCursorEnabled( false );
    }
   
    /*
     * Handles changes to the "delta x" slider.
     */
    private void handleDeltaX() {
        
        setWaitCursorEnabled( true );
        
        double deltaX = _deltaXSlider.getValue();
        
        // Update the delta k slider.
        _deltaKSlider.removeChangeListener( _listener );
        _deltaKSlider.setValue( 1 / deltaX );
        _deltaKSlider.addChangeListener( _listener );
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_deltaXSlider.isAdjusting() ) {
            _wavePacket.setDeltaX( deltaX );
        }
        
        setWaitCursorEnabled( false );
    }
}