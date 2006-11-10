/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.hydrogenatom.control.SliderControl;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.Gun;
import edu.colorado.phet.hydrogenatom.model.HAModel;
import edu.colorado.phet.hydrogenatom.model.RutherfordScattering;
import edu.colorado.phet.hydrogenatom.module.HAModule;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperControlsDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Insets SLIDER_INSETS = new Insets( 0, 0, 0, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HAModule _module;
    
    private SliderControl _ticksPerPhotonSlider;
    private SliderControl _ticksPerAlphaParticleSlider;
    private JCheckBox _particleLimitCheckBox;
    private JCheckBox _rutherfordScatteringOutputCheckBox;
    private JCheckBox _bohrAbsorptionCheckBox;
    private JCheckBox _bohrEmissionCheckBox;
    private JCheckBox _bohrStimulatedEmissionCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeveloperControlsDialog( Frame owner, HAModule module ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        
        _module = module;
        
        JPanel inputPanel = createInputPanel();
        
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );
        
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createInputPanel() {

        // Photon production
        double ticksPerPhoton = _module.getGun().getTicksPerPhoton();
        _ticksPerPhotonSlider = new SliderControl( ticksPerPhoton, 1, 20, 20, 0, 1, "Fired 1 photon every", "ticks", 3, SLIDER_INSETS );
              
        // Alpha Particle production
        double ticksPerAlphaParticle = _module.getGun().getTicksPerAlphaParticle();
        _ticksPerAlphaParticleSlider = new SliderControl( ticksPerAlphaParticle, 1, 20, 20, 0, 1, "Fired 1 alpha particle", "ticks", 3, SLIDER_INSETS );
        
        // Limits model to one particle at a time
        _particleLimitCheckBox = new JCheckBox( "Limit model to 1 particle" );
        _particleLimitCheckBox.setSelected( HAModel.isSingleParticleLimitEnabled() );
        
        // Enables debug output from Rutherford Scattering algorithm
        _rutherfordScatteringOutputCheckBox = new JCheckBox( "Rutherford Scattering debug output" );
        _rutherfordScatteringOutputCheckBox.setSelected( RutherfordScattering.DEBUG_OUTPUT_ENABLED );
        
        // Bohr absorption/emission enables
        _bohrAbsorptionCheckBox = new JCheckBox( "absorption enabled", BohrModel.DEBUG_ASBORPTION_ENABLED );
        _bohrEmissionCheckBox = new JCheckBox( "emission enabled", BohrModel.DEBUG_EMISSION_ENABLED );
        _bohrStimulatedEmissionCheckBox = new JCheckBox( "stimulated emission enabled", BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED );
        
        // Event handling
        EventListener listener = new EventListener();
        _ticksPerPhotonSlider.addChangeListener( listener );
        _ticksPerAlphaParticleSlider.addChangeListener( listener );
        _particleLimitCheckBox.addChangeListener( listener );
        _rutherfordScatteringOutputCheckBox.addChangeListener( listener );
        _bohrAbsorptionCheckBox.addChangeListener( listener );
        _bohrEmissionCheckBox.addChangeListener( listener );
        _bohrStimulatedEmissionCheckBox.addChangeListener( listener );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        panel.setLayout( layout );
        int row = 0;
        layout.addComponent( _ticksPerPhotonSlider, row++, 0 ); 
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _ticksPerAlphaParticleSlider, row++, 0 );
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _particleLimitCheckBox, row++, 0 );
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _rutherfordScatteringOutputCheckBox, row++, 0 );
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( new JLabel( "Bohr model:"), row++, 0 );
        layout.addComponent( _bohrAbsorptionCheckBox, row++, 0 );
        layout.addComponent( _bohrEmissionCheckBox, row++, 0 );
        layout.addComponent( _bohrStimulatedEmissionCheckBox, row++, 0 );
        
        return panel;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener implements ChangeListener {
        
        public EventListener() {}

        public void stateChanged( ChangeEvent event ) {
            Object source = event.getSource();
            if ( source == _ticksPerPhotonSlider ) {
                handleTicksPerPhotonSlider();
            }
            else if ( source == _ticksPerAlphaParticleSlider ) {
                handleTicksPerAlphaParticleSlider();
            }
            else if ( source == _particleLimitCheckBox ) {
                handleParticleLimitCheckBox();
            }
            else if ( source == _rutherfordScatteringOutputCheckBox ) {
                handleRutherfordScatteringOutputCheckBox();
            }
            else if ( source == _bohrAbsorptionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
            else if ( source == _bohrEmissionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
            else if ( source == _bohrStimulatedEmissionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
        }
    }
    
    private void handleTicksPerPhotonSlider() {
        Gun gun = _module.getGun();
        gun.setTicksPerPhoton( _ticksPerPhotonSlider.getValue() );
    }
    
    private void handleTicksPerAlphaParticleSlider() {
        Gun gun = _module.getGun();
        gun.setTicksPerAlphaParticle( _ticksPerAlphaParticleSlider.getValue() ); 
    }
    
    private void handleParticleLimitCheckBox() {
        HAModel.setSingleParticleLimitEnabled( _particleLimitCheckBox.isSelected() );
    }
    
    private void handleRutherfordScatteringOutputCheckBox() {
        RutherfordScattering.DEBUG_OUTPUT_ENABLED = _rutherfordScatteringOutputCheckBox.isSelected();
    }
    
    private void handleBohrAbsorptionEmission() {
        BohrModel.DEBUG_ASBORPTION_ENABLED = _bohrAbsorptionCheckBox.isSelected();
        BohrModel.DEBUG_EMISSION_ENABLED = _bohrEmissionCheckBox.isSelected();
        BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED = _bohrStimulatedEmissionCheckBox.isSelected();
    }
}
