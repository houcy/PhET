/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


public class MomentarySliderNode extends PNode {
    
    // track
    private static final float TRACK_STROKE_WIDTH = 2f;
    private static final Stroke TRACK_STROKE = new BasicStroke( TRACK_STROKE_WIDTH );
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    
    // knob
    private static final int KNOB_STROKE_WIDTH = 1;
    private static final Stroke KNOB_STROKE = new BasicStroke( KNOB_STROKE_WIDTH );
    private static final Color KNOB_FILL_COLOR = new Color( 255, 255, 255, 210 );
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    
    private final ArrayList _listeners;
    private boolean _on;
    private final PDimension _trackSize;
    private final TrackNode _trackNode;
    private final KnobNode _knobNode;
    private boolean _dragging;
    
    public MomentarySliderNode( PDimension trackSize, PDimension knobSize ) {
        _listeners = new ArrayList();
        _on = false;
        _dragging = false;
        _trackSize = new PDimension( trackSize );
        _trackNode = new TrackNode( trackSize );
        _knobNode = new KnobNode( knobSize );
        
        addChild( _trackNode );
        addChild( _knobNode );
        
        _trackNode.setOffset( 0, 0 );
        _knobNode.setOffset( 0, _trackNode.getFullBoundsReference().getCenterY() + _knobNode.getFullBoundsReference().getHeight() / 2 );
        
        initInteractivity();
    }
    
    /*
     * Adds interactivity to the knob.
     */
    private void initInteractivity() {
        
        // hand cursor on knob
        _knobNode.addInputEventListener( new CursorHandler() );
        
        // Constrain the knob to be dragged vertically within the track
        _knobNode.addInputEventListener( new PDragEventHandler() {
            
            private double _globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates
            
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                _dragging = true;
                // note the offset between the mouse click and the knob's origin
                Point2D pMouseLocal = event.getPositionRelativeTo( MomentarySliderNode.this );
                Point2D pMouseGlobal = MomentarySliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = MomentarySliderNode.this.localToGlobal( _knobNode.getOffset() );
                _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            protected void drag(PInputEvent event) {
                
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( MomentarySliderNode.this );
                Point2D pMouseGlobal = MomentarySliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = MomentarySliderNode.this.globalToLocal( pKnobGlobal );
                
                // constrain the drag to the track
                double xOffset = pKnobLocal.getX();
                if ( xOffset < 0 ) {
                    xOffset = 0;
                }
                else if ( xOffset > _trackSize.getWidth() ) {
                    xOffset = _trackSize.getWidth();
                }
                
                // move the knob
                _knobNode.setOffset( xOffset, _knobNode.getYOffset() );
                
                // notify listeners of state change
                if ( xOffset == 0 && _on == true ) {
                    _on = false;
                    notifyOnOffChanged();
                }
                else if ( xOffset != 0 && _on == false ) {
                    _on = true;
                    notifyOnOffChanged();
                }
            }
            
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                _dragging = false;
                _knobNode.setOffset( 0, _knobNode.getYOffset() );
                if ( _on  ) {
                    _on = false;
                    notifyOnOffChanged();
                }
            }
        } );
    }
    
    public void setOn( boolean on ) {
        if ( on != _on && !_dragging ) {
            _on = on;
            if ( on ) {
                _knobNode.setOffset( _trackSize.getWidth(), _knobNode.getYOffset() );
            }
            else {
                _knobNode.setOffset( 0, _knobNode.getYOffset() );
            }
            notifyOnOffChanged();
        }
    }
    
    public boolean isOn() {
        return _on;
    }
    
    /*
     * The slider track, horizontal orientation.
     * Origin is at the upper left corner.
     */
    private static class TrackNode extends PNode {
        public TrackNode( PDimension size ) {
            super();
            PPath pathNode = new PPath();
            final double width = size.getWidth() - TRACK_STROKE_WIDTH;
            final double height = size.getHeight() - TRACK_STROKE_WIDTH;
            pathNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
            pathNode.setPaint( TRACK_FILL_COLOR );
            pathNode.setStroke( TRACK_STROKE );
            pathNode.setStrokePaint( TRACK_STROKE_COLOR );
            addChild( pathNode );
        }
    }
    /*
     * 
     * The slider knob, tip points down.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PNode {
        public KnobNode( PDimension size ) {

            float w = (float) size.getWidth();
            float h = (float) size.getHeight();
            GeneralPath knobPath = new GeneralPath();
            knobPath.moveTo( 0f, 0f );
            knobPath.lineTo( w / 2f, -0.35f * h );
            knobPath.lineTo( w / 2f, -h );
            knobPath.lineTo( -w / 2f, -h );
            knobPath.lineTo( -w / 2f, -0.35f * h );
            knobPath.closePath();

            PPath pathNode = new PPath();
            pathNode.setPathTo( knobPath );
            pathNode.setPaint( KNOB_FILL_COLOR );
            pathNode.setStroke( KNOB_STROKE );
            pathNode.setStrokePaint( KNOB_STROKE_COLOR );
            addChild( pathNode );
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface MomentarySliderListener {
        public void onOffChanged( boolean on );
    }
    
    public void addMomentarySliderListener( MomentarySliderListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeMomentarySliderListener( MomentarySliderListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyOnOffChanged() {
        final boolean on = isOn();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MomentarySliderListener) i.next() ).onOffChanged( on );
        }
    }
    
    /*
     * Example
     */
    public static void main( String args[] ) {
        
        PDimension trackSize = new PDimension( 200, 5 );
        PDimension knobSize = new PDimension( 15, 20 );
        final MomentarySliderNode sliderNode = new MomentarySliderNode( trackSize, knobSize );
        
        final PPath onOffNode = new PPath( new Rectangle2D.Double( 0, 0, 50, 50 ) );
        onOffNode.setPaint( sliderNode.isOn() ? Color.GREEN : Color.RED );
        
        sliderNode.addMomentarySliderListener( new MomentarySliderListener() {
            public void onOffChanged( boolean on ) {
                onOffNode.setPaint( sliderNode.isOn() ? Color.GREEN : Color.RED );
            }
        });
        
        PCanvas canvas = new PCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        canvas.getLayer().addChild( sliderNode );
        canvas.getLayer().addChild( onOffNode );
        
        sliderNode.setOffset( 50, 50 );
        onOffNode.setOffset( 50, 150 );
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( canvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 400 ) );
        frame.setVisible( true );
    }
}
