package edu.colorado.phet.glaciers.test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import junit.framework.TestCase;
import edu.colorado.phet.glaciers.view.ModelViewTransform;

/**
 * ZModelViewTransformTester is the JUnit test for ModelViewTransform.
 * Tests a representative sample of common transforms, but is in no way complete.
 * Correct answers were calculated manually.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ZModelViewTransformTester extends TestCase {
    
    //----------------------------------------------------------------------------
    // Fixtures
    //----------------------------------------------------------------------------
    
    private ModelViewTransform _mvtIdentity; // identity transform
    private ModelViewTransform _mvtPositiveScale; // positive scaling
    private ModelViewTransform _mvtNegativeScale; // negative scaling
    private ModelViewTransform _mvtPositiveTranslation; // positive translation
    private ModelViewTransform _mvtNegativeTranslation; // negative translation
    private ModelViewTransform _mvtCombination;
    
    //----------------------------------------------------------------------------
    // setup
    //----------------------------------------------------------------------------
    
    public void setUp() {
        _mvtIdentity = new ModelViewTransform( 1, 1, 0, 0 );
        _mvtPositiveScale = new ModelViewTransform( 2, 3, 0, 0 );
        _mvtNegativeScale = new ModelViewTransform( -2, -3, 0, 0 );
        _mvtPositiveTranslation = new ModelViewTransform( 1, 1, 10, 20 );
        _mvtNegativeTranslation = new ModelViewTransform( 1, 1, -10, -20 );
        _mvtCombination = new ModelViewTransform( 2, -3, 10, 20 );
    }
    
    //----------------------------------------------------------------------------
    // Identity transform
    //----------------------------------------------------------------------------
    
    public void testIdentityTransform_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtIdentity.modelToView( pModel );
        assertTrue( pView.equals( pModel ) );
    }
    
    public void testIdentityTransform_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtIdentity.modelToView( rModel );
        assertTrue( rView.equals( rModel ) );
    }
    
    public void testIdentityTransform_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 10, 20 );
        Point2D pModel = _mvtIdentity.viewToModel( pView );
        assertTrue( pModel.equals( pView ) );
    }
    
    public void testIdentityTransform_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rModel = _mvtIdentity.viewToModel( rView );
        assertTrue( rModel.equals( rView ) );
    }

    //----------------------------------------------------------------------------
    // Positive scaling
    //----------------------------------------------------------------------------
    
    public void testPositiveScale_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtPositiveScale.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 20, 60 ); // 2,3
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testPositiveScale_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtPositiveScale.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 200, 600, 600, 1200 ); // 2,3
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testPositiveScale_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 20, 60 );
        Point2D pModel = _mvtPositiveScale.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // 1/2, 1/3
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testPositiveScale_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 200, 600, 600, 1200 );
        Rectangle2D rModel = _mvtPositiveScale.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // 1/2, 1/3
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Negative scaling
    //----------------------------------------------------------------------------
    
    public void testNegativeScale_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtNegativeScale.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( -20, -60 );  // -2,-3
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testNegativeScale_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtNegativeScale.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( -800, -1800, 600, 1200 ); // -2,-3
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testNegativeScale_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( -20, -60 );
        Point2D pModel = _mvtNegativeScale.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // -1/2, -1/3
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testNegativeScale_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( -800, -1800, 600, 1200 );
        Rectangle2D rModel = _mvtNegativeScale.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // -1/2, -1/3
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Positive translation
    //----------------------------------------------------------------------------
    
    public void testPositiveTranslation_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtPositiveTranslation.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 20, 40 ); // 10,20
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testPositiveTranslation_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtPositiveTranslation.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 110, 220, 300, 400 ); // 10,20
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testPositiveTranslation_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 20, 40 );
        Point2D pModel = _mvtPositiveTranslation.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // -10,-20
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testPositiveTranslation_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 110, 220, 300, 400 );
        Rectangle2D rModel = _mvtPositiveTranslation.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // -10,-20
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Negative translation
    //----------------------------------------------------------------------------
    
    public void testNegativeTranslation_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtNegativeTranslation.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 0, 0 ); // -10, -20
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testNegativeTranslation_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtNegativeTranslation.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 90, 180, 300, 400 ); // -10,-20
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testNegativeTranslation_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 0, 0 );
        Point2D pModel = _mvtNegativeTranslation.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // 10,20
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testNegativeTranslation_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 90, 180, 300, 400 );
        Rectangle2D rModel = _mvtNegativeTranslation.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // 10.,20
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Combined scale and translation
    //----------------------------------------------------------------------------
    
    public void testCombination_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtCombination.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 40, -120 ); // 2,-3,10,20
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testCombination_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtCombination.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 220, -1860, 600, 1200 ); // 2,-3,10,20
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testCombination_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 40, -120 );
        Point2D pModel = _mvtCombination.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // 1/2,-1/3,-10,-20
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testCombination_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 220, -1860, 600, 1200 );
        Rectangle2D rModel = _mvtCombination.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // 1/2,-1/3,-10,-20
        assertTrue( rModel.equals( rCorrect ) );
    }
}
