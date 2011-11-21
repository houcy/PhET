// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.BeingSynthesizedState;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.DetachingState;

/**
 * Base class for proteins.  Defines the methods used for growing a protein.
 *
 * @author John Blanco
 */
public abstract class Protein extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Max value for the growth factor, indicates that it is fully grown.
    public static final double MAX_GROWTH_FACTOR = 1;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // A value between 0 and 1 that defines how fully developed, or "grown"
    // this protein is.
    private double growthFactor = 0;

    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    protected Protein( GeneExpressionModel model, Ribosome parentRibosome, Shape initialShape, Color baseColor ) {
        super( model, initialShape, baseColor );
        behaviorState = new BeingSynthesizedState( this, parentRibosome );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void setGrowthFactor( double growthFactor ) {
        if ( this.growthFactor != growthFactor ) {
            this.growthFactor = growthFactor;
            shapeProperty.set( getShape( this.growthFactor ) );
        }
    }

    public double getGrowthFactor() {
        return growthFactor;
    }

    public void grow( double growthAmount ) {
        assert growthAmount >= 0;
        if ( growthAmount < 0 ) {
            // Ignore this.
            return;
        }
        setGrowthFactor( Math.min( growthFactor + growthAmount, 1 ) );
    }

    protected abstract Shape getShape( double growthFactor );

    /**
     * Method to get an untranslated (in terms of position, not language)
     * version of this protein's shape when it fully grown.  This is intended
     * for use in creating control panel shapes that match this protein's shape.
     *
     * @return Shape representing the fully developed protein.
     */
    public Shape getFullyGrownShape() {
        return getShape( MAX_GROWTH_FACTOR );
    }

    public abstract Protein createInstance( GeneExpressionModel model, Ribosome parentRibosome );

    /**
     * Release this protein from the ribosome and allow it to drift around in
     * the cell.
     */
    public void release() {
        behaviorState = new DetachingState( this, new ImmutableVector2D( 1, 1 ) );
    }

    public void setPositionOfAttachmentPoint( Point2D attachmentPoint ) {
        // TODO: This method probably needs to be unique for each protein.
        setPosition( attachmentPoint );
    }
}
