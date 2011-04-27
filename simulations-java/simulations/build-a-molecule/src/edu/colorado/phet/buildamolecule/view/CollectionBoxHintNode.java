//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

public class CollectionBoxHintNode extends PNode {
    public CollectionBoxHintNode( ModelViewTransform mvt, PBounds moleculeDestinationBounds, CollectionBox box ) {
        final ImmutableVector2D tip = mvt.modelToView( new ImmutableVector2D( box.getDropBounds().getMinX() - 20, box.getDropBounds().getCenterY() ) );
        final ImmutableVector2D tail;
        final Rectangle2D moleculeViewBounds = mvt.modelToViewRectangle( moleculeDestinationBounds );

        double maxArrowLength = 100;
        ImmutableVector2D farTailStart = mvt.modelToView( new ImmutableVector2D( moleculeDestinationBounds.getMaxX() + 20, moleculeDestinationBounds.getCenterY() ) );
        if ( tip.getDistance( farTailStart ) > maxArrowLength ) {
            tail = farTailStart.getSubtractedInstance( tip ).getNormalizedInstance().getScaledInstance( maxArrowLength ).getAddedInstance( tip );
        }
        else {
            tail = farTailStart;
        }
        addChild( new ArrowNode( tail.toPoint2D(), tip.toPoint2D(), 30, 40, 20 ) {{
            setPaint( Color.BLUE );
        }} );

        addChild( new PText( "Collect your molecule!" ) {{
            setFont( new PhetFont( 18 ) );
            double lowestObstacle = Math.max( tail.getY(), tip.getY() );
            double xOffset = tip.getX() - 30 - getFullBounds().getWidth();
            double belowObstaclePadding = 20;
            setOffset( xOffset, lowestObstacle + belowObstaclePadding );

            // TODO: how to handle this nicely? it can overlap our molecule!
            if ( getFullBounds().intersects( moleculeViewBounds ) ) {
                setOffset( xOffset, moleculeViewBounds.getMaxY() + belowObstaclePadding );
            }
        }} );
    }

    public void disperse() {
        // TODO: animation
        setVisible( false );
    }
}
