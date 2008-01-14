/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * ModelViewTransform provides the transforms between model and view coordinate systems.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelViewTransform {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final double _viewXScale;
    private final double _viewYScale;
    private final Point2D.Double _viewOrigin;
    
    private final AffineTransform _modelToViewTransform;
    private final AffineTransform _viewToModelTransform;
    
    private final Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor with model and view having the same scale and origin.
     */
    public ModelViewTransform() {
        this( 1, 1, new Point2D.Double( 0, 0 ) );
    }
    
    /**
     * Constructor with model and view having different scales, but same origin.
     * 
     * @param viewXScale x scale for mapping from model to view
     * @param viewYScale y scale for mapping from model to view
     */
    public ModelViewTransform( double viewXScale, double viewYScale ) {
        this( viewXScale, viewYScale, new Point2D.Double( 0, 0 ) );
    }
    
    /**
     * Constructor with model and view having same scales, but different origin.
     * 
     * @param viewOrigin point in the view that corresponds to the model's origin
     */
    public ModelViewTransform( Point2D viewOrigin ) {
        this( 1, 1, viewOrigin );
    }
    
    /**
     * Constructor.
     * 
     * @param viewXScale x scale for mapping from model to view
     * @param viewYScale y scale for mapping from model to view
     * @param viewOrigin point in the view that corresponds to the model's origin
     */
    public ModelViewTransform( double viewXScale, double viewYScale, Point2D viewOrigin ) {
        
        _viewXScale = viewXScale;
        _viewYScale = viewYScale;
        _viewOrigin = new Point2D.Double( viewOrigin.getX(), viewOrigin.getY() );
        
        _modelToViewTransform = new AffineTransform();
        _modelToViewTransform.scale( viewXScale, viewYScale );
        _modelToViewTransform.translate( viewOrigin.getX(), viewOrigin.getY() );
        
        _viewToModelTransform = new AffineTransform();
        _viewToModelTransform.translate( -viewOrigin.getX(), -viewOrigin.getY() );
        _viewToModelTransform.scale( 1d/viewXScale, 1d/viewYScale );
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
    }
    
    //----------------------------------------------------------------------------
    // Model-to-view transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a point from model to view coordinates.
     * If pView is not null, the result is returned in pView.
     * 
     * @param pModel point in model coordinates
     * @param pView point in view coordinates, possibly null
     * @return point in view coordinates
     */
    public Point2D modelToView( Point2D pModel, Point2D pView ) {
        return _modelToViewTransform.transform( pModel, pView );
    }
    
    public Point2D modelToView( Point2D pModel ) {
        return modelToView( pModel, null );
    }
    
    public Point2D modelToView( double xModel, double yModel ) {
        return modelToView( xModel, yModel, null );
    }
    
    public Point2D modelToView( double xModel, double yModel, Point2D pView ) {
        return modelToView( new Point2D.Double( xModel, yModel ), pView );
    }
    
    /**
     * Maps a rectangle from model to view coordinates.
     * If rView is not null, the result is returned in rView.
     * 
     * @param rModel
     * @param rView
     * @return
     */
    public Rectangle2D modelToView( Rectangle2D rModel, Rectangle2D rView ) {
        // transform upper-left corner
        _pModel.setLocation( rModel.getX(), rModel.getY() );
        modelToView( _pModel, _pView );
        final double x1 = _pView.getX();
        final double y1 = _pView.getY();
        // transform lower-right corner
        _pModel.setLocation( rModel.getMaxX(), rModel.getMaxY() );
        modelToView( _pModel, _pView );
        final double x2 = _pView.getX();
        final double y2 = _pView.getY();
        // new non-empty rectangle
        final double x = ( x1 < x2 ) ? x1 : x2;
        final double y = ( y1 < y2 ) ? y1 : y2;
        final double w = Math.abs( x1 - x2 );
        final double h = Math.abs( y1 - y2 );
        // return value
        if ( rView == null ) {
            rView = new Rectangle2D.Double();
        }
        rView.setRect( x, y, w, h  );
        return rView;
    }
    
    //----------------------------------------------------------------------------
    // View-to-model transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a point from view to model coordinates.
     * If pModel is not null, the result is returned in pModel.
     * 
     * @param pView point in view coordinates
     * @param pModel point in model coordinates, possibly null
     * @return point in model coordinates
     */
    public Point2D viewToModel( Point2D pView, Point2D pModel ) {
        return _viewToModelTransform.transform( pView, pModel );
    }
    
    public Point2D viewToModel( Point2D pView ) {
        return viewToModel( pView, null );
    }
    
    public Point2D viewToModel( double xView, double yView ) {
        return viewToModel( new Point2D.Double( xView, yView ) );
    }
    
    public Point2D viewToModel( double xView, double yView, Point2D pModel ) {
        return viewToModel( new Point2D.Double( xView, yView ), pModel );
    }
    
    /**
     * Maps a rectangle from view to model to coordinates.
     * If rModel is not null, the result is returned in rModel.
     * 
     * @param rModel
     * @param rView
     * @return
     */
    public Rectangle2D viewToModel( Rectangle2D rView, Rectangle2D rModel ) {
        // transform upper-left corner
        _pView.setLocation( rView.getX(), rView.getY() );
        viewToModel( _pView, _pModel );
        final double x1 = _pModel.getX();
        final double y1 = _pModel.getY();
        // transform lower-right corner
        _pView.setLocation( rView.getMaxX(), rView.getMaxY() );
        viewToModel( _pView, _pModel );
        final double x2 = _pModel.getX();
        final double y2 = _pModel.getY();
        // new non-empty rectangle
        final double x = ( x1 < x2 ) ? x1 : x2;
        final double y = ( y1 < y2 ) ? y1 : y2;
        final double w = Math.abs( x1 - x2 );
        final double h = Math.abs( y1 - y2 );
        // return value
        if ( rModel == null ) {
            rModel = new Rectangle2D.Double();
        }
        rModel.setRect( x, y, w, h  );
        return rModel;
    }
}
