/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * DrainControlNode is the faucet used to drain the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DrainControlNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final PDimension LIQUID_COLUMN_SIZE = new PDimension( 20, 500 );
    private static final double DRAINING_RATE = 0.01; // liters per clock tick
    
    private static final Color PIPE_FILL_COLOR = new Color( 233, 184, 0 ); // mustard yellow
    private static final Color PIPE_STROKE_COLOR = Color.BLACK;
    private static final Stroke PIPE_STROKE = new BasicStroke( 2f );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final FaucetControlNode _faucetControlNode;
    private final PPath _liquidColumnNode;
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DrainControlNode( Liquid liquid ) {
        super();
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_LEFT );
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                if ( on ) {
                    _liquid.startDraining( DRAINING_RATE );
                }
                else {
                    _liquid.stopDraining();
                }
            }
        });
        _faucetControlNode.setOn( false );

        _liquidColumnNode = new PPath( new Rectangle2D.Double( 0, 0, LIQUID_COLUMN_SIZE.getWidth(), LIQUID_COLUMN_SIZE.getHeight() ) );
        _liquidColumnNode.setStroke( null );
        _liquidColumnNode.setVisible( _faucetControlNode.isOn() );
        
        Shape pipeShape = createPipeShape();
        PPath pipeNode = new PPath( pipeShape );
        pipeNode.setPaint( PIPE_FILL_COLOR );
        pipeNode.setStrokePaint( PIPE_STROKE_COLOR );
        pipeNode.setStroke( PIPE_STROKE );
        
        addChild( pipeNode );
        addChild( _liquidColumnNode );
        addChild( _faucetControlNode );
        
        _faucetControlNode.setOffset( 0, 0 );
        _liquidColumnNode.setOffset( _faucetControlNode.getFullBoundsReference().getMinX() + 4, _faucetControlNode.getFullBoundsReference().getMaxY() );
        pipeNode.setOffset( _faucetControlNode.getFullBoundsReference().getMaxX() - 2, 37 );

        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isOn() {
        return _faucetControlNode.isOn();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        _liquidColumnNode.setVisible( _liquid.isDraining() );
        _liquidColumnNode.setPaint( _liquid.getColor() );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Creates the shape of the pipe, using constructive area geometry.
     * This shape is specific to the faucet image.
     */
    private Shape createPipeShape() {
        
        final double pipeWidth = 31;
        final double horizontalLength = 20; // horizontal section of pipe
        final double verticalLength = 64; // vertical section of pipe
        final double elbowRadius = 20; // elbow where the horizontal and vertical sections are joined
        assert( elbowRadius < pipeWidth );
        
        Shape horizontalPipe = new Rectangle2D.Double( 0, 0, horizontalLength + elbowRadius, pipeWidth );
        Shape verticalPipe = new Rectangle2D.Double( horizontalLength, -verticalLength, pipeWidth, verticalLength + elbowRadius );
        Shape elbow = new RoundRectangle2D.Double( horizontalLength, 0, pipeWidth, pipeWidth, elbowRadius, elbowRadius );
        
        Area area = new Area( horizontalPipe );
        area.add( new Area( verticalPipe ) );
        area.add( new Area( elbow ) );
        return area;
    }
}
