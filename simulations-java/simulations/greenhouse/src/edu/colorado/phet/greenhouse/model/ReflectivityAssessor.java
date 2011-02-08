// Copyright 2002-2011, University of Colorado

/**
 * Class: ReflectivityAssessor
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 21, 2003
 */
package edu.colorado.phet.greenhouse.model;


import edu.colorado.phet.common.photonabsorption.model.Photon;

public interface ReflectivityAssessor {
    double getReflectivity( Photon photon );
}
