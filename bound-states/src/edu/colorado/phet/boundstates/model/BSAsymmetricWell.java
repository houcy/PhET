/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.test.schmidt_lee.PotentialFunction;
import edu.colorado.phet.boundstates.test.schmidt_lee.Wavefunction;


/**
 * BSAsymmetricWell is the model of a potential composed on one Asymmetric well.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>offset
 * <li>width
 * <li>depth
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricWell extends BSAbstractPotential {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _width;
    private double _depth;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAsymmetricWell( BSParticle particle ) {
        this( particle,
              BSConstants.DEFAULT_ASYMMETRIC_WIDTH, 
              BSConstants.DEFAULT_ASYMMETRIC_DEPTH, 
              BSConstants.DEFAULT_ASYMMETRIC_OFFSET, 
              BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSAsymmetricWell( BSParticle particle, double width, double depth, double offset, double center ) {
        super( particle, 1, 0, offset, center );
        setWidth( width );
        setDepth( depth );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public double getWidth() {
        return _width;
    }

    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "invalid width: " + width );
        }
        if ( width != _width ) {
            _width = width;
            notifyObservers();
        }
    }

    public double getDepth() {
        return _depth;
    }

    public void setDepth( double depth ) {
        if ( depth < 0 ) {
            throw new IllegalArgumentException( "invalid depth: " + depth );
        }
        if ( depth != _depth ) {
            _depth = depth;
            notifyObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells != 1 ) {
            throw new UnsupportedOperationException( "mutiple wells not supported for asymmetric well" );
        }
        else {
            super.setNumberOfWells( numberOfWells );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractPotential implementation
    //----------------------------------------------------------------------------
    
    public BSWellType getWellType() {
        return BSWellType.ASYMMETRIC;
    }
    
    public int getStartingIndex() {
        return 1;
    }

    public double getEnergyAt( double position ) {
        assert( getNumberOfWells() == 1 );
        
        final double offset = getOffset();
        final double c = getCenter();
        final double w = getWidth();
        
        double energy = offset;
        if ( Math.abs( position - c ) <= w / 2 ) {
            energy = offset - Math.abs( c + w/2 - position ) * getDepth() / w;
        }
        return energy;
    }

    //HACK dummy eigenstates, evenly spaced between offset and depth
    public BSEigenstate[] getEigenstates() {
        System.out.println( "BSAsymmetricWell.getEigenestates, numberOfWells=" + getNumberOfWells() );//XXX
        
        ArrayList eigenstates = new ArrayList();

        for ( int nodes = 0; nodes < 10; nodes++ ) {
            try {
                PotentialFunction function = new PotentialFunctionAdapter( this );
                Wavefunction wavefunction = new Wavefunction( 0.5, -4, +4, 1000, nodes, function );
                double E = wavefunction.getE();
                eigenstates.add( new BSEigenstate( E ) );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
}
