/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.chart.SingleTerminalFloatingChart;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.BranchGraphic;
import edu.colorado.phet.cck3.circuit.CircuitGraphic;
import edu.colorado.phet.cck3.circuit.InteractiveBranchGraphic;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 11:43:52 AM
 * Copyright (c) Jun 22, 2006 by Sam Reid
 */

public class CurrentStripChart extends SingleTerminalFloatingChart {
    private CircuitGraphic circuitGraphic;

    public CurrentStripChart( PSwingCanvas pSwingCanvas, String title, IClock clock, CircuitGraphic circuitGraphic ) {
        super( pSwingCanvas, title, new ValueReader() {
            public double getValue( double x, double y ) {
                return 0;
            }
        }, clock );
        this.circuitGraphic = circuitGraphic;
        super.setValueReader( new CurrentReader() );
    }

    public class CurrentReader implements ValueReader {
        public double getValue( double x, double y ) {
            Point2D target = new Point2D.Double( x, y );
            //check for intersect with circuit.
            Graphic[] g = circuitGraphic.getBranchGraphics();
            for( int i = g.length - 1; i >= 0; i-- ) {
                Graphic graphic = g[i];
                if( graphic instanceof InteractiveBranchGraphic ) {
                    InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)graphic;
                    Branch branch = ibg.getBranch();
                    BranchGraphic branchGraphic = ibg.getBranchGraphic();
                    Shape shape = branchGraphic.getCoreShape();//getShape();
                    if( shape.contains( target ) ) {
                        return branch.getCurrent();
                    }
                }
            }
            return Double.NaN;
        }
    }
}
