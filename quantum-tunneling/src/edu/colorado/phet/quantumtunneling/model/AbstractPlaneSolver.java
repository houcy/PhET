/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * AbstractPlaneSolver is the base class for all classes that implement
 * closed-form solutions to the wave function equation for a plane wave.
 * <p>
 * Terms are defined as follows:
 * <code>
 * e = Euler's number
 * E = total energy, in eV
 * h = Planck's constant
 * i = sqrt(-1)
 * kn = wave number of region n, in 1/nm
 * m = mass, in eV/c^2
 * t = time, in fs
 * Vn = potential energy of region n, in eV
 * x = position, in nm
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractPlaneSolver implements IWaveFunctionSolver {
    
    // Direction of motion
    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;
    
    protected static final double PLANCKS_CONSTANT = 0.658;  // Planck's constant, in eV fs
    protected static final double ELECTRON_MASS = 5.7;  // mass, in eV/c^2
    
    // Static term used to calculate k:  2 * m / h^2
    private static final double K_COMMON = ( 2 * ELECTRON_MASS ) / ( PLANCKS_CONSTANT * PLANCKS_CONSTANT );

    private TotalEnergy _te;
    private AbstractPotential _pe;
    private Direction _direction;
    
    private Complex[] _k;
    
    /**
     * Constructor.
     * 
     * @param te
     * @param pe
     */
    public AbstractPlaneSolver( TotalEnergy te, AbstractPotential pe, Direction direction ) {
        _te = te;
        _pe = pe;
        _direction = direction;
        _k = new Complex[ pe.getNumberOfRegions() ];
        update();
    }
    
    protected TotalEnergy getTotalEnergy() {
        return _te;
    }
    
    protected AbstractPotential getPotentialEnergy() {
        return _pe;
    }
    
    public void setDirection( Direction direction ) {
        _direction = direction;
        update();
    }
    
    public Direction getDirection() {
        return _direction;
    }
    
    protected boolean isLeftToRight() {
        return ( _direction == Direction.LEFT_TO_RIGHT );
    }
    
    protected boolean isRightToLeft() {
        return ( _direction == Direction.RIGHT_TO_LEFT );
    }
    
    /*
     * Gets the boundary position between two regions.
     * 
     * @param regionIndex1 smaller index
     * @param regionIndex2 larger index
     * @return
     */
    protected double getBoundary( final int regionIndex1, final int regionIndex2 ) {
        if ( regionIndex1 + 1 != regionIndex2 ) {
            throw new IllegalArgumentException( "regionIndex1 + 1 != regionIndex2" );
        }
        if ( isLeftToRight() ) {
            return _pe.getEnd( regionIndex1 );
        }
        else {
            return _pe.getStart( flipRegionIndex( regionIndex1 ) );
        }
    }
    
    /*
     * Gets the k value for a specified region.
     * 
     * @param regionIndex
     * @return
     */
    protected Complex getK( final int regionIndex ) {
        if ( regionIndex < 0 || regionIndex > _k.length - 1  ) {
            throw new IndexOutOfBoundsException( "regionIndex out of range: " + regionIndex );
        }
        return _k[ regionIndex ];
    }
    
    /**
     * Updates the solver to match it's model.
     */
    public void update() {
        updateK();
        updateCoefficients(); // update coefficients after updating k values!
    }
    
    /*
     * Updates the k values.
     */
    private void updateK() {
        final double E = getTotalEnergy().getEnergy();
        final int numberOfRegions = getPotentialEnergy().getNumberOfRegions();
        for ( int i = 0; i < numberOfRegions; i++ ) {
            double V = 0;
            if ( isLeftToRight() ) {
                V = getPotentialEnergy().getEnergy( i );
            }
            else {
                V = getPotentialEnergy().getEnergy( flipRegionIndex( i ) );
            }
            _k[i] = solveK( E, V );
        }
    }
    
    /*
     * Updates the coeffiecients.
     */
    protected abstract void updateCoefficients();
    
    /*
     * Calculate the wave number, in units of 1/nm.
     * 
     * k = sqrt( 2 * m * (E-V) / h^2 )
     * 
     * If direction is right-to-left, multiply k by -1.
     * If E < V, the result is imaginary.
     *
     * @param E total energy, in eV
     * @param V potential energy, in eV
     * @return
     */
    private Complex solveK( final double E, final double V ) {
        final double value = Math.sqrt( K_COMMON * Math.abs( E - V ) );
        double real = 0;
        double imag = 0;
        if ( E >= V  ) {
            real = value;
        }
        else {
            imag = value;
        }
        if ( isRightToLeft() ) {
            real *= -1;
            imag *= -1;
        }
        return new Complex( real, imag );
    }
    
    /*
     * e^(ikx)
     * 
     * @param x position
     * @param k
     */
    protected static Complex commonTerm1( final double x, Complex k ) {
        MutableComplex c = new MutableComplex();
        c.setValue( Complex.I );
        c.multiply( k );
        c.multiply( x );
        c.exp();
        return c;
    }
    
    /*
     * e^(-ikx)
     * 
     * @param x position
     * @param regionIndex region index
     */
    protected static Complex commonTerm2( final double x, Complex k ) {
        MutableComplex c = new MutableComplex();
        c.setValue( Complex.I );
        c.multiply( k );
        c.multiply( -x );
        c.exp();
        return c;
    }
    
    /*
     * e^(-i*E*t/h)
     * 
     * @param t time 
     * @param E total energy
     */
    protected static Complex commonTerm3( final double t, final double E ) {
        final double h = PLANCKS_CONSTANT;
        MutableComplex c = new MutableComplex();
        c.setValue( Complex.I );
        c.multiply( -1 * E * t / h );
        c.exp();
        return c;
    }
    
    protected int flipRegionIndex( int regionIndex ) {
        // OK if region index is out of bounds!
        return getPotentialEnergy().getNumberOfRegions() - 1 - regionIndex;
    }
}
