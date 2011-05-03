//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.buildamolecule.model.AtomModel;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.LewisDotModel;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * This is a circular mouse target that when moused-over turns the mouse cursor into scissors that will split the referenced bond
 */
public class MoleculeBondNode extends PNode {

    /**
     * "Radius" of the bond target that will break the bond
     */
    public static final double BOND_RADIUS = 5;

    private ScissorsNode scissorsNode; // scissors image, rotated depending on the orientation
    private BuildAMoleculeCanvas canvas;
    private RichSimpleObserver positionObserver;

    // our two atoms
    private AtomModel a;
    private AtomModel b;

    public MoleculeBondNode( MoleculeStructure.Bond bond, final Kit kit, final BuildAMoleculeCanvas canvas, final ModelViewTransform mvt ) {
        this.canvas = canvas;
        a = kit.getAtomModel( bond.a );
        b = kit.getAtomModel( bond.b );

        // use the lewis dot model to get our bond direction
        LewisDotModel.Direction bondDirection = kit.getBondDirection( a.getAtomInfo(), b.getAtomInfo() );
        final boolean isHorizontal = bondDirection == LewisDotModel.Direction.West || bondDirection == LewisDotModel.Direction.East;

        // construct our scissors node
        scissorsNode = new ScissorsNode() {{
            if ( isHorizontal ) {
                rotateInPlace( -Math.PI / 2 );
            }

            // hide it by default
            setVisible( false );

            // can't be clicked
            setPickable( false );
        }};

        // add our scissors image to the world as a screen child
        canvas.addScreenChild( scissorsNode );

        // hit target
        addChild( new PhetPPath( new Ellipse2D.Double( -BOND_RADIUS, -BOND_RADIUS, 2 * BOND_RADIUS, 2 * BOND_RADIUS ) ) {

            private boolean isDragging = false; // keep track of mouse-down state so we can NOT hide the cursor when it goes out of our hit zone
            private boolean isOver = false; // keep track of whether the mouse is over our hit zone, so that when the mouse is released we can keep the scissors visible if we are inside the hit zone

            {
                setPaint( Color.RED );
                setStrokePaint( Color.BLUE );

                // hit target is invisible
                setTransparency( 0.0f );

                addInputEventListener( new CursorHandler( createEmptyCursor() ) {
                    @Override public void mouseClicked( PInputEvent event ) {
                        // actually snip and break the bond
                        kit.breakBond( a, b );

                        // switch back from our "scissors" to a regular pointer
                        mouseExited( event );
                    }

                    @Override public void mouseEntered( PInputEvent event ) {
                        super.mouseEntered( event );

                        // mark the mouse as over our hit zone
                        isOver = true;

                        // show the scissors
                        scissorsNode.setVisible( true );
                    }

                    @Override public void mouseExited( PInputEvent event ) {
                        super.mouseExited( event );

                        // mark the mouse as outside of our hit zone
                        isOver = false;

                        // if the mouse is NOT down (not being dragged), we hide the scissors
                        if ( !isDragging ) {
                            scissorsNode.setVisible( false );
                        }
                    }

                    @Override public void mousePressed( PInputEvent event ) {
                        super.mousePressed( event );

                        // mark the mouse as down
                        isDragging = true;

                        scissorsNode.setClosed( true );
                    }

                    @Override public void mouseReleased( PInputEvent event ) {
                        super.mouseReleased( event );

                        // mark the mouse as up
                        isDragging = false;

                        if ( !isOver ) {
                            // if we are outside of the hit zone and we released, we need to hide the scissors
                            scissorsNode.setVisible( false );
                        }

                        scissorsNode.setClosed( false );
                    }

                    /*---------------------------------------------------------------------------*
                    * keeping the position of the scissors node updated
                    *----------------------------------------------------------------------------*/

                    private void updateScissorsPosition( PInputEvent event ) {
                        scissorsNode.centerFullBoundsOnPoint( event.getCanvasPosition().getX(), event.getCanvasPosition().getY() );
                    }

                    @Override public void mouseDragged( PInputEvent event ) {
                        super.mouseDragged( event );
                        updateScissorsPosition( event );
                    }

                    @Override public void mouseMoved( PInputEvent event ) {
                        super.mouseMoved( event );
                        updateScissorsPosition( event );
                    }
                } );
            }
        } );

        // listener that will update the position of our hit target
        positionObserver = new RichSimpleObserver() {
            public void update() {
                ImmutableVector2D location = b.getPosition().getSubtractedInstance( a.getPosition() ).getNormalizedInstance().getScaledInstance( a.getRadius() ).getAddedInstance( a.getPosition() );
                setOffset( mvt.modelToView( location.toPoint2D() ) );
            }
        };
        positionObserver.observe( a.position, b.position );
    }

    private static Cursor createEmptyCursor() {
        return Toolkit.getDefaultToolkit().createCustomCursor( new BufferedImage( 16, 16, BufferedImage.TYPE_INT_ARGB ), new Point( 0, 0 ), "invisibleCursor" );
    }

    public void destruct() {
        canvas.removeScreenChild( scissorsNode );
        positionObserver.unobserve( a.position, b.position );
    }
}
