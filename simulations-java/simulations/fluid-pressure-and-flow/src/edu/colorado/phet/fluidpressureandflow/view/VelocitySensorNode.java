package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class VelocitySensorNode extends PNode {

    public VelocitySensorNode( final ModelViewTransform transform, final VelocitySensor sensor, final Property<Units.Unit> units ) {
        double hotSpotRadius = PressureSensorNode.hotSpotRadius;
        addChild( new PhetPPath( new Ellipse2D.Double( -hotSpotRadius, -hotSpotRadius, hotSpotRadius * 2, hotSpotRadius * 2 ), Color.red ) );
        final PText child = new PText( getText( sensor, units ) ) {{
            setFont( new PhetFont( 18, true ) );
        }};

        final ArrowNode arrowNode = new ArrowNode( new Point2D.Double(), new Point2D.Double( 0, 1 ), 10, 10, 5, 0.5, true ) {{
            setPaint( Color.red );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( Color.black );
        }};

        addChild( arrowNode );
        addChild( child );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new RelativeDragHandler( this, transform, sensor.getLocationProperty() ) );

        sensor.addPositionObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
        sensor.addVelocityObserver( new SimpleObserver() {
            public void update() {
                child.setText( getText( sensor, units ) );

                child.setOffset( -child.getFullBounds().getWidth() / 2, 0 );//Center the text under the the hot spot

                ImmutableVector2D velocity = sensor.getVelocity().getValue();
                ImmutableVector2D viewVelocity = transform.modelToViewDelta( velocity );
                double velocityScale = 0.2;
                Point2D tip = viewVelocity.getScaledInstance( velocityScale ).toPoint2D();
                Point2D tail = viewVelocity.getScaledInstance( -1 * velocityScale ).toPoint2D();
                arrowNode.setTipAndTailLocations( tip, tail );
            }
        } );
    }
    
    private static String getText( VelocitySensor sensor, final Property<Units.Unit> units ) {
        return "v = " + new DecimalFormat( "0.00" ).format( sensor.getVelocity().getValue().getMagnitude() ) + " " + units.getValue().getAbbreviation();
    }
}
