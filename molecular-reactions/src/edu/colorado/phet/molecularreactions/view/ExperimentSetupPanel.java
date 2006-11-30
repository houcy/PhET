/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.RangeLimitedIntegerTextField;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.modules.ComplexModule;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;

/**
 * ExperimentSetupPanel
 * <p/>
 * Contains the controls for setting up and running an experiment
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ExperimentSetupPanel extends JPanel implements Resetable {
    private JTextField numATF;
    private JTextField numBCTF;
    private JTextField numABTF;
    private JTextField numCTF;
    private MoleculeParamGenerator moleculeParamGenerator;
    private ComplexModule module;
    private MoleculeCounter moleculeACounter;
    private MoleculeCounter moleculeBCCounter;
    private MoleculeCounter moleculeABCounter;
    private MoleculeCounter moleculeCCounter;

    private boolean hasBeenReset = true;
    private JButton resetBtn;
    private GoStopBtn goStopBtn;

    /**
     * @param module
     */
    public ExperimentSetupPanel( ComplexModule module ) {
        super( new GridBagLayout() );
        this.module = module;

        moleculeACounter = new MoleculeCounter( MoleculeA.class, module.getMRModel() );
        moleculeBCCounter = new MoleculeCounter( MoleculeBC.class, module.getMRModel() );
        moleculeABCounter = new MoleculeCounter( MoleculeAB.class, module.getMRModel() );
        moleculeCCounter = new MoleculeCounter( MoleculeC.class, module.getMRModel() );

        // Create a generator for molecule parameters
        Rectangle2D r = module.getMRModel().getBox().getBounds();
        Rectangle2D generatorBounds = new Rectangle2D.Double( r.getMinX() + 20,
                                                              r.getMinY() + 20,
                                                              r.getWidth() - 40,
                                                              r.getHeight() - 40 );
        moleculeParamGenerator = new RandomMoleculeParamGenerator( generatorBounds,
                                                                   5,
                                                                   .1,
                                                                   0,
                                                                   Math.PI * 2 );

        // Create the controls
        JLabel topLineLbl = new JLabel( SimStrings.get( "ExperimentSetup.topLine" ) );
        JLabel numALbl = new JLabel( SimStrings.get( "ExperimentSetup.numA" ) );
        JLabel numBCLbl = new JLabel( SimStrings.get( "ExperimentSetup.numBC" ) );
        JLabel numABLbl = new JLabel( SimStrings.get( "ExperimentSetup.numAB" ) );
        JLabel numCLbl = new JLabel( SimStrings.get( "ExperimentSetup.numC" ) );

        int maxMolecules = MRConfig.MAX_MOLECULE_CNT;
        numATF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numBCTF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numABTF = new RangeLimitedIntegerTextField( 0, maxMolecules );
        numCTF = new RangeLimitedIntegerTextField( 0, maxMolecules );

        // Must create reset button before the stop/go button, because the
        // stop/go button references the reset button
        resetBtn = new JButton( SimStrings.get( "ExperimentSet.reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        goStopBtn = new GoStopBtn( module, this );

        // Add a border
        setBorder( ControlBorderFactory.createPrimaryBorder( "Experimental Controls" ) );

        // Lay out the controls
        GridBagConstraints labelGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.WEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 2, 3, 3, 3 ),
                                                              0, 0 );
        GridBagConstraints textFieldGbc = new GridBagConstraints( 1, 1,
                                                                  1, 1, 1, 1,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets( 2, 3, 3, 3 ),
                                                                  0, 0 );
        // Header
        labelGbc.gridwidth = 4;
        add( topLineLbl, labelGbc );

        // Labels
        labelGbc.gridwidth = 1;
        labelGbc.anchor = GridBagConstraints.EAST;
        add( numALbl, labelGbc );
        add( numABLbl, labelGbc );
        labelGbc.gridy = 0;
        labelGbc.gridx = 2;
        labelGbc.gridy = GridBagConstraints.RELATIVE;
        add( numBCLbl, labelGbc );
        add( numCLbl, labelGbc );

        // Text fields
        textFieldGbc.gridy = GridBagConstraints.RELATIVE;
        add( numATF, textFieldGbc );
        add( numABTF, textFieldGbc );
        textFieldGbc.gridy = 1;
        textFieldGbc.gridx = 3;
        textFieldGbc.gridy = GridBagConstraints.RELATIVE;
        add( numBCTF, textFieldGbc );
        add( numCTF, textFieldGbc );

        labelGbc.gridx = 0;
        labelGbc.gridwidth = 4;
        labelGbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( "Select a reaction:" ), labelGbc );
        labelGbc.anchor = GridBagConstraints.CENTER;
        add( new ReactionChooserComboBox( module ), labelGbc );
        add( goStopBtn, labelGbc );
        add( resetBtn, labelGbc );
    }

    /**
     * Adds molecules to the model as specified in the controls
     */
    private void startExperiment() {

        if( hasBeenReset ) {
            int dA = Integer.parseInt( numATF.getText() ) - moleculeACounter.getCnt();
            int dBC = Integer.parseInt( numBCTF.getText() ) - moleculeBCCounter.getCnt();
            int dAB = Integer.parseInt( numABTF.getText() ) - moleculeABCounter.getCnt();
            int dC = Integer.parseInt( numCTF.getText() ) - moleculeCCounter.getCnt();

            generateMolecules( MoleculeA.class, dA );
            generateMolecules( MoleculeBC.class, dBC );
            generateMolecules( MoleculeAB.class, dAB );
            generateMolecules( MoleculeC.class, dC );

            module.setStripChartVisible( true );
            module.rescaleStripChart();
            hasBeenReset = false;
        }
    }

    private void generateMolecules( Class moleculeClass, int numMolecules ) {
        MRModel model = module.getMRModel();

        // Adding molecules?
        if( numMolecules > 0 ) {
            for( int i = 0; i < numMolecules; i++ ) {
                AbstractMolecule m = MoleculeFactory.createMolecule( moleculeClass,
                                                                     moleculeParamGenerator );
                if( m instanceof CompositeMolecule ) {
                    CompositeMolecule cm = (CompositeMolecule)m;
                    for( int j = 0; j < cm.getComponentMolecules().length; j++ ) {
                        model.addModelElement( cm.getComponentMolecules()[j] );
                    }
                }
                model.addModelElement( m );
            }
        }
        // Removing molecules?
        else {
            for( int i = numMolecules; i < 0; i++ ) {
                List modelElements = model.getModelElements();
                boolean moleculeRemoved = false;
                for( int j = 0; j < modelElements.size() && !moleculeRemoved; j++ ) {
                    Object o = modelElements.get( j );
                    if( moleculeClass.isInstance( o ) ) {
                        if( o instanceof CompositeMolecule ) {
                            CompositeMolecule cm = (CompositeMolecule)o;
                            for( int k = 0; k < cm.getComponentMolecules().length; k++ ) {
                                model.removeModelElement( cm.getComponentMolecules()[k] );
                            }
                        }
                        model.removeModelElement( (ModelElement)o );
                    }
                }
            }
        }
    }

    /**
     * Enables/disables appropriate controls
     *
     * @param isRunning
     */
    private void setExperimentRunning( boolean isRunning ) {
        module.setExperimentRunning( isRunning );
    }

    private void setInitialConditionsEditable( boolean editable ) {
        numATF.setEditable( editable );
        numBCTF.setEditable( editable );
        numABTF.setEditable( editable );
        numCTF.setEditable( editable );
    }

    /**
     * Resets everything
     */
    public void reset() {

        setInitialConditionsEditable( true );
        numATF.setText( "0" );
        numBCTF.setText( "0" );
        numABTF.setText( "0" );
        numCTF.setText( "0" );

        generateMolecules( MoleculeA.class, -moleculeACounter.getCnt() );
        generateMolecules( MoleculeBC.class, -moleculeBCCounter.getCnt() );
        generateMolecules( MoleculeAB.class, -moleculeABCounter.getCnt() );
        generateMolecules( MoleculeC.class, -moleculeCCounter.getCnt() );

        module.setStripChartVisible( false );
        goStopBtn.setState( GoStopBtn.stop );
        // This must come after the state of the goStopBtn has been set
        module.getClock().start();

        hasBeenReset = true;
//        setExperimentRunning( false );
    }


    /**
     * Three state button for controlling the experiment
     */
    private static class GoStopBtn extends JButton {
        static Object go = new Object();
        static Object stop = new Object();
        static Object setup = new Object();
        private Object state = setup;
        private String goString = SimStrings.get( "ExperimentSetup.go" );
        private String stopString = SimStrings.get( "ExperimentSetup.stop" );
        private String setupString = SimStrings.get( "ExperimentSetup.setup" );
        private Color goColor = Color.green;
        private Color stopColor = Color.red;
        private Color setupColor = Color.yellow;
        private IClock clock;
        private ComplexModule module;
        private ExperimentSetupPanel parent;

        public GoStopBtn( ComplexModule module, ExperimentSetupPanel parent ) {
            this.parent = parent;
            this.clock = module.getClock();
            this.module = module;
            setState( stop );
            addActionListener( new ActionHandler() );
        }

        void setState( Object state ) {
            this.state = state;
            if( state == go ) {
                clock.start();
                parent.startExperiment();
                setText( stopString );
                setBackground( stopColor );
                module.setExperimentRunning( true );
                parent.setInitialConditionsEditable( false );
            }
            if( state == stop ) {
                clock.pause();
                setText( goString );
                setBackground( goColor );
                parent.setExperimentRunning( false );
            }
        }


        private class ActionHandler implements ActionListener {
            public void actionPerformed( ActionEvent e ) {
                if( state == go ) {
                    setState( stop );
                }
                else if( state == stop ) {
                    setState( go );
                }
            }
        }
    }
}
