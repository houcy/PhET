/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.junction;


/**
 * User: Sam Reid
 * Date: Aug 31, 2003
 * Time: 12:00:05 AM
 * Copyright (c) Aug 31, 2003 by Sam Reid
 */
public interface JunctionObserver {
    void locationChanged(Junction junction2);

    void connectivityChanged();
}
