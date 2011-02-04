// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A pair of boxes that show the number of molecules indicated by the equation coefficients.
 * Left box is for the reactants, right box is for the products.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxesNode extends PComposite {

    private final IntegerRange coefficientRange;
    private final HorizontalAligner aligner;
    private final PComposite moleculesParentNode;
    private final SimpleObserver coefficientsObserver;
    private Equation equation;
    private final RightArrowNode arrowNode;

    public BoxesNode( final Property<Equation> equationProperty, IntegerRange coefficientRange, HorizontalAligner aligner, final Property<Color> boxColorProperty ) {

        this.coefficientRange = coefficientRange;
        this.aligner = aligner;

        // boxes
        final BoxNode reactantsBoxNode = new BoxNode( aligner.getBoxSizeReference() );
        addChild( reactantsBoxNode );
        final BoxNode productsBoxNode = new BoxNode( aligner.getBoxSizeReference() );
        addChild( productsBoxNode );

        // right-pointing arrow
        arrowNode = new RightArrowNode( equationProperty.getValue().isBalanced() );
        addChild( arrowNode );

        // molecules
        moleculesParentNode = new PComposite();
        addChild( moleculesParentNode );

        // layout
        double x = 0;
        double y = 0;
        reactantsBoxNode.setOffset( x, y );
        moleculesParentNode.setOffset( x, y );
        x = aligner.getCenterXOffset() - ( arrowNode.getFullBoundsReference().getWidth() / 2 );
        y = reactantsBoxNode.getFullBoundsReference().getCenterY() - ( arrowNode.getFullBoundsReference().getHeight() / 2 );
        arrowNode.setOffset( x, y );
        x = reactantsBoxNode.getFullBoundsReference().getMaxX() + aligner.getBoxSeparation();
        y = reactantsBoxNode.getYOffset();
        productsBoxNode.setOffset( x, y );

        // coefficient changes
        coefficientsObserver = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        // equation changes
        this.equation = equationProperty.getValue();
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                BoxesNode.this.equation.removeCoefficientsObserver( coefficientsObserver );
                BoxesNode.this.equation = equationProperty.getValue();
                BoxesNode.this.equation.addCoefficientsObserver( coefficientsObserver );
            }
        } );
        // box color changes
        boxColorProperty.addObserver( new SimpleObserver() {
            public void update() {
                reactantsBoxNode.setPaint( boxColorProperty.getValue() );
                productsBoxNode.setPaint( boxColorProperty.getValue() );
            }
        } );
    }

    public void setMoleculesVisible( boolean moleculesVisible ) {
        moleculesParentNode.setVisible( moleculesVisible );
    }

    private void updateNode() {
        moleculesParentNode.removeAllChildren();
        updateMolecules( equation.getReactants(), aligner.getReactantXOffsets( equation ) );
        updateMolecules( equation.getProducts(), aligner.getProductXOffsets( equation ) );
        arrowNode.setHighlighted( equation.isBalanced() );
    }

    private void updateMolecules( EquationTerm[] terms, double[] xOffsets ) {
        assert( terms.length == xOffsets.length );
        final double yMargin = 10;
        final double rowHeight = ( aligner.getBoxSizeReference().getHeight() - ( 2 * yMargin ) ) / ( coefficientRange.getMax() );
        for ( int i = 0; i < terms.length; i++ ) {
            int numberOfMolecules = terms[i].getActualCoefficient();
            Image moleculeImage = terms[i].getMolecule().getImage();
            double y = yMargin + ( rowHeight / 2 );
            for ( int j = 0; j < numberOfMolecules; j++ ) {
                PImage imageNode = new PImage( moleculeImage );
                moleculesParentNode.addChild( imageNode );
                imageNode.setOffset( xOffsets[i] - ( imageNode.getFullBoundsReference().getWidth() / 2 ), y - ( imageNode.getFullBoundsReference().getHeight()  / 2 ) );
                y += rowHeight;
            }
        }
    }

    /**
     * A simple box.
     */
    private static class BoxNode extends PPath {
        public BoxNode( Dimension boxSize ) {
            super( new Rectangle2D.Double( 0, 0, boxSize.getWidth(), boxSize.getHeight() ) );
            setStrokePaint( Color.BLACK );
            setStroke( new BasicStroke( 1f ) );
        }
    }
}
