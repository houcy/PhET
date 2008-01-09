package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.model.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;


public class ToolNodeFactory {

    /* not intended for instantiation */
    private ToolNodeFactory() {}
    
    public static PNode createNode( AbstractTool tool, ModelViewTransform mvt ) {
        PNode node = null;
        if ( tool instanceof Thermometer ) {
            node = new ThermometerNode( (Thermometer)tool, mvt );
        }
        else if ( tool instanceof GlacialBudgetMeter ) {
            node = new GlacialBudgetMeterNode( (GlacialBudgetMeter)tool, mvt );
        }
        else if ( tool instanceof TracerFlag ) {
            node = new TracerFlagNode( (TracerFlag)tool, mvt );
        }
        else if ( tool instanceof IceThicknessTool ) {
            node = new IceThicknessToolNode( (IceThicknessTool)tool, mvt );
        }
        else if ( tool instanceof BoreholeDrill ) {
            node = new BoreholeDrillNode( (BoreholeDrill)tool, mvt );
        }
        else if ( tool instanceof GPSReceiver ) {
            node = new GPSReceiverNode( (GPSReceiver)tool, mvt );
        }
        else {
            throw new UnsupportedOperationException( "no node for tool type " + tool.getClass() );
        }
        
        node.addInputEventListener( new CursorHandler() );
        node.addInputEventListener( new PDragEventHandler() ); //XXX unconstrained dragging
        
        return node;
    }
}
