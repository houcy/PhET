// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.Bucket;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.buildanatom.view.BucketNode;
import edu.colorado.phet.buildanatom.view.PeriodicTableControlNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class IsotopeMixturesCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public IsotopeMixturesCanvas( final IsotopeMixturesModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        // Set up the model-canvas transform.  The test chamber is centered
        // at (0, 0) in model space, and this transform is set up to place
        // the chamber where we want it on the canvas.
        //
        // IMPORTANT NOTES: The multiplier factors for the point in the view
        // can be adjusted to shift the center right or left, and the scale
        // factor can be adjusted to zoom in or out (smaller numbers zoom out,
        // larger ones zoom in).
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.30 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.37 ) ),
                1.6, // "Zoom factor" - smaller zooms out, larger zooms in.
                true );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the nodes that will allow the canvas to be layered.
        PNode chamberLayer = new PNode();
        rootNode.addChild( chamberLayer );
        final PNode backBucketLayer = new PNode();
        rootNode.addChild( backBucketLayer );
        PNode particleLayer = new PNode();
        rootNode.addChild( particleLayer );
        PNode frontBucketLayer = new PNode();
        rootNode.addChild( frontBucketLayer );

        // Add the test chamber into and out of which the individual isotopes
        // will be moved. As with all elements in this model, the shape and
        // position are considered to be two separate things.
        final PhetPPath testChamberNode = new PhetPPath( Color.BLACK ){{
            setPathTo( new Rectangle2D.Double( 0, 0,
                mvt.modelToViewDifferentialXDouble( model.getIsotopeTestChamberSize().getWidth() ),
                mvt.modelToViewDifferentialYDouble( -model.getIsotopeTestChamberSize().getHeight() ) ) );
            setOffset( mvt.modelToViewXDouble( model.getIsotopeTestChamberPosition().getX() ),
                    mvt.modelToViewYDouble( -model.getIsotopeTestChamberPosition().getY() ) );
        }};
        chamberLayer.addChild( testChamberNode );

        // Add the periodic table node that will allow the user to set the
        // current isotope.
        PNode periodicTableNode = new PeriodicTableControlNode( model, 18, BuildAnAtomConstants.CANVAS_BACKGROUND ){{
            setOffset( testChamberNode.getFullBoundsReference().getMaxX() + 15, testChamberNode.getFullBoundsReference().getMinY() );
            setScale( 1.1 ); // Empirically determined.
        }};
        chamberLayer.addChild( periodicTableNode );

        // Listen to the bucket list property in the model and update our
        // buckets if and when the list changes.
        model.getBucketListProperty().addObserver( new SimpleObserver() {
            public void update() {
                // TODO: need to handle bucket layering.
                backBucketLayer.removeAllChildren();
                int bucketCount = 0;
                for ( Bucket bucket : model.getBucketListProperty().getValue() ) {
                    BucketNode bucketNode = new BucketNode( bucket, mvt );
                    System.out.println("Adding bucket, count = " + bucketCount++);
                    System.out.println(bucket.getPosition());
                    System.out.println("T:" + mvt.modelToView( bucket.getPosition() ));
                    bucketNode.setOffset( mvt.modelToView( bucket.getPosition() ).getX(), 0 );
//                    bucketNode.setOffset( mvt.modelToView( new Point2D.Double(-50 + bucketCount * 10, -15) ) );
//                    bucketNode.setOffset( 100, 250 );
                    backBucketLayer.addChild( bucketNode );
                }
            }
        });

        // TODO: For debug, put a marker at 0,0 in model space.
        addWorldChild( new PhetPPath( mvt.createTransformedShape( new Ellipse2D.Double(-5, -5, 10, 10) ), Color.PINK ) );

        PieValue[] pieSlices = new PieValue[] {
                new PieValue( 100, Color.BLUE ),
                new PieValue( 50, Color.RED ),
                new PieValue( 100, Color.GREEN ) };

        PieChartNode pieChart = new PieChartNode( pieSlices, new Rectangle(0, 0, 100, 100));
        pieChart.setOffset( 720, 230 );
        testChamberNode.addChild( pieChart );
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
