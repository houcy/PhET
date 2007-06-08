/*
* The Physics Education Technology (PhET) project provides
* a suite of interactive educational simulations.
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/
package edu.colorado.phet.common.jfreechartphet.piccolo;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * This class shows a draggable cursor overlaid on a JFreeChartNode
 *
 * @author Sam Reid
 */
public class JFreeChartCursorNode extends PNode {
    private JFreeChartNode jFreeChartNode;
    private PhetPPath path;
    private double time;
    private double width = 9;
    private double minDragTime = Double.NEGATIVE_INFINITY;
    private double maxDragTime = Double.POSITIVE_INFINITY;

    private ArrayList listeners = new ArrayList();

    final PropertyChangeListener updater = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            update();
        }
    };

    //todo: for certain circumstances, this will be a memory leak.
    //This code is supposed to maintain correct bounds if target JFreeChartNode changes screen bounds.
    private PropertyChangeListener updateParent = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            PNode parent = jFreeChartNode.getParent();
            while( parent != null ) {
                parent.removePropertyChangeListener( PNode.PROPERTY_TRANSFORM, updater );
                parent.removePropertyChangeListener( PNode.PROPERTY_PARENT, updateParent );
                parent.addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, updater );
                parent.addPropertyChangeListener( PNode.PROPERTY_PARENT, updateParent );
                parent = parent.getParent();
                update();
            }
        }
    };

    /**
     * Constructs a JFreeChartCursorNode to target the specified jfreechartnode.
     *
     * @param jFreeChartNode the node to overlay
     */
    public JFreeChartCursorNode( final JFreeChartNode jFreeChartNode ) {
        this.jFreeChartNode = jFreeChartNode;
        path = new PhetPPath( new Color( 50, 50, 200, 80 ), new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10.0f, 5.0f}, 0 ), Color.darkGray );
        addChild( path );

        jFreeChartNode.addPropertyChangeListener( updater );
        addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, updater );

        addPropertyChangeListener( PNode.PROPERTY_PARENT, updateParent );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            Point2D pressPoint;
            double pressTime;

            public void mousePressed( PInputEvent event ) {
                pressPoint = event.getPositionRelativeTo( JFreeChartCursorNode.this );
                pressTime = time;
            }

            public Point2D localToPlotDifferential( double dx, double dy ) {
                Point2D pt1 = new Point2D.Double( 0, 0 );
                Point2D pt2 = new Point2D.Double( dx, dy );
                localToGlobal( pt1 );
                localToGlobal( pt2 );
                jFreeChartNode.globalToLocal( pt1 );
                jFreeChartNode.globalToLocal( pt2 );
                pt1 = jFreeChartNode.nodeToPlot( pt1 );
                pt2 = jFreeChartNode.nodeToPlot( pt2 );
                return new Point2D.Double( pt2.getX() - pt1.getX(), pt2.getY() - pt1.getY() );
            }

            public void mouseDragged( PInputEvent event ) {
                Point2D d = event.getPositionRelativeTo( JFreeChartCursorNode.this );
                Point2D dx = new Point2D.Double( d.getX() - pressPoint.getX(), d.getY() - pressPoint.getY() );
                Point2D diff = localToPlotDifferential( dx.getX(), dx.getY() );
                time = pressTime + diff.getX();
                time = MathUtil.clamp( getUpperBound(), time, getLowBound() );

                update();
                notifySliderDragged();
            }
        } );
        update();
    }

    public double getMinDragTime() {
        return minDragTime;
    }

    public void setMinDragTime( double minDragTime ) {
        this.minDragTime = minDragTime;
        if( getTime() < minDragTime ) {
            setTime( minDragTime );
        }
    }

    public double getMaxDragTime() {
        return maxDragTime;
    }

    public void setMaxDragTime( double maxDragTime ) {
        this.maxDragTime = maxDragTime;
        if( getTime() > maxDragTime ) {
            setTime( maxDragTime );
        }
    }

    private double getLowBound() {
        return Math.min( jFreeChartNode.getChart().getXYPlot().getDomainAxis().getRange().getUpperBound(), maxDragTime );
    }

    private double getUpperBound() {
        return Math.max( jFreeChartNode.getChart().getXYPlot().getDomainAxis().getRange().getLowerBound(), minDragTime );
    }

    public static interface Listener {
        void cursorTimeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifySliderDragged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).cursorTimeChanged();
        }
    }

    public void setTime( double time ) {
        if( this.time != time ) {
            this.time = time;
            update();
        }
    }

    public double getTime() {
        return time;
    }

    private void update() {
        Point2D topCenterModel = jFreeChartNode.plotToNode( new Point2D.Double( time, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound() ) );
        Point2D bottomCenterModel = jFreeChartNode.plotToNode( new Point2D.Double( time, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getLowerBound() ) );

        jFreeChartNode.localToGlobal( topCenterModel );
        jFreeChartNode.localToGlobal( bottomCenterModel );

        globalToLocal( topCenterModel );
        globalToLocal( bottomCenterModel );//todo: should one of these transforms be through the parent?

        path.setPathTo( new Rectangle2D.Double( topCenterModel.getX() - width / 2, topCenterModel.getY(), width, bottomCenterModel.getY() - topCenterModel.getY() ) );
    }
}