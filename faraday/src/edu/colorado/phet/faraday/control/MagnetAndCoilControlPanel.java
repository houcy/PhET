/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.module.MagnetAndCoilModule;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;

/**
 * MagnetAndCoilControlPanel is the control panel for the "Magnet & Coil" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MagnetAndCoilControlPanel extends FaradayControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private AbstractMagnet _magnetModel;
    private Compass _compassModel;
    private PickupCoil _pickupCoilModel;
    private LightBulb _lightBulbModel;
    private VoltMeter _voltMeterModel;
    private BarMagnetGraphic _magnetGraphic;
    private CompassGridGraphic _gridGraphic;
    
    // UI components
    private JButton _flipPolarityButton;
    private JSlider _strengthSlider;
    private JCheckBox _seeInsideCheckBox, _gridCheckBox, _compassCheckBox;
    private JSpinner _loopsSpinner;
    private JSlider _radiusSlider;
    private JRadioButton _voltmeterRadioButton;
    private JRadioButton _lightbulbRadioButton;
    private JLabel _strengthValue, _radiusValue;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * <p>
     * The structure of the code (the way that code blocks are nested)
     * reflects the structure of the panel.
     * 
     * @param module the module that this control panel is associated with.
     * @param magnetModel
     * @param compassModel
     * @param pickupCoilModel
     * @param lightBulbModel
     * @param voltMeterModel
     * @param magnetGraphic
     * @param gridGraphic
     */
    public MagnetAndCoilControlPanel( 
        MagnetAndCoilModule module,
        AbstractMagnet magnetModel,
        Compass compassModel,
        PickupCoil pickupCoilModel,
        LightBulb lightBulbModel,
        VoltMeter voltMeterModel,
        BarMagnetGraphic magnetGraphic,
        CompassGridGraphic gridGraphic ) {

        super( module );

        assert( magnetModel != null );
        assert( compassModel != null );
        assert( pickupCoilModel != null );
        assert( lightBulbModel != null );
        assert( voltMeterModel != null );
        assert( magnetGraphic != null );
        assert( gridGraphic != null );

        // Things we'll be controlling.
        _magnetModel = magnetModel;
        _compassModel = compassModel;
        _pickupCoilModel = pickupCoilModel;
        _lightBulbModel = lightBulbModel;
        _voltMeterModel = voltMeterModel;
        _magnetGraphic = magnetGraphic;
        _gridGraphic = gridGraphic;

        JPanel fillerPanel = new JPanel();
        {
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            // WORKAROUND: Filler to set consistent panel width
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
        }
        
        // Magnet strength
        JPanel strengthPanel = new JPanel();
        {
            // Title
            TitledBorder border = new TitledBorder( SimStrings.get( "MagnetAndCoilModule.magnetStrength" ) );
            strengthPanel.setBorder( border );

            // Slider
            _strengthSlider = new JSlider();
            _strengthSlider.setMaximum( (int) FaradayConfig.MAGNET_STRENGTH_MAX );
            _strengthSlider.setMinimum( (int) FaradayConfig.MAGNET_STRENGTH_MIN );
            _strengthSlider.setValue( (int) FaradayConfig.MAGNET_STRENGTH_MIN );
            setSliderSize( _strengthSlider, SLIDER_SIZE );

            // Value
            _strengthValue = new JLabel( UNKNOWN_VALUE );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( strengthPanel );
            strengthPanel.setLayout( layout );
            layout.addAnchoredComponent( _strengthSlider, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _strengthValue, 0, 1, GridBagConstraints.WEST );
        }
        
        //  Flip Polarity button
        _flipPolarityButton = new JButton( SimStrings.get( "MagnetAndCoilModule.flipPolarity" ) );
        
        // Magnet transparency on/off
        _seeInsideCheckBox = new JCheckBox( SimStrings.get( "MagnetAndCoilModule.seeInside" ) );

        // Compass Grid on/off
        _gridCheckBox = new JCheckBox( SimStrings.get( "MagnetAndCoilModule.showGrid" ) );

        // Compass on/off
        _compassCheckBox = new JCheckBox( SimStrings.get( "MagnetAndCoilModule.showCompass" ) );
        
        // Number of loops
        JPanel loopsPanel = new JPanel();
        {
            JLabel loopsLabel = new JLabel( SimStrings.get( "MagnetAndCoilModule.numberOfLoops" ) );

            // Spinner, keyboard editing disabled.
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
            spinnerModel.setMaximum( new Integer( FaradayConfig.MAX_PICKUP_LOOPS ) );
            spinnerModel.setMinimum( new Integer( FaradayConfig.MIN_PICKUP_LOOPS ) );
            spinnerModel.setValue( new Integer( FaradayConfig.MIN_PICKUP_LOOPS ) );
            _loopsSpinner = new JSpinner( spinnerModel );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _loopsSpinner.getEditor() ).getTextField();
            tf.setEditable( false );

            // Dimensions
            _loopsSpinner.setPreferredSize( SPINNER_SIZE );
            _loopsSpinner.setMaximumSize( SPINNER_SIZE );
            _loopsSpinner.setMinimumSize( SPINNER_SIZE );

            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( loopsPanel );
            loopsPanel.setLayout( layout );
            layout.addAnchoredComponent( loopsLabel, 0, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _loopsSpinner, 0, 1, GridBagConstraints.WEST );
        }

        // Loop radius
        JPanel radiusPanel = new JPanel();
        {
            // Title
            TitledBorder border = new TitledBorder( SimStrings.get( "MagnetAndCoilModule.radius" ) );
            radiusPanel.setBorder( border );

            // Slider
            _radiusSlider = new JSlider();
            _radiusSlider.setMaximum( (int) MagnetAndCoilModule.LOOP_RADIUS_MAX );
            _radiusSlider.setMinimum( (int) MagnetAndCoilModule.LOOP_RADIUS_MIN );
            _radiusSlider.setValue( (int) MagnetAndCoilModule.LOOP_RADIUS_MIN );
            super.setSliderSize( _radiusSlider, SLIDER_SIZE );

            // Value
            _radiusValue = new JLabel( UNKNOWN_VALUE );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( radiusPanel );
            radiusPanel.setLayout( layout );
            layout.addAnchoredComponent( _radiusSlider, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _radiusValue, 0, 1, GridBagConstraints.WEST );
        }
        
        JPanel connectionPanel = new JPanel();
        {
            // Title
            TitledBorder border = new TitledBorder( SimStrings.get( "MagnetAndCoilModule.connectToCoil" ) );
            connectionPanel.setBorder( border );

            // Radio buttons
            _lightbulbRadioButton = new JRadioButton( SimStrings.get( "MagnetAndCoilModule.lightbulb" ) );
            _voltmeterRadioButton = new JRadioButton( SimStrings.get( "MagnetAndCoilModule.voltmeter" ) );
            ButtonGroup group = new ButtonGroup();
            group.add( _lightbulbRadioButton );
            group.add( _voltmeterRadioButton );

            // Layout
//            connectionPanel.add( _lightbulbRadioButton );
//            connectionPanel.add( _voltmeterRadioButton );
            EasyGridBagLayout layout = new EasyGridBagLayout( connectionPanel );
            connectionPanel.setLayout( layout );
            layout.addAnchoredComponent( _lightbulbRadioButton, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _voltmeterRadioButton, 1, 0, GridBagConstraints.WEST );
        }

        JPanel controlPanel = new JPanel();
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( controlPanel );
            controlPanel.setLayout( layout );
            controlPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
            
            int row = 0;
            layout.addFilledComponent( strengthPanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _flipPolarityButton, row++, 0 );
            layout.addComponent( _seeInsideCheckBox, row++, 0 );
            layout.addComponent( _gridCheckBox, row++, 0 );
            layout.addComponent( _compassCheckBox, row++, 0 );
            layout.addFilledComponent( loopsPanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( radiusPanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( connectionPanel, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Add panels.
        addFullWidth( fillerPanel );
        addFullWidth( controlPanel );

        // Wire up event handling
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _seeInsideCheckBox.addActionListener( listener );
        _gridCheckBox.addActionListener( listener );
        _loopsSpinner.addChangeListener( listener );
        _radiusSlider.addChangeListener( listener );
        _lightbulbRadioButton.addActionListener( listener );
        _voltmeterRadioButton.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        
        // Update control panel to match the components that it's controlling.
        _strengthSlider.setValue( (int) _magnetModel.getStrength() );
        _seeInsideCheckBox.setSelected( _magnetGraphic.isTransparencyEnabled() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _loopsSpinner.setValue( new Integer( _pickupCoilModel.getNumberOfLoops() ) );
        _radiusSlider.setValue( (int) _pickupCoilModel.getRadius() );
        _lightbulbRadioButton.setSelected( _lightBulbModel.isEnabled() );
        _voltmeterRadioButton.setSelected( _voltMeterModel.isEnabled() );
    }
    
    //----------------------------------------------------------------------------
    // Event Handling
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class EventListener implements ActionListener, ChangeListener {

        /** Sole constructor */
        public EventListener() {}

        /**
         * ActionEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _flipPolarityButton ) {
                // Magnet polarity
                boolean smoothingEnabled = _pickupCoilModel.isSmoothingEnabled();
                _pickupCoilModel.setSmoothingEnabled( false );
                _magnetModel.setDirection( _magnetModel.getDirection() + Math.PI );
                _compassModel.startMovingNow();
                _pickupCoilModel.updateEmf();
                _pickupCoilModel.setSmoothingEnabled( smoothingEnabled );
            }
            else if ( e.getSource() == _seeInsideCheckBox ) {
                // Magnet transparency
                _magnetGraphic.setTransparencyEnabled( _seeInsideCheckBox.isSelected() );
            }
            else if ( e.getSource() == _gridCheckBox ) {
                // Grid enable
                _gridGraphic.resetSpacing();
                _gridGraphic.setVisible( _gridCheckBox.isSelected() );
            }
            else if ( e.getSource() == _compassCheckBox ) {
                // Compass enable
                _compassModel.setEnabled( _compassCheckBox.isSelected() );
            }
            else if ( e.getSource() == _lightbulbRadioButton ) {
                // Lightbulb enable
                _lightBulbModel.setEnabled( _lightbulbRadioButton.isSelected() );
                _voltMeterModel.setEnabled( !_lightbulbRadioButton.isSelected() );
            }
            else if ( e.getSource() == _voltmeterRadioButton ) {
                // Voltmeter enable
                _voltMeterModel.setEnabled( _voltmeterRadioButton.isSelected() );
                _lightBulbModel.setEnabled( !_voltmeterRadioButton.isSelected() );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }

        /**
         * ChangeEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _strengthSlider ) {
                // Magnet strength
                int strength = _strengthSlider.getValue();
                _magnetModel.setStrength( strength );
                _strengthValue.setText( String.valueOf( strength ) + " " + FaradayConfig.GAUSS_LABEL );
            }
            else if ( e.getSource() == _radiusSlider ) {
                // Loop radius
                int radius = _radiusSlider.getValue();
                boolean smoothingEnabled = _pickupCoilModel.isSmoothingEnabled();
                _pickupCoilModel.setSmoothingEnabled( false );
                _pickupCoilModel.setRadius( radius );
                _pickupCoilModel.updateEmf();
                _pickupCoilModel.setSmoothingEnabled( smoothingEnabled );
                Integer i = new Integer( radius );
                _radiusValue.setText( i.toString() );
            }
            else if ( e.getSource() == _loopsSpinner ) {
                // Number of loops
                int numberOfLoops = ( (Integer) _loopsSpinner.getValue() ).intValue();
                boolean smoothingEnabled = _pickupCoilModel.isSmoothingEnabled();
                _pickupCoilModel.setSmoothingEnabled( false );
                _pickupCoilModel.setNumberOfLoops( numberOfLoops );
                _pickupCoilModel.updateEmf();
                _pickupCoilModel.setSmoothingEnabled( smoothingEnabled );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}