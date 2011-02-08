// Copyright 2002-2011, University of Colorado

/**
 * Class: PhotonEmitter
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse.model;


import edu.colorado.phet.common.photonabsorption.model.Photon;

public interface PhotonEmitter {

    void addListener( Listener listener );

    void removeListener( Listener listener );

    double getProductionRate();

    void setProductionRate( double productionRate );

    Photon emitPhoton();

    //
    // Inner classes
    //
    public interface Listener {
        void photonEmitted( Photon photon );
    }

}
