/**
 * Class: ContainmentGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.Containment;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class ContainmentGraphic extends DefaultInteractiveGraphic {
    private Containment containment;
    private AffineTransform atx;
    private Rep rep;
    private boolean leftSideDragged;
    private boolean rightSideDragged;
    private boolean topSideDragged;
    private boolean bottomSideDragged;
    private int strokeWidth = 100;
    private Area mouseableArea;

    public ContainmentGraphic( Containment containment, Component component, AffineTransform atx ) {
        super( null );
        this.containment = containment;
        this.atx = atx;
        rep = new Rep( component );
        setBoundedGraphic( rep );
        addCursorHandBehavior();
        addTranslationBehavior( new Translator() );
    }

    public void mouseDragged( MouseEvent e ) {
        super.mouseDragged( e );
        // Determine which side of the box is selected
        Point p = e.getPoint();
        if( rep.contains( p.x, p.y ) ) {
            leftSideDragged = false;
            topSideDragged = false;
            rightSideDragged = false;
            bottomSideDragged = false;
            if( p.getX() >= rep.getBounds().getMinX() - strokeWidth / 2
                && p.getX() <= rep.getBounds().getMinX() + strokeWidth / 2 ) {
                leftSideDragged = true;
            }
            if( p.getX() >= rep.getBounds().getMaxX() - strokeWidth / 2
                && p.getX() <= rep.getBounds().getMaxX() + strokeWidth / 2 ) {
                rightSideDragged = true;
            }
            if( p.getY() >= rep.getBounds().getMinY() - strokeWidth / 2
                && p.getY() <= rep.getBounds().getMinY() + strokeWidth / 2 ) {
                topSideDragged = true;
            }
            if( p.getY() >= rep.getBounds().getMaxY() - strokeWidth / 2
                && p.getY() <= rep.getBounds().getMaxY() + strokeWidth / 2 ) {
                bottomSideDragged = true;
            }
        }
    }

    private class Translator implements Translatable {
        public void translate( double dx, double dy ) {
            Rectangle2D rect = containment.getBounds();
            dx /= atx.getScaleX();
            dy /= atx.getScaleY();
            if( leftSideDragged ) {
                rect.setRect( rect.getMinX() + dx, rect.getMinY(), rect.getWidth() - 2 * dx, rect.getHeight() );
            }
            if( rightSideDragged ) {
                rect.setRect( rect.getMinX() - dx, rect.getMinY(), rect.getWidth() + 2 * dx, rect.getHeight() );
            }
            if( topSideDragged ) {
                rect.setRect( rect.getMinX(), rect.getMinY() + dy, rect.getWidth(), rect.getHeight() - 2 * dy );
            }
            if( bottomSideDragged ) {
                rect.setRect( rect.getMinX(), rect.getMinY() - dy, rect.getWidth(), rect.getHeight() + 2 * dy );
            }
            rep.update();
        }
    }

    private class Rep extends PhetShapeGraphic implements SimpleObserver {
        //        private Area mouseableArea;
        RoundRectangle2D outer = new RoundRectangle2D.Double();
        RoundRectangle2D inner = new RoundRectangle2D.Double();
        private Stroke stroke = new BasicStroke( strokeWidth );
        private Color color = Color.black;

        Rep( Component component ) {
            super( component, null, null, null );
            containment.addObserver( this );
            update();
        }

        public void update() {
            Rectangle2D r = containment.getBounds();
            outer.setRoundRect( r.getMinX() - strokeWidth,
                                r.getMinY() - strokeWidth,
                                r.getWidth() + strokeWidth * 2, r.getHeight() + strokeWidth * 2,
                                strokeWidth * 2, strokeWidth * 2 );
            inner.setRoundRect( r.getMinX(),
                                r.getMinY(),
                                r.getWidth(), r.getHeight(),
                                strokeWidth, strokeWidth );
            mouseableArea = new Area( outer );
            mouseableArea.subtract( new Area( inner ) );
            this.setShape( atx.createTransformedShape( mouseableArea ) );
            setBoundsDirty();
            repaint();
        }

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            GraphicsUtil.setAntiAliasingOn( g );
            g.transform( atx );
            g.setColor( color );
            g.setStroke( stroke );
            g.fill( mouseableArea );

            g.setColor( Color.red );
            g.setStroke( new BasicStroke( 1 ) );
            g.draw( mouseableArea );
            restoreGraphicsState();
        }
    }
}
