package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.NoSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionAdapter;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class BeakerControlsNode extends PNode {
    
    private static final String RATIO_PATTERN = ABSStrings.CHECK_BOX_RATIO;
    
    private final BeakerNode beakerNode;
    
    private final JPanel panel;
    private final JCheckBox dissociatedComponentsRatioCheckBox;
    private final JCheckBox hyroniumHydroxideRatioCheckBox;
    private final JCheckBox moleculeCountsCheckBox;
    private final JCheckBox beakerLabelCheckBox;
    private final PSwing pswing;
    
    public BeakerControlsNode( final BeakerNode beakerNode, Color background, AqueousSolution solution ) {
        this( beakerNode, background );
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    private BeakerControlsNode( final BeakerNode beakerNode, Color background ) {
        super();
        
        this.beakerNode = beakerNode;
        
        dissociatedComponentsRatioCheckBox = new JCheckBox( "?" );
        dissociatedComponentsRatioCheckBox.setSelected( beakerNode.isDisassociatedRatioComponentsVisible() );
        dissociatedComponentsRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerNode.setDisassociatedRatioComponentsVisible( dissociatedComponentsRatioCheckBox.isSelected() );
            }
        });
        
        Object[] args = { ABSSymbols.H3O_PLUS, ABSSymbols.OH_MINUS };
        String html = HTMLUtils.toHTMLString( MessageFormat.format( RATIO_PATTERN, args ) );
        hyroniumHydroxideRatioCheckBox = new JCheckBox( html );
        hyroniumHydroxideRatioCheckBox.setSelected( beakerNode.istHydroniumHydroxideRatioVisible() );
        hyroniumHydroxideRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerNode.setHydroniumHydroxideRatioVisible( hyroniumHydroxideRatioCheckBox.isSelected() );
            }
        });
        
        moleculeCountsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_MOLECULE_COUNTS );
        moleculeCountsCheckBox.setSelected( beakerNode.isMoleculeCountsVisible() );
        moleculeCountsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerNode.setMoleculeCountsVisible( moleculeCountsCheckBox.isSelected() );
            }
        });
        
        beakerLabelCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_BEAKER_LABEL );
        beakerLabelCheckBox.setSelected( beakerNode.isBeakerLabelVisible() );
        beakerLabelCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerNode.setBeakerLabelVisible( beakerLabelCheckBox.isSelected() );
            }
        });

        // border
        panel = new JPanel();
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_BEAKER_CONTROLS );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        panel.setBorder( border );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( dissociatedComponentsRatioCheckBox, row++, column );
        layout.addComponent( hyroniumHydroxideRatioCheckBox, row++, column );
        layout.addComponent( moleculeCountsCheckBox, row++, column );
        layout.addComponent( beakerLabelCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( panel, background );
        
        pswing = new PSwing( panel );
        addChild( pswing );
    }
    
    protected void setDissociatedComponents( String component1, String component2 ) {
        Object[] args = { component1, component2 };
        String html = HTMLUtils.toHTMLString( MessageFormat.format( RATIO_PATTERN, args ) );
        dissociatedComponentsRatioCheckBox.setText( html );
        pswing.computeBounds(); // workaround for #1670
    }
    
    protected void setDissociatedComponentsCheckBoxVisible( boolean visible ) {
        dissociatedComponentsRatioCheckBox.setVisible( visible );
    }
    
    public void setDissociatedComponentsRatioSelected( boolean b ) {
        dissociatedComponentsRatioCheckBox.setSelected( b );
        beakerNode.setDisassociatedRatioComponentsVisible( b );
    }
    
    public boolean isDissociatedComponentsRatioSelected() {
        return dissociatedComponentsRatioCheckBox.isSelected();
    }
    
    public void setHydroniumHydroxideRatioSelected( boolean b ) {
        hyroniumHydroxideRatioCheckBox.setSelected( b );
        beakerNode.setHydroniumHydroxideRatioVisible( b );
    }
    
    public boolean isHydroniumHydroxideRatioSelected() {
        return hyroniumHydroxideRatioCheckBox.isSelected();
    }
    
    public void setMoleculeCountsSelected( boolean b ) {
        moleculeCountsCheckBox.setSelected( b );
        beakerNode.setMoleculeCountsVisible( b );
    }
    
    public boolean isMoleculeCountsSelected() {
        return moleculeCountsCheckBox.isSelected();
    }
    
    public void setBeakerLabelSelected( boolean b ) {
        beakerLabelCheckBox.setSelected( b );
        beakerNode.setBeakerLabelVisible( b );
    }
    
    public boolean isBeakerLabelSelected() {
        return beakerLabelCheckBox.isSelected();
    }
    
    private static class ModelViewController extends SolutionAdapter {
        
        private final AqueousSolution solution;
        private final BeakerControlsNode beakerControlsNode; 

        public ModelViewController( AqueousSolution solution, BeakerControlsNode beakerControlsNode ) {
            this.solution = solution;
            this.beakerControlsNode = beakerControlsNode;
        }
        public void soluteChanged() {
            updateRatioLabels();
        }
        
        public void strengthChanged() {
            updateRatioLabels();
        }
        
        private void updateRatioLabels() {
            Solute solute = solution.getSolute();
            beakerControlsNode.setDissociatedComponentsCheckBoxVisible( !(solute instanceof NoSolute ) );
            if ( solute instanceof Acid ) {
                Acid acid = (Acid) solute;
                beakerControlsNode.setDissociatedComponents( acid.getSymbol(), acid.getConjugateSymbol() );
            }
            else if ( solute instanceof WeakBase ) {
                WeakBase base = (WeakBase) solute;
                beakerControlsNode.setDissociatedComponents( base.getSymbol(), base.getConjugateSymbol() );
            }
            else if ( solute instanceof StrongBase ) {
                StrongBase base = (StrongBase) solute;
                beakerControlsNode.setDissociatedComponents( base.getSymbol(), base.getMetalSymbol() );
            }
            else if ( solute instanceof CustomBase ) {
                CustomBase base = (CustomBase) solute;
                if ( base.isStrong() ) {
                    beakerControlsNode.setDissociatedComponents( base.getSymbol(), base.getMetalSymbol() );
                }
                else {
                    beakerControlsNode.setDissociatedComponents( base.getSymbol(), base.getConjugateSymbol() );
                }
            }
        }
    }
}
