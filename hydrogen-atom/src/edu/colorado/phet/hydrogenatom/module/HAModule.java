/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.module;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.AtomicModelSelector;
import edu.colorado.phet.hydrogenatom.control.GunControlPanel;
import edu.colorado.phet.hydrogenatom.control.HAClockControlPanel;
import edu.colorado.phet.hydrogenatom.control.ModeSwitch;
import edu.colorado.phet.hydrogenatom.energydiagrams.BohrEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.DeBroglieEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SchrodingerEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SolarSystemEnergyDiagram;
import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
import edu.colorado.phet.hydrogenatom.model.HAClock;
import edu.colorado.phet.hydrogenatom.spectrometer.Spectrometer;
import edu.colorado.phet.hydrogenatom.view.*;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;


public class HAModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 750, 750 );
    
    private static final Dimension BLACK_BOX_SIZE = new Dimension( 475, 475 );
    private static final double BLACK_BOX_DEPTH = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPCanvas _canvas;
    private PNode _rootNode;
    
    private HAController _controller;
    private ModeSwitch _modeSwitch;
    private AtomicModelSelector _atomicModelSelector;
    private GunNode _gunNode;
    private GunControlPanel _gunControlPanel;
    private PText _notToScaleLabel;
    private JCheckBox _energyDiagramCheckBox;
    private PNode _energyDiagramCheckBoxNode;
    private JCheckBox _spectrometerCheckBox;
    private PNode _spectrometerCheckBoxNode;
    
    private BlackBox _blackBox;
    
    private ExperimentAtomNode _experimentAtomNode;
    private BilliardBallAtomNode _billiardBallAtomNode;
    private BohrAtomNode _bohrAtomNode;
    private DeBroglieAtomNode _deBroglieAtomNode;
    private PlumPuddingAtomNode _plumPuddingAtomNode;
    private SchrodingerAtomNode _schrodingerAtomNode;
    private SolarSystemAtomNode _solarSystemAtomNode;
    
    private BohrEnergyDiagram _bohrEnergyDiagram;
    private DeBroglieEnergyDiagram _deBroglieEnergyDiagram;
    private SchrodingerEnergyDiagram _schrodingerEnergyDiagram;
    private SolarSystemEnergyDiagram _solarSystemEnergyDiagram;
    
    private Spectrometer _spectrometer;
    private ArrayList _spectrometerSnapshots; // list of Spectrometer
    
    private HAClockControlPanel _clockControlPanel;
    private int _spectrumSnapshotCounter; // incremented each time a spectrometer snapshot is taken
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAModule() {
        super( SimStrings.get( "HAModule.title" ), new HAClock() );
        
        // hide the PhET logo
        setLogoPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------
        
        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( CANVAS_RENDERING_SIZE );
            _canvas.setBackground( HAConstants.CANVAS_BACKGROUND );
            setSimulationPanel( _canvas );
            
            _canvas.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    updateCanvasLayout();
                }
            } );
        }
        
        // Root of our scene graph
        {
            _rootNode = new PNode();
            _canvas.addWorldChild( _rootNode );
        }
        
        // Mode switch (experiment/prediction)
        {
            _modeSwitch = new ModeSwitch();
            _rootNode.addChild( _modeSwitch );
        }
        
        // Atomic Model selector
        {
            _atomicModelSelector = new AtomicModelSelector();
            _rootNode.addChild( _atomicModelSelector );
        }
        
        // Gun
        {
            _gunNode = new GunNode( _canvas );
            _rootNode.addChild( _gunNode );
            
            _gunControlPanel = new GunControlPanel( _canvas );
            _rootNode.addChild( _gunControlPanel );
        }
        
        // "Not to scale" label
        {
            _notToScaleLabel = new PText( SimStrings.get( "label.notToScale" ) );
            _notToScaleLabel.setFont( new Font( HAConstants.FONT_NAME, Font.PLAIN, 14 ) );
            _rootNode.addChild( _notToScaleLabel );
        }
        
        // Energy Diagram checkbox
        {
            _energyDiagramCheckBox = new JCheckBox( SimStrings.get( "label.showEnergyDiagram" ) );
            _energyDiagramCheckBox.setOpaque( false );
            _energyDiagramCheckBox.setFont( HAConstants.CONTROL_FONT );
            _energyDiagramCheckBoxNode = new PSwing( _canvas, _energyDiagramCheckBox );
            _rootNode.addChild( _energyDiagramCheckBoxNode );
        }
        
        // Energy diagrams
        {
            _bohrEnergyDiagram = new BohrEnergyDiagram();
            _deBroglieEnergyDiagram = new DeBroglieEnergyDiagram();
            _schrodingerEnergyDiagram = new SchrodingerEnergyDiagram();
            _solarSystemEnergyDiagram = new SolarSystemEnergyDiagram();
            
            _rootNode.addChild( _bohrEnergyDiagram );
            _rootNode.addChild( _deBroglieEnergyDiagram );
            _rootNode.addChild( _schrodingerEnergyDiagram );
            _rootNode.addChild( _solarSystemEnergyDiagram );
        }
        
        // Spectrometer checkbox
        {
            _spectrometerCheckBox = new JCheckBox( SimStrings.get( "label.showSpectrometer" ) );
            _spectrometerCheckBox.setOpaque( false );
            _spectrometerCheckBox.setFont( HAConstants.CONTROL_FONT );
            _spectrometerCheckBoxNode = new PSwing( _canvas, _spectrometerCheckBox );
            _rootNode.addChild( _spectrometerCheckBoxNode );
        }
        
        // Spectrometer
        {
            String title = SimStrings.get( "label.photonsEmitted" );
            _spectrometer = new Spectrometer( _canvas, title, false /* isaSnapshot */ );
            _rootNode.addChild( _spectrometer );
            
            _spectrometerSnapshots = new ArrayList();
        }
        
        // Atom representations
        {
            _experimentAtomNode = new ExperimentAtomNode();
            _billiardBallAtomNode = new BilliardBallAtomNode();
            _bohrAtomNode = new BohrAtomNode();
            _deBroglieAtomNode = new DeBroglieAtomNode();
            _plumPuddingAtomNode = new PlumPuddingAtomNode();
            _schrodingerAtomNode = new SchrodingerAtomNode();
            _solarSystemAtomNode = new SolarSystemAtomNode();
            
//            _rootNode.addChild( _experimentAtomNode );
//            _rootNode.addChild( _billiardBallAtomNode );
//            _rootNode.addChild( _bohrAtomNode );
//            _rootNode.addChild( _deBroglieAtomNode );
//            _rootNode.addChild( _plumPuddingAtomNode );
//            _rootNode.addChild( _schrodingerAtomNode );
//            _rootNode.addChild( _solarSystemAtomNode );
        }
        
        // Magic Box
        {
           _blackBox = new BlackBox( BLACK_BOX_SIZE.width, BLACK_BOX_SIZE.height, BLACK_BOX_DEPTH ); 
           _rootNode.addChild( _blackBox.getBackNode() );
           _rootNode.addChild( _blackBox.getFrontNode() );
        }
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Clock controls
        {
            _clockControlPanel = new HAClockControlPanel( (HAClock) getClock() );
            setClockControlPanel( _clockControlPanel );
        }
        
        _controller = new HAController( this, 
                _modeSwitch, _atomicModelSelector, _gunNode, _gunControlPanel,
                _energyDiagramCheckBox, _spectrometer, _spectrometerCheckBox );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        if ( hasHelp() ) {
            HelpPane helpPane = getDefaultHelpPane();
            
            //XXX add help items
        }
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
        
        reset();
        updateCanvasLayout();
    }
    
    //----------------------------------------------------------------------------
    //
    //----------------------------------------------------------------------------
    
    private void reset() {
        _modeSwitch.setPredictionSelected();
        _atomicModelSelector.setSelection( AtomicModel.BILLIARD_BALL );
        _blackBox.open();
        _gunNode.getGunOnOffControl().setOn( false );
        _gunControlPanel.getGunTypeControl().setLightSelected();
        _gunControlPanel.getLightTypeControl().setMonochromaticSelected();
        _gunControlPanel.getLightIntensityControl().setValue( 100 );
        _gunControlPanel.getWavelengthControl().setWavelength( VisibleColor.MIN_WAVELENGTH );
        _gunControlPanel.getAlphaParticlesIntensityControl().setValue( 100 );
        _spectrometerCheckBox.setSelected( true );
        _energyDiagramCheckBox.setSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    public void updateCanvasLayout() {
        
        // Determine the visible bounds in world coordinates
        Dimension2D dim = new PDimension( _canvas.getWidth(), _canvas.getHeight() );
        _canvas.getPhetRootNode().screenToWorld( dim ); // this modifies dim!
        Dimension worldSize = new Dimension( (int)dim.getWidth(), (int)dim.getHeight() );

        // margins and spacing
        final double xMargin = 20;
        final double yMargin = 20;
        final double xSpacing = 20;
        final double ySpacing = 10;
        
        // reusable (x,y) coordinates, for setting offsets
        double x, y;
        
        // Mode Switch, in upper left corner
        {
            _modeSwitch.setOffset( xMargin, yMargin );
        }
        
        // Atomic Model Selector, below mode selector, left aligned.
        {
            PBounds msb = _modeSwitch.getFullBounds();
            x = msb.getX();
            y = msb.getY() + msb.getHeight() + ySpacing;
            _atomicModelSelector.setOffset( x, y );
        }
        
        // Gun
        {
            PBounds ab = _atomicModelSelector.getFullBounds();
            x = ab.getX() + ab.getWidth() + xSpacing;
            y = 190;//XXX
            _gunNode.setOffset( x, y );
        }
        
        // Black box
        {
            _blackBox.setOffset( 400, 50 );//XXX
        }
        
        // Gun control panel
        {
            PBounds ab = _atomicModelSelector.getFullBounds();
            PBounds bb = _blackBox.getBackNode().getFullBounds();
            x = ab.getX() + ab.getWidth() + xSpacing;
            y = bb.getY() + bb.getHeight() + ySpacing;
            _gunControlPanel.setOffset( x, y );
        }
        
        // "Drawings are not to scale" note, centered above black box.
        {
            PBounds bb = _blackBox.getBackNode().getFullBounds();
            x = bb.getX() + ( ( bb.getWidth() - _notToScaleLabel.getFullBounds().getWidth() ) / 2 );
            y = ( bb.getY() - _notToScaleLabel.getFullBounds().getHeight() ) / 2;
            _notToScaleLabel.setOffset( x, y );
        }
        
        // Energy Diagram, to the right of the black box.
        {
            PBounds bb = _blackBox.getBackNode().getFullBounds();
            x = bb.getX() + bb.getHeight() + 10;
            y = yMargin;
            _energyDiagramCheckBoxNode.setOffset( x, y );
            
            // Diagram is below checkbox, left aligned.
            PBounds b = _energyDiagramCheckBoxNode.getFullBounds();
            x = b.getX();
            y = b.getY() + b.getHeight() + 10;
            _bohrEnergyDiagram.setOffset( x, y );
            _deBroglieEnergyDiagram.setOffset( x, y );
            _schrodingerEnergyDiagram.setOffset( x, y );
            _solarSystemEnergyDiagram.setOffset( x, y );
        }
        
        // Spectrometer, in bottom right corner.
        {
            PBounds bb = _blackBox.getBackNode().getFullBounds();
            PBounds gb = _gunNode.getFullBounds();
            final double gunRightEdge = gb.getX() + gb.getWidth() + xSpacing;
            x = Math.max( gunRightEdge, worldSize.getWidth() - _spectrometer.getFullBounds().getWidth() - xMargin );
            y = bb.getY() + bb.getHeight() + ySpacing;
            _spectrometer.setOffset( x, y );
            _spectrometerCheckBoxNode.setOffset( x + 10, y + 5 );
        }
        
        //XXX temporary
        {
            x = 300;
            y = 135;
            _experimentAtomNode.setOffset( x, y );
            _billiardBallAtomNode.setOffset( x, y );
            _bohrAtomNode.setOffset( x, y );
            _deBroglieAtomNode.setOffset( x, y );
            _plumPuddingAtomNode.setOffset( x, y );
            _schrodingerAtomNode.setOffset( x, y );
            _solarSystemAtomNode.setOffset( x, y );
        }
    }
    
    public void updateBlackBox() {
        _blackBox.setOpen( _modeSwitch.isPredictionSelected() );
    }
    
    public void updateAtomicModelSelector() {
        _atomicModelSelector.setVisible( _modeSwitch.isPredictionSelected() );
    }
    
    public void updateAtomicModel() {
        
        AtomicModel atomicModel = _atomicModelSelector.getSelection();
        
        _experimentAtomNode.setVisible( false );
        _billiardBallAtomNode.setVisible( false );
        _bohrAtomNode.setVisible( false );
        _deBroglieAtomNode.setVisible( false );
        _plumPuddingAtomNode.setVisible( false );
        _schrodingerAtomNode.setVisible( false );
        _solarSystemAtomNode.setVisible( false );
        
        if ( _modeSwitch.isExperimentSelected() ) {
            _experimentAtomNode.setVisible( true );
        }
        else {
            if ( atomicModel == AtomicModel.BILLIARD_BALL ) {
                _billiardBallAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.BOHR ) {
                _bohrAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _deBroglieAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.PLUM_PUDDING ) {
                _plumPuddingAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _schrodingerAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _solarSystemAtomNode.setVisible( true );
            }
        }
    }
    
    public void updateEnergyDiagram() {
        
        AtomicModel atomicModel = _atomicModelSelector.getSelection();
        
        _energyDiagramCheckBoxNode.setVisible( false );
        _bohrEnergyDiagram.setVisible( false );
        _deBroglieEnergyDiagram.setVisible( false );
        _schrodingerEnergyDiagram.setVisible( false );
        _solarSystemEnergyDiagram.setVisible( false );
        
        if ( _modeSwitch.isPredictionSelected() ) {
            if ( atomicModel == AtomicModel.BOHR ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _bohrEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _deBroglieEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _schrodingerEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _solarSystemEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
        }
    }
    
    public void updateSpectrometer() {
        final boolean visible = _spectrometerCheckBox.isSelected();
        _spectrometer.setVisible( visible );
        Iterator i = _spectrometerSnapshots.iterator();
        while ( i.hasNext() ) {
            Spectrometer spectrometer = (Spectrometer)i.next();
            spectrometer.setVisible( visible );
        }
    }
    
    public void createSpectrometerSnapshot() {
        
        _spectrumSnapshotCounter++;
        
        String title = SimStrings.get( "label.snapshot") + " " + _spectrumSnapshotCounter + ": ";
        if ( _modeSwitch.isPredictionSelected() ) {
            //XXX replace this call, the title may contain HTML markup
            title +=  _atomicModelSelector.getSelectionName();
        }
        else {
            title += SimStrings.get( "title.spectrometer.experiment" );
        }
        
        final Spectrometer spectrometer = new Spectrometer( _canvas, title, true /* isaSnapshot */ );
        
        _rootNode.addChild( spectrometer );
        _controller.addSpectrometerListener( spectrometer );
        _spectrometerSnapshots.add( spectrometer );
        
        PBounds sb = _spectrometer.getFullBounds();
        double x = sb.getX();
        double y = sb.getY() - spectrometer.getFullBounds().getHeight() - ( 10 * _spectrometerSnapshots.size() );
        spectrometer.setOffset( x, y );
    }
    
    public void deleteSpectrometerSnapshot( Spectrometer spectrometer ) {
        if ( spectrometer == _spectrometer ) {
            _spectrometerCheckBox.setSelected( false );
        }
        else {
            _rootNode.removeChild( spectrometer );
            _spectrometerSnapshots.remove( spectrometer );
        }
    }
}
