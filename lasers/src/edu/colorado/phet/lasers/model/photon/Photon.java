/**
 * Class: Photon
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.photon;

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.lasers.EventRegistry;
import edu.colorado.phet.lasers.coreadditions.SubscriptionService;
import edu.colorado.phet.lasers.model.atom.Atom;

import javax.swing.event.EventListenerList;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 *
 */
public class Photon extends Particle implements Collidable {

    ////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //
    static public double s_speed = 1;
    static public double s_radius = 10;
    static public double RED = 680;
    static public double DEEP_RED = 640;
    static public double BLUE = 440;
    static public double GRAY = 5000;
    static private double PLANCK = 6.626E-34;
    static private EventRegistry classEventRegistry = new EventRegistry();

    public static double energyToWavelength( double energy ) {
        return PLANCK / energy;
    }

    public static double wavelengthToEnergy( double wavelength ) {
        return PLANCK / wavelength;
    }


    // Free pool of photons. We do this so we don't have to use the heap
    // at run-time
    static private int freePoolSize = 2000;
    static private ArrayList freePool = new ArrayList( freePoolSize );

    // Populate the free pool
    static {
        for( int i = 0; i < freePoolSize; i++ ) {
            freePool.add( new Photon() );
        }
    }

    static public Photon create( Point2D location, Vector2D velocity ) {
        Photon newPhoton = new Photon();
        newPhoton.setPosition( location );
        newPhoton.setVelocity( velocity );
        classEventRegistry.fireEvent( new PhotonEmittedEvent( Photon.class, newPhoton ) );
        return newPhoton;
    }

    static public Photon createStimulated( Photon stimulatingPhoton, Point2D location ) {
        stimulatingPhoton.numStimulatedPhotons++;
        Photon newPhoton = create( stimulatingPhoton.getWavelength(), location,
                                   stimulatingPhoton.getVelocity() );
        //        Photon newPhoton = create( stimulatingPhoton.getWavelength(), location, velocity );
        //        newPhoton.setVelocity( new Vector2D.Double( stimulatingPhoton.getVelocity() ) );
        int yOffset = stimulatingPhoton.numStimulatedPhotons * 8;
        newPhoton.setPosition( stimulatingPhoton.getPosition().getX(),
                               stimulatingPhoton.getPosition().getY() - yOffset );
        return newPhoton;
    }

    /**
     * If the photon is created by a CollimatedBeam, it should use this method,
     * so that the photon can tell the CollimatedBeam if it is leaving the system.
     */
    static public Photon create( double wavelength, Point2D location, Vector2D velocity ) {
        Photon newPhoton = create( location, velocity );
        newPhoton.setWavelength( wavelength );
        return newPhoton;
    }

    static public void addClassListener( EventListener listener ) {
        classEventRegistry.addListener( listener );
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Inner classes & interfaces
    //
    public class LeftSystemEvent extends EventObject {
        public LeftSystemEvent() {
            super( Photon.this );
        }

        public Photon getPhoton() {
            return Photon.this;
        }
    }

    public interface LeftSystemEventListener extends EventListener {
        public void leftSystemEventOccurred( LeftSystemEvent event );
    }

    public class VelocityChangedEvent extends EventObject {
        public VelocityChangedEvent() {
            super( Photon.this );
        }

        public Vector2D getVelocity() {
            return Photon.this.getVelocity();
        }
    }

    public interface VelocityChangedListener extends EventListener {
        public void velocityChanged( VelocityChangedEvent event );
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private EventListenerList listenerList = new EventListenerList();
    private EventRegistry eventRegistry = new EventRegistry();
    private int numObservers;
    private int numStimulatedPhotons;
    // If this photon was produced by the stimulation of another, this
    // is a reference to that photon.
    private Photon parentPhoton;
    // If this photon has stimulated the production of another photon, this
    // is a reference to that photon
    private Photon childPhoton;
    private CollidableAdapter collidableAdapter;
    private boolean isCollidable;
    private double wavelength;
    //    private CollimatedBeam beam;
    // This list keeps track of atoms that the photon has collided with
    private ArrayList contactedAtoms = new ArrayList();


    public void addLeftSystemEventListener( LeftSystemEventListener listener ) {
        listenerList.add( LeftSystemEventListener.class, listener );
    }

    public void removeLeftSystemEventListener( LeftSystemEventListener listener ) {
        listenerList.remove( LeftSystemEventListener.class, listener );
    }

    public synchronized void addObserver( SimpleObserver o ) {
        super.addObserver( o );
        numObservers++;
    }

    /**
     * Constructor is private so that clients of the class must use static create()
     * methods. This allows us to manage a free pool of photons and not hit the
     * heap so hard.
     */
    private Photon() {
        collidableAdapter = new CollidableAdapter( this );
        setVelocity( s_speed, 0 );
        //        setMass( 1 );
    }

    private SubscriptionService bulletinBoard = new SubscriptionService();

    public interface Listener {
        void leavingSystem( Photon photon );
    }

    public void addListener( EventListener listener ) {
        eventRegistry.addListener( listener );
    }

    public void removeListener( EventListener listener ) {
        bulletinBoard.removeListener( listener );
        eventRegistry.removeListener( listener );
    }

    /**
     * Rather than use the superclass behavior, the receiver
     * puts itself in the class free pool, so it can be used
     * again. This helps prevent us from flogging the heap.
     */
    public void removeFromSystem() {
        eventRegistry.fireEvent( new LeftSystemEvent() );
        this.removeAllObservers();
        //        freePool.add( this );
        //        setChanged();
        //        notifyObservers( Particle.S_REMOVE_BODY );
    }

    public double getWavelength() {
        return wavelength;
    }

    public void setWavelength( double wavelength ) {
        this.wavelength = wavelength;
    }

    public double getEnergy() {
        return wavelengthToEnergy( wavelength );
    }

    public boolean hasCollidedWithAtom( Atom atom ) {
        return contactedAtoms.contains( atom );
    }

    public Photon getParentPhoton() {
        return parentPhoton;
    }

    public void setParentPhoton( Photon parentPhoton ) {
        this.parentPhoton = parentPhoton;
    }

    public Photon getChildPhoton() {
        return childPhoton;
    }

    public void setChildPhoton( Photon childPhoton ) {
        this.childPhoton = childPhoton;
    }

    public void setVelocity( double vx, double vy ) {
        VelocityChangedEvent vce = new VelocityChangedEvent();
        super.setVelocity( vx, vy );
        eventRegistry.fireEvent( vce );
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public void stepInTime( double dt ) {
        collidableAdapter.stepInTime( dt );
        super.stepInTime( dt );
    }
}
