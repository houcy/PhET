// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.Mode;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class PullerNode extends PNode {
    private final Vector2D initialOffset;
    public final PColor color;
    private final PhetPPath attachmentNode;
    private final PSize size;
    public final double scale;
    public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
    private KnotNode knot;
    public Double force;
    public static F<PullerNode, Double> _weight = new F<PullerNode, Double>() {
        @Override public Double f( final PullerNode pullerNode ) {

            //Average human mass is 76-83 kg, so the average human weight is between 745 and 813 Newtons.
            //Read more: http://wiki.answers.com/Q/What_does_an_average_human_weigh_in_newtons#ixzz26k003Tsu
            return pullerNode.size == PSize.SMALL ? 200.0 :
                   pullerNode.size == PSize.MEDIUM ? 400.0 :
                   pullerNode.size == PSize.LARGE ? 800.0 :
                   null;
        }
    };

    public PullerNode( final PColor color, final PSize size, final int item, final double scale, Vector2D offset, final PullerContext context, final ObservableProperty<Mode> mode ) {
        this.color = color;
        this.size = size;
        this.scale = scale;
        final BufferedImage standingImage = pullerImage( item );
        addChild( new PImage( pullerImage( item ) ) {{
            mode.addObserver( new ChangeObserver<Mode>() {
                public void update( final Mode newValue, final Mode oldValue ) {
                    if ( knot != null ) {
                        final BufferedImage pullingImage = pullerImage( 3 );
                        setImage( pullingImage );

                        //Padding accounts for the fact that the hand is no longer at the edge of the image when the puller is pulling, because the foot sticks out
                        double padding = size == PSize.LARGE ? 40 :
                                         size == PSize.MEDIUM ? 20 :
                                         size == PSize.SMALL ? 10 :
                                         Integer.MAX_VALUE;
                        if ( color == PColor.BLUE ) {
                            //align bottom right
                            setOffset( standingImage.getWidth() - pullingImage.getWidth() + padding,
                                       standingImage.getHeight() - pullingImage.getHeight() );
                        }
                        else {
                            //align bottom left
                            setOffset( 0 - padding,
                                       standingImage.getHeight() - pullingImage.getHeight() );
                        }
                    }
                }
            } );
        }} );
        setScale( scale );
        setOffset( offset.x, offset.y );
        initialOffset = offset;

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                context.startDrag( PullerNode.this );
            }

            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( PullerNode.this.getParent() );
                PullerNode.this.translate( delta.width / PullerNode.this.getScale(), delta.height / PullerNode.this.getScale() );
                context.drag( PullerNode.this );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( PullerNode.this );
            }
        } );

        final double w = 10;
        attachmentNode = new PhetPPath( new Ellipse2D.Double( -w / 2, -w / 2, w, w ), new BasicStroke( 2 ), TRANSPARENT ) {{
            setOffset( color == PColor.BLUE ?
                       standingImage.getWidth() - w / 2 :
                       w / 2, standingImage.getHeight() - 100 );
        }};
        addChild( attachmentNode );
    }

    private BufferedImage pullerImage( final int item ) {return ForcesAndMotionBasicsResources.RESOURCES.getImage( "pull_figure_" + sizeText( size ) + color.name() + "_" + item + ".png" );}

    private static String sizeText( final PSize size ) {
        return size == PSize.LARGE ? "lrg_" :
               size == PSize.SMALL ? "small_" :
               "";
    }

    public Point2D getGlobalAttachmentPoint() {
        return attachmentNode.getGlobalFullBounds().getCenter2D();
    }

    public void animateHome() {
        animateToPositionScaleRotation( initialOffset.x, initialOffset.y, scale, 0, TugOfWarCanvas.ANIMATION_DURATION );
    }

    public void setKnot( final KnotNode knot ) {
        this.knot = knot;
    }

    public KnotNode getKnot() {
        return knot;
    }

    public double getForce() {
        return size == PSize.SMALL ? 10 :
               size == PSize.MEDIUM ? 20 :
               size == PSize.LARGE ? 30 :
               Double.NaN;
    }

}