/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.draghandles;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.ValueAxis;

import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSCoulomb1DSpacingHandle
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DSpacingHandle extends AbstractHandle implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    private BSCoulomb1DPotential _potential;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSCoulomb1DSpacingHandle( BSCoulomb1DPotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( AbstractHandle.HORIZONTAL );
        
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
        setPotential( potential );
        
        int significantDecimalPlaces = potentialSpec.getSpacingRange().getSignificantDecimalPlaces();
        String numberFormat = createNumberFormat( significantDecimalPlaces );
        setValueNumberFormat( numberFormat );
        setValuePattern( SimStrings.get( "drag.spacing" ) );
        
        updateDragBounds();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSCoulomb1DPotential potential ) {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
        }
        _potential = potential;
        _potential.addObserver( this );
        updateView();
    }
    
    public BSCoulomb1DPotential getPotential() {
        return _potential;
    }
    
    //----------------------------------------------------------------------------
    // Bounds
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds.
     */
    public void updateDragBounds() {
        assert( _potential.getCenter() == 0 );
        
//        final int n = _potential.getNumberOfWells();
//        final double center = _potential.getCenter( n - 1 ); // center of the well that we're attaching the handle to
//        final double width = _potential.getWidth();
//        final double separation = _potential.getSeparation();
//        final double centerOfSeparation = center - ( width / 2 ) - ( separation / 2 );
//        final double minSeparation = _potentialSpec.getSeparationRange().getMin();
//        final double maxSeparation = _potentialSpec.getSeparationRange().getMax();
//        
//        // position -> x coordinates
//        final double minPosition = centerOfSeparation + ( minSeparation / 2 );
//        final double maxPosition = centerOfSeparation + ( maxSeparation / 2 );
//        final double minX = _chartNode.positionToNode( minPosition );
//        final double maxX = _chartNode.positionToNode( maxPosition );
//        
//        // energy -> y coordinates (+y is down!)
//        ValueAxis yAxis = _chartNode.getEnergyPlot().getRangeAxis();
//        final double minEnergy = yAxis.getLowerBound();
//        final double maxEnergy =  yAxis.getUpperBound();
//        final double minY = _chartNode.energyToNode( maxEnergy );
//        final double maxY = _chartNode.energyToNode( minEnergy );
//        
//        // bounds, local coordinates
//        final double w = maxX - minX;
//        final double h = maxY - minY;
//        Rectangle2D dragBounds = new Rectangle2D.Double( minX, minY, w, h );
//
//        // Convert to global coordinates
//        dragBounds = _chartNode.localToGlobal( dragBounds );
//
//        setDragBounds( dragBounds );
        updateView();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDragHandle implementation
    //----------------------------------------------------------------------------

    protected void updateModel() {
        assert ( _potential.getCenter() == 0 );

        _potential.deleteObserver( this );
        {
//            Point2D globalNodePoint = getGlobalPosition();
//            Point2D localNodePoint = _chartNode.globalToLocal( globalNodePoint );
//            Point2D modelPoint = _chartNode.nodeToEnergy( localNodePoint );
//            final double d = modelPoint.getX();
//
//            final int n = _potential.getNumberOfWells();
//            final double w = _potential.getWidth();
//            double separation = 0;
//            
//            if ( n % 2 == 0 ) {
//                // even number of wells
//                final int m = ( n / 2 ) - 1;
//                separation = ( 1.0 / ( m + 0.5 ) ) * ( d - ( m * w ) );
//            }
//            else {
//                // odd number of wells
//                final int m = ( n - 1 ) / 2;
//                separation = ( 1.0 / m ) * ( d - ( ( m - 1 ) * w ) - ( w / 2.0 ) );
//            }
//
//            _potential.setSeparation( separation );
//            setValueDisplay( separation );
        }
        _potential.addObserver( this );
        updateDragBounds();
    }
    
    protected void updateView() {
        assert( _potential.getCenter() == 0 );
        
        removePropertyChangeListener( this );
        {
            final double spacing = _potential.getSpacing();
            final int n = _potential.getNumberOfWells();
            double position = 0;
            if ( n % 2 == 0 ) {
                // even number of wells
                position = spacing / 2;
            }
            else {
                // odd number of wells
                position = spacing;
            }
            
            final double energy = _potential.getOffset() - 5;
            Point2D modelPoint = new Point2D.Double( position, energy );
            Point2D localNodePoint = _chartNode.energyToNode( modelPoint );
            Point2D globalNodePoint = _chartNode.localToGlobal( localNodePoint );
            setGlobalPosition( globalNodePoint );
            setValueDisplay( spacing );
        }
        addPropertyChangeListener( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view when the model changes.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        assert( o == _potential );
        updateDragBounds();
        updateView();
    }
}
