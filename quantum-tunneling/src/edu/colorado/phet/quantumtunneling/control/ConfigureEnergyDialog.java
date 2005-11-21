/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.view.EnergyPlot;


/**
 * ConfigureEnergyDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConfigureEnergyDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Dimension CHART_SIZE = new Dimension( 450, 150 );
    private static final Font AXES_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    private static final Color BARRIER_PROPERTIES_COLOR = Color.RED;
    private static final Dimension SPINNER_SIZE = new Dimension( 65, 25 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private TotalEnergy _totalEnergy;
    private AbstractPotentialEnergy _potentialEnergy;
    
    // Chart area
    private EnergyPlot _energyPlot;
    
    // Input area
    private JPanel _inputPanel;
    private JComboBox _potentialComboBox;
    private Object _constantItem, _stepItem, _barrierItem, _doubleBarrierItem; // potential choices
    private JSpinner _teSpinner;
    private ArrayList _peSpinners; // array of JSpinner
    private JSpinner _stepSpinner;
    private ArrayList _widthSpinners; // array of JSpinner
    private ArrayList _positionSpinners; // array of JSpinner
    
    // Action area
    private JButton _applyButton, _closeButton;
    
    // Misc
    private Frame _parent;
    private EventListener _listener;
    private boolean _unsavedChanges;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param parent
     * @param totalEnergy
     * @param potentialEnergy
     */
    public ConfigureEnergyDialog( Frame parent, TotalEnergy totalEnergy, AbstractPotentialEnergy potentialEnergy ) {
        super( parent );

        setTitle( SimStrings.get( "title.configureEnergy" ) );
        setModal( true );
        setResizable( false );

        _parent = parent;
        _listener = new EventListener();
        _unsavedChanges = false;
        
        _totalEnergy = new TotalEnergy( totalEnergy );
        if ( potentialEnergy instanceof ConstantPotential ) {
            _potentialEnergy = new ConstantPotential( (ConstantPotential) potentialEnergy );
        }
        else if ( potentialEnergy instanceof StepPotential ) {
            _potentialEnergy = new StepPotential( (StepPotential) potentialEnergy );
        }
        else if ( potentialEnergy instanceof BarrierPotential ) {
            _potentialEnergy = new BarrierPotential( (BarrierPotential) potentialEnergy );
        }

        createUI( parent );
        populateValues();
        
        setLocationRelativeTo( parent );
    }

    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        // Nothing to do
    }

    //----------------------------------------------------------------------------
    // Internal initializers
    //----------------------------------------------------------------------------

    /**
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI( Frame parent ) {
        
        JPanel chartPanel = createChartPanel();
        _inputPanel = new JPanel();
        _inputPanel.add( createInputPanel() );
        JPanel actionsPanel = createActionsPanel();

        JPanel p1 = new JPanel( new BorderLayout() );
        p1.add( chartPanel, BorderLayout.NORTH );
        p1.add( new JSeparator(), BorderLayout.CENTER );
        
        JPanel p2 = new JPanel( new BorderLayout() );
        p2.add( p1, BorderLayout.NORTH );
        p2.add( _inputPanel, BorderLayout.SOUTH );
        
        JPanel p3 = new JPanel( new BorderLayout() );
        p3.add( p2, BorderLayout.NORTH );
        p3.add( new JSeparator(), BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( p3, BorderLayout.NORTH );
        mainPanel.add( actionsPanel, BorderLayout.SOUTH );

        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        chartPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );
        _inputPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );

        getContentPane().add( mainPanel );
        pack();
    }

    /**
     * Creates the dialog's chart panel.
     * 
     * @return the chart panel
     */
    private JPanel createChartPanel() {

        // Plot
        _energyPlot = new EnergyPlot();
        _energyPlot.setAxesFont( AXES_FONT );
        
        // Chart
        JFreeChart chart = new JFreeChart( null /*title*/, null /*font*/, _energyPlot, false /* createLegend */);

        // Chart panel
        ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPopupMenu( null ); // disable popup menu, on by default
        chartPanel.setMouseZoomable( false ); // disable zooming, on by default
        chartPanel.setMinimumDrawWidth( (int) CHART_SIZE.getWidth() - 1 );
        chartPanel.setMinimumDrawHeight( (int) CHART_SIZE.getHeight() - 1 );
        chartPanel.setPreferredSize( CHART_SIZE );

        return chartPanel;
    }

    /**
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {

        // Menu panel...
        JPanel menuPanel = new JPanel();
        {
            // Potential menu...
            JLabel potentialLabel = new JLabel( SimStrings.get( "label.potential" ) );
            _constantItem = SimStrings.get( "choice.potential.constant" );
            _stepItem = SimStrings.get( "choice.potential.step" );
            _barrierItem = SimStrings.get( "choice.potential.barrier" );
            _doubleBarrierItem = SimStrings.get( "choice.potential.double" );
            Object[] items = { _constantItem, _stepItem, _barrierItem, _doubleBarrierItem };
            _potentialComboBox = new JComboBox( items );
            if ( _potentialEnergy instanceof ConstantPotential ) {
                _potentialComboBox.setSelectedItem( _constantItem );
            }
            else if ( _potentialEnergy instanceof StepPotential ) {
                _potentialComboBox.setSelectedItem( _stepItem );
            }
            else if ( _potentialEnergy instanceof BarrierPotential ) {
                int numberOfBarriers = ( (BarrierPotential) _potentialEnergy).getNumberOfBarriers();
                if ( numberOfBarriers == 1 ) {
                    _potentialComboBox.setSelectedItem( _barrierItem );
                }
                else if ( numberOfBarriers == 2 ) {
                    _potentialComboBox.setSelectedItem( _doubleBarrierItem );
                }
                else {
                    throw new IllegalStateException( "unsupported number of barriers" );
                }
            }
            _potentialComboBox.addItemListener( _listener );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.addAnchoredComponent( potentialLabel, 0, 1, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _potentialComboBox, 0, 2, GridBagConstraints.WEST );
            menuPanel.setLayout( new BorderLayout() );
            menuPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Spinner panel...
        JPanel spinnerPanel = new JPanel();
        {
            EasyGridBagLayout inputPanelLayout = new EasyGridBagLayout( spinnerPanel );
            inputPanelLayout.setMinimumWidth( 0, 25 );
            inputPanelLayout.setMinimumWidth( 4, 60 );
            inputPanelLayout.setMinimumWidth( 5, 25 );

            spinnerPanel.setLayout( inputPanelLayout );
            int row = 0;

            // Total Energy
            {
                JLabel teLabel = new JLabel( SimStrings.get( "label.totalEnergy" ) );
                teLabel.setForeground( QTConstants.TOTAL_ENERGY_COLOR );
                SpinnerModel model = new SpinnerNumberModel( 8.00, QTConstants.ENERGY_RANGE.getLowerBound(), QTConstants.ENERGY_RANGE.getUpperBound(), 0.01 );
                _teSpinner = new JSpinner( model );
                _teSpinner.addChangeListener( _listener );
                _teSpinner.setPreferredSize( SPINNER_SIZE );
                _teSpinner.setMinimumSize( SPINNER_SIZE );
                JLabel teUnits = new JLabel( SimStrings.get( "units.energy" ) );
                inputPanelLayout.addAnchoredComponent( teLabel, row, 0, 2, 1, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( _teSpinner, row, 2 );
                inputPanelLayout.addComponent( teUnits, row, 3 );
                row++;
            }

            // Potential Energy for each region...
            {
                JLabel peTitle = new JLabel( SimStrings.get( "label.potentialEnergy" ) );
                peTitle.setForeground( QTConstants.POTENTIAL_ENERGY_COLOR );
                inputPanelLayout.addAnchoredComponent( peTitle, row, 0, 4, 1, GridBagConstraints.WEST );
                row++;
                int numberOfRegions = _potentialEnergy.getNumberOfRegions();
                _peSpinners = new ArrayList();
                for ( int i = 0; i < numberOfRegions; i++ ) {
                    JLabel peLabel = new JLabel( "<html>R<sub>" + ( i + 1 ) + "</sub>:</html>" );
                    peLabel.setForeground( QTConstants.POTENTIAL_ENERGY_COLOR );
                    SpinnerModel model = new SpinnerNumberModel( 5.00, QTConstants.ENERGY_RANGE.getLowerBound(), QTConstants.ENERGY_RANGE.getUpperBound(), 0.01 );
                    JSpinner peSpinner = new JSpinner( model );
                    peSpinner.addChangeListener( _listener );
                    peSpinner.setPreferredSize( SPINNER_SIZE );
                    peSpinner.setMinimumSize( SPINNER_SIZE );
                    _peSpinners.add( peSpinner );
                    JLabel peUnits = new JLabel( SimStrings.get( "units.energy" ) );
                    inputPanelLayout.addAnchoredComponent( peLabel, row, 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( peSpinner, row, 2, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( peUnits, row, 3, GridBagConstraints.WEST );
                    row++;
                }
            }

            // Step...
            _stepSpinner = null;
            if ( _potentialEnergy instanceof StepPotential ) {
                JLabel stepLabel = new JLabel( SimStrings.get( "label.stepPosition" ) );
                stepLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                SpinnerModel model = new SpinnerNumberModel( 5.00, QTConstants.POSITION_RANGE.getLowerBound(), QTConstants.POSITION_RANGE.getUpperBound(), 0.01 );
                _stepSpinner = new JSpinner( model );
                _stepSpinner.addChangeListener( _listener );
                _stepSpinner.setPreferredSize( SPINNER_SIZE );
                _stepSpinner.setMinimumSize( SPINNER_SIZE );
                JLabel stepUnits = new JLabel( SimStrings.get( "units.position" ) );
                inputPanelLayout.addAnchoredComponent( stepLabel, row, 0, 2, 1, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( _stepSpinner, row, 2 );
                inputPanelLayout.addComponent( stepUnits, row, 3 );
                row++;
            }
            
            // Barriers...
            _widthSpinners = null;
            _positionSpinners = null;
            if ( _potentialEnergy instanceof BarrierPotential ) {

                row = 1;
                int column = 5;

                int numberOfBarriers = ( (BarrierPotential) _potentialEnergy ).getNumberOfBarriers();

                // Barrier Widths...
                _widthSpinners = new ArrayList();
                JLabel widthTitle = new JLabel( SimStrings.get( "label.barrierWidth" ) );
                widthTitle.setForeground( BARRIER_PROPERTIES_COLOR );
                inputPanelLayout.addAnchoredComponent( widthTitle, row, column, 4, 1, GridBagConstraints.WEST );
                row++;
                column++;
                for ( int i = 0; i < numberOfBarriers; i++ ) {
                    JLabel widthLabel = new JLabel( "<html>B<sub>" + ( i + 1 ) + "</sub>:</html>" );
                    widthLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                    SpinnerModel widthModel = new SpinnerNumberModel( 5.00, 0, QTConstants.POSITION_RANGE.getUpperBound() - QTConstants.POSITION_RANGE.getLowerBound(), 0.01 );
                    JSpinner widthSpinner = new JSpinner( widthModel );
                    widthSpinner.addChangeListener( _listener );
                    widthSpinner.setPreferredSize( SPINNER_SIZE );
                    widthSpinner.setMinimumSize( SPINNER_SIZE );
                    _widthSpinners.add( widthSpinner );
                    JLabel widthUnits = new JLabel( SimStrings.get( "units.position" ) );
                    inputPanelLayout.addAnchoredComponent( widthLabel, row, column, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( widthSpinner, row, column + 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( widthUnits, row, column + 2, GridBagConstraints.WEST );
                    row++;
                }
                column--;

                // Barrier Positions...
                _positionSpinners = new ArrayList();
                JLabel positionTitle = new JLabel( SimStrings.get( "label.barrierPosition" ) );
                positionTitle.setForeground( BARRIER_PROPERTIES_COLOR );
                inputPanelLayout.addAnchoredComponent( positionTitle, row, column, 4, 1, GridBagConstraints.WEST );
                row++;
                column++;
                for ( int i = 0; i < numberOfBarriers; i++ ) {
                    JLabel positionLabel = new JLabel( "<html>B<sub>" + ( i + 1 ) + "</sub>:</html>" );
                    positionLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                    SpinnerModel positionModel = new SpinnerNumberModel( 2.00, QTConstants.POSITION_RANGE.getLowerBound(), QTConstants.POSITION_RANGE.getUpperBound(), 0.01 );
                    JSpinner positionSpinner = new JSpinner( positionModel );
                    positionSpinner.addChangeListener( _listener );
                    positionSpinner.setPreferredSize( SPINNER_SIZE );
                    positionSpinner.setMinimumSize( SPINNER_SIZE );
                    _positionSpinners.add( positionSpinner );
                    JLabel positionUnits = new JLabel( SimStrings.get( "units.position" ) );
                    inputPanelLayout.addAnchoredComponent( positionLabel, row, column, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( positionSpinner, row, column + 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( positionUnits, row, column + 2, GridBagConstraints.WEST );
                    row++;
                }
            }
        }

        JPanel inputPanel = new JPanel( new BorderLayout() );
        inputPanel.add( menuPanel, BorderLayout.NORTH );
        inputPanel.add( spinnerPanel, BorderLayout.CENTER );
        
        return inputPanel;
    }

    /** 
     * Creates the dialog's actions panel, consisting of Apply and Close buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _applyButton = new JButton( SimStrings.get( "button.apply" ) );
        _applyButton.addActionListener( _listener );

        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.addActionListener( _listener );

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 2, 10, 0 ) );
        buttonPanel.add( _applyButton );
        buttonPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }

    private void populateValues() {
        
        // Energy plot
        _energyPlot.setTotalEnergy( _totalEnergy );
        _energyPlot.setPotentialEnergy( _potentialEnergy );
        
        // Total Energy
        double te = _totalEnergy.getEnergy();
        _teSpinner.setValue( new Double( te ) );
        
        // Potential Energy
        for ( int i = 0; i < _peSpinners.size(); i++ ) {
            double pe = _potentialEnergy.getRegion(i).getEnergy();
            JSpinner peSpinner = (JSpinner) _peSpinners.get( i );
            peSpinner.setValue( new Double( pe ) );
        }
        
        // Step 
        if ( _stepSpinner != null ) {
            double position = _potentialEnergy.getRegion( 1 ).getStart();
            _stepSpinner.setValue( new Double( position ) );
        }
        
        // Barrier Width
        if ( _widthSpinners != null ) {
            for ( int i = 0; i < _widthSpinners.size(); i++ ) {
                JSpinner widthSpinner = (JSpinner) _widthSpinners.get( i );
                int regionIndex = BarrierPotential.toRegionIndex( i );
                double width = ( (BarrierPotential) _potentialEnergy ).getRegion( regionIndex ).getWidth();
                widthSpinner.setValue( new Double( width ) );
            }
        }
        
        // Barrier Positions
        if ( _positionSpinners != null ) {
            for ( int i = 0; i < _positionSpinners.size(); i++ ) {
                JSpinner positionSpinner = (JSpinner) _positionSpinners.get( i );
                int regionIndex = BarrierPotential.toRegionIndex( i );
                double position = ( (BarrierPotential) _potentialEnergy ).getRegion( regionIndex ).getStart();
                positionSpinner.setValue( new Double( position ) );
            }
        }
    }
    
    private void rebuildUI() {
        boolean visible = isVisible();
        if ( visible ) {
            setVisible( false );
        }
        _inputPanel.removeAll();
        _inputPanel.add( createInputPanel() );
        populateValues();
        pack();
        if ( visible ) {
            setVisible( true );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public TotalEnergy getTotalEnergy() {
        return _totalEnergy;
    }
    
    public AbstractPotentialEnergy getPotentialEnergy() {
        return _potentialEnergy;
    }
    
    //----------------------------------------------------------------------------
    // Event dispatcher
    //----------------------------------------------------------------------------

    private class EventListener implements ActionListener, ChangeListener, ItemListener {

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _applyButton ) {
                handleApply();
            }
            else if ( event.getSource() == _closeButton ) {
                handleClose();
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _teSpinner ) {
                handleTotalEnergyChange();
            }
            else if ( _peSpinners.contains( event.getSource() ) ) {
                handlePotentialEnergyChange( _peSpinners.indexOf( event.getSource() ) );
            }
            else if ( event.getSource() == _stepSpinner ) {
                handleStepPositionChange();
            }
            else if ( _widthSpinners.contains( event.getSource() ) ) {
                handleBarrierWidthChange( _widthSpinners.indexOf( event.getSource() ) );
            }
            else if ( _positionSpinners.contains( event.getSource() ) ) {
                handleBarrierPositionChange( _positionSpinners.indexOf( event.getSource() ) );
            }
        }

        public void itemStateChanged( ItemEvent event ) {
            if ( event.getSource() == _potentialComboBox ) {
                handlePotentialTypeChange();
            }
        }
    }

    //----------------------------------------------------------------------------
    // Button handlers
    //----------------------------------------------------------------------------

    private void handleApply() {
    //XXX change the original energy profile
        _unsavedChanges = false;
    }

    private void handleClose() {
        if ( _unsavedChanges ) {
            //XXX check for changes that haven't been applied
        }
        dispose();
    }
    
    private void handlePotentialTypeChange() {
        AbstractPotentialEnergy potentialEnergy = null;
        
        Object o = _potentialComboBox.getSelectedItem();
        if ( o == _constantItem ) {
            potentialEnergy = new ConstantPotential();
        }
        else if ( o == _stepItem ) {
            potentialEnergy = new StepPotential();
        }
        else if ( o == _barrierItem ) {
            potentialEnergy = new BarrierPotential();
        }
        else if ( o == _doubleBarrierItem ) {
            potentialEnergy = new BarrierPotential( 2 );
        }
        
        if ( potentialEnergy != null ) {
            _potentialEnergy = potentialEnergy;
            rebuildUI();
        }
    }
    
    private void handleTotalEnergyChange() {
        Double value = (Double) _teSpinner.getValue();
        _totalEnergy.setEnergy( value.doubleValue() );
        _unsavedChanges = true;
    }
    
    private void handlePotentialEnergyChange( int regionIndex ) {
        JSpinner peSpinner = (JSpinner) _peSpinners.get( regionIndex );
        Double value = (Double) peSpinner.getValue();
        _potentialEnergy.setEnergy( regionIndex, value.doubleValue() );
        _unsavedChanges = true;
    }
    
    private void handleStepPositionChange() {
        if ( _potentialEnergy instanceof StepPotential ) {
        Double value = (Double) _stepSpinner.getValue();
            ( (StepPotential) _potentialEnergy ).setStepPosition( value.doubleValue() );
            _unsavedChanges = true;
        }
    }
    
    private void handleBarrierWidthChange( int barrierIndex ) {
        if ( _potentialEnergy instanceof BarrierPotential ) {
            JSpinner positionSpinner = (JSpinner) _positionSpinners.get( barrierIndex );
            Double value = (Double) positionSpinner.getValue();
            ( (BarrierPotential) _potentialEnergy).setBarrierPosition( barrierIndex, value.doubleValue() );
            _unsavedChanges = true;
        }
    }
    
    private void handleBarrierPositionChange( int barrierIndex ) {
        if ( _potentialEnergy instanceof BarrierPotential ) {
            JSpinner widthSpinner = (JSpinner) _widthSpinners.get( barrierIndex );
            Double value = (Double) widthSpinner.getValue();
            ( (BarrierPotential) _potentialEnergy).setBarrierWidth( barrierIndex, value.doubleValue() );
            _unsavedChanges = true;
        }
    }
}
