/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

/**
 * Utility class for timing activity:
 * <p/>
 * Sample usage:
 * <p/>
 * QuickTimer paintTime=new QuickTimer();
 * paintComponent(g2d);
 * System.out.println("paintTime="+paintTime);
 */

public class QuickTimer {
    private long startTime;

    public QuickTimer() {
        this.startTime = System.currentTimeMillis();
    }

    public long getTime() {
        long now = System.currentTimeMillis();
        long dt = now - startTime;
        return dt;
    }

    public String toString() {
        return String.valueOf( getTime() );
    }
}