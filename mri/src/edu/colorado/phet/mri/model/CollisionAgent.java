/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.quantum.model.Photon;

import java.util.*;

/**
 * CollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CollisionAgent implements ModelElement {
    private MriModel model;
    private List collisionExperts = new ArrayList();
    private PhotonDipoleCollisionAgent photonDipoleCollisonCollisionAgent;

    public CollisionAgent( MriModel model ) {
        this.model = model;

        photonDipoleCollisonCollisionAgent = new PhotonDipoleCollisionAgent( model );
    }

    public void stepInTime( double dt ) {

        List dipoles = model.getDipoles();
        List photons = model.getPhotons();
        TreeMap map = model.getPhotonsByXLoc();
//        Collection photons = map.values();
        Iterator photonIt = photons.iterator();
//        Photon photon = (Photon)photonIt.next();
        for( int i = 0; i < dipoles.size(); i++ ) {
            Dipole dipole = (Dipole)dipoles.get( i );
            for( int j = 0; j < photons.size(); j++ ) {
//            while( photon != null && photon.getPosition().getX() < dipole.getPosition().getX() + dipole.getRadius() ) {

                Photon photon = (Photon)photons.get( j );
                photonDipoleCollisonCollisionAgent.detectAndDoCollision( dipole, photon );
//                if( photonIt.hasNext() ) {
//                    photon = (Photon)photonIt.next();
//                }
//                else {
//                    photon = null;
//                }
            }
        }
    }
}
