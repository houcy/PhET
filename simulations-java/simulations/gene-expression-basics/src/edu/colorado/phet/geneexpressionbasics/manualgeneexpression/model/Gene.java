// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * This class is the model representation of a gene on a DNA molecule.  Each
 * gene consists of a regulatory region and a transcribed region.  In real
 * life, a gene is just a collection of base pairs on the DNA strand.  This
 * class essentially says where on the strand the gene exists.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class Gene {

    private static final double REG_REGION_PROPORTION = 0.25;

    private final DnaMolecule dnaMolecule;
    private final Color regulatoryRegionColor;
    private final Color transcribedRegionColor;
    private final DoubleRange regulatoryRegion;
    private final DoubleRange transcribedRegion;
    private final Shape transcribedRegionShape;
    private final Shape regulatoryRegionShape;

    /**
     * Constructor.
     *
     * @param dnaMolecule            The DNA molecule within which this gene exists.
     * @param regulatoryRegion       The range, with respect to the left edge of
     *                               the DNA molecule, where the regulatory region exists.
     * @param regulatoryRegionColor
     * @param transcribedRegion      The range, with respect to the left edge of
     *                               the DNA molecule, where the transcribed region exists.
     * @param transcribedRegionColor
     */
    public Gene( DnaMolecule dnaMolecule, DoubleRange regulatoryRegion, Color regulatoryRegionColor,
                 DoubleRange transcribedRegion, Color transcribedRegionColor ) {
        this.dnaMolecule = dnaMolecule;
        this.regulatoryRegion = regulatoryRegion;
        this.regulatoryRegionColor = regulatoryRegionColor;
        this.transcribedRegion = transcribedRegion;
        this.transcribedRegionColor = transcribedRegionColor;
        transcribedRegionShape = createRegionShape( dnaMolecule, transcribedRegion );
        regulatoryRegionShape = createRegionShape( dnaMolecule, regulatoryRegion );
    }

    /**
     * Get a shape that is suitable for enclosing the transcribed region of
     * the gene.  This is used primarily by the view.
     */
    public Shape getTranscribedRegionShape() {
        return transcribedRegionShape;
    }

    /**
     * Get a shape that is suitable for enclosing the regulatory region of the
     * gene.  This is used primarily by the view.
     */
    public Shape getRegulatoryRegionShape() {
        return regulatoryRegionShape;
    }

    private Shape createRegionShape( DnaMolecule dnaMolecule, DoubleRange range ) {
        double rectHeight = dnaMolecule.getWidth() * 1.75;
        double rectWidth = range.getLength();
        double xPos = dnaMolecule.getLeftEdgePos().getX() + range.getMin();
        double yPos = dnaMolecule.getLeftEdgePos().getY() - rectHeight / 2;
        double rounding = rectHeight * 0.75; // Empirically chosen based on what looked good.
        return new RoundRectangle2D.Double( xPos, yPos, rectWidth, rectHeight, rounding, rounding );
    }

    public Color getRegulatoryRegionColor() {
        return regulatoryRegionColor;
    }

    public Color getTranscribedRegionColor() {
        return transcribedRegionColor;
    }

    public double getCenterX() {
        return ( Math.min( regulatoryRegion.getMin(), transcribedRegion.getMin() ) +
                 Math.max( regulatoryRegion.getMax(), transcribedRegion.getMax() ) ) / 2 + dnaMolecule.getLeftEdgePos().getX();
    }
}
