/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.util;

import edu.colorado.phet.common.math.PhetVector;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 9:33:13 PM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class DoubleGeneralPath {
    GeneralPath path;

    public DoubleGeneralPath(PhetVector pt) {
        this(pt.getX(), pt.getY());
    }

    public DoubleGeneralPath(Point2D.Double pt) {
        this(pt.x, pt.y);
    }

    public DoubleGeneralPath(double x, double y) {
        path = new GeneralPath();
        path.moveTo((float) x, (float) y);
    }

    public void moveTo(double x, double y) {
        path.moveTo((float) x, (float) y);
    }

    public void lineTo(double x, double y) {
        path.lineTo((float) x, (float) y);
    }

    public void lineTo(Point2D.Double pt) {
        lineTo(pt.x, pt.y);
    }

    public GeneralPath getGeneralPath() {
        return path;
    }

    public void lineTo(PhetVector loc) {
        lineTo(loc.getX(), loc.getY());
    }
}
