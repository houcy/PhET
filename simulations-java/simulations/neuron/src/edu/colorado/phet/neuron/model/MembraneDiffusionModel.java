/* Copyright 2009, University of Colorado */
package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class represents the main class for modeling membrane diffusion.  It
 * acts as the central location where the interactions between the membrane,
 * the particles (i.e. ions), and the gates are all governed.
 *
 * @author John Blanco
 */
public class MembraneDiffusionModel implements IParticleCapture {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final Random RAND = new Random();
	
	// The following constants define the overall size of the model, which
	// means the boundaries within which any model element is allowed to
	// exist.  This does NOT define the size of the chamber where the
	// particles reside.
	private static final double OVERALL_MODEL_HEIGHT = 130; // In nanometers.
	private static final double OVERALL_MODEL_WIDTH = 180; // In nanometers.
	
	// Definition of the rectangle where the particles can move.  Note that
	// the center of the chamber is assumed to be at (0,0).
	private static final double PARTICLE_CHAMBER_WIDTH = 80; // In nanometers.
	private static final double PARTICLE_CHAMBER_HEIGHT = 60; // In nanometers.
	private static final Rectangle2D PARTICLE_CHAMBER_RECT = new Rectangle2D.Double( -PARTICLE_CHAMBER_WIDTH / 2,
			-PARTICLE_CHAMBER_HEIGHT / 2, PARTICLE_CHAMBER_WIDTH, PARTICLE_CHAMBER_HEIGHT );
	
	// Definition of the rectangle that separates the upper and lower portions
	// of the chamber.
	private static final double MEMBRANE_THICKNESS = 4; // In nanometers.
	private static final Rectangle2D MEMBRANE_RECT = new Rectangle2D.Double( -PARTICLE_CHAMBER_WIDTH / 2,
			-MEMBRANE_THICKNESS / 2, PARTICLE_CHAMBER_WIDTH, MEMBRANE_THICKNESS );
	
	// Maximum number of channels allowed on the membrane.
	private static int MAX_CHANNELS_ON_MEMBRANE = 8;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final ConstantDtClock clock;
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private ArrayList<MembraneChannel> membraneChannels = new ArrayList<MembraneChannel>();
    private MembraneChannel userControlledMembraneChannel = null;
    private EventListenerList listeners = new EventListenerList();
    private IHodgkinHuxleyModel hodgkinHuxleyModel = new FakeHodgkinHuxleyModel();
    private final ArrayList<Point2D> allowableChannelLocations = new ArrayList<Point2D>(MAX_CHANNELS_ON_MEMBRANE);

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MembraneDiffusionModel( NeuronClock clock ) {
    	
        this.clock = clock;
        
        clock.addClockListener(new ClockAdapter(){
			@Override
			public void clockTicked(ClockEvent clockEvent) {
				stepInTime( clockEvent.getSimulationTimeChange() );
			}
        });
        
        // Initialize the set of points where channels can be located.
        double interChannelDistance = MEMBRANE_RECT.getWidth() / (double)MAX_CHANNELS_ON_MEMBRANE;
        double channelLocationOffset = MEMBRANE_RECT.getMinX() + interChannelDistance / 2;
        for (int i = 0; i < MAX_CHANNELS_ON_MEMBRANE; i++){
        	allowableChannelLocations.add(new Point2D.Double(channelLocationOffset + i * interChannelDistance, MEMBRANE_RECT.getCenterY()));
        }
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    public Rectangle2D getParticleChamberRect(){
    	return new Rectangle2D.Double(PARTICLE_CHAMBER_RECT.getX(), PARTICLE_CHAMBER_RECT.getY(),
    			PARTICLE_CHAMBER_RECT.getWidth(), PARTICLE_CHAMBER_RECT.getHeight());
    }
    
    public Rectangle2D getMembraneRect(){
    	return new Rectangle2D.Double(MEMBRANE_RECT.getX(), MEMBRANE_RECT.getY(),
    			MEMBRANE_RECT.getWidth(), MEMBRANE_RECT.getHeight());
    }
    
    public ConstantDtClock getClock() {
        return clock;
    }    
    
    public ArrayList<Particle> getParticles(){
    	return particles;
    }
    
    public ArrayList<MembraneChannel> getMembraneChannels(){
    	return new ArrayList<MembraneChannel>(membraneChannels);
    }
    
    public IHodgkinHuxleyModel getHodgkinHuxleyModel(){
    	return hodgkinHuxleyModel;
    }
    
    public void reset(){
    	
    	// Reset the HH model.
    	hodgkinHuxleyModel.reset();
    	
    	// Remove the particles.
    	removeAllParticles();
    	
    	// Remove all membrane channels.
    	removeAllChannels();
    }
    
    /**
     * Inject a particle into either the upper or lower particle chamber.
     * 
     * @param particle - Assumed to have its motion strategy and location
     * already set.
     * @return true if able to add the particle, false if something prevents
     * the particle from being added.
     */
    public boolean injectParticle(final Particle particle){
    	// Validate that there are not already to many.
    	// TODO
    	
    	// Validate that the particle is in bounds.
    	if (!PARTICLE_CHAMBER_RECT.contains(particle.getPositionReference())){
    		return false;
    	}
    	
    	// Add the particle to the list and send appropriate notification.
    	particles.add(particle);
    	notifyParticleAdded(particle);
    	
    	// Listen for notifications from this particle that indicate that it
    	// is being removed from the model.
    	particle.addListener(new Particle.Adapter(){
    		public void removedFromModel() {
    			particles.remove(particle);
    		}
    	});
    	
    	// If we made it to this point, everything went okay.
    	return true;
    }
    
    /**
     * Starts a particle of the specified type moving through the
     * specified channel.  If one or more particles of the needed type exist
     * within the capture zone for this channel, one will be chosen and set to
     * move through, and another will be created to essentially take its place
     * (though the newly created one will probably be in a slightly different
     * place for better visual effect).  If none of the needed particles
     * exist, two will be created, and one will move through the channel and
     * the other will just hang out in the zone.
     * 
     * Note that it is not guaranteed that the particle will make it through
     * the channel, since it is possible that the channel could close before
     * the particle goes through it.
     * 
     * @param particleType
     * @param channel
     * @return
     */
    public void requestParticleThroughChannel(ParticleType particleType, MembraneChannel channel, double maxVelocity){

    	// Scan the capture zone for particles of the desired type.
    	CaptureZoneScanResult czsr = scanCaptureZoneForFreeParticles(channel.getCaptureZone(), particleType);
    	Particle particleToCapture = czsr.getClosestFreeParticle();
    	
    	if (czsr.getNumParticlesInZone() != 0){
    		// We found a particle to capture.  Set a motion strategy that
    		// will cause this particle to move across the membrane.
    		channel.createAndSetTraversalMotionStrategy(particleToCapture, maxVelocity);
    	}
    }
    
    private void stepInTime(double dt){
    
    	// Update the value of the membrane potential by stepping the
    	// Hodgkins-Huxley model.
    	hodgkinHuxleyModel.stepInTime( dt );
    	
    	// Step the channels.
    	for (MembraneChannel channel : membraneChannels){
    		channel.stepInTime( dt );
    	}
    	
    	// Step the particles.  Since particles may remove themselves as a
    	// result of being stepped, we need to copy the list in order to avoid
    	// concurrent modification exceptions.
    	ArrayList<Particle> particlesCopy = new ArrayList<Particle>(particles);
    	for (Particle particle : particlesCopy){
    		particle.stepInTime( dt );
    	}
    }
    
    /**
     * Scan the supplied capture zone for particles of the specified type.
     * 
     * @param zone
     * @param particleType
     * @return
     */
    private CaptureZoneScanResult scanCaptureZoneForFreeParticles(CaptureZone zone, ParticleType particleType){
    	Particle closestFreeParticle = null;
    	double distanceOfClosestParticle = Double.POSITIVE_INFINITY;
    	int totalNumberOfParticles = 0;
    	Point2D captureZoneOrigin = zone.getOriginPoint();
    	
    	for (Particle particle : particles){
    		if ((particle.getType() == particleType) && (particle.isAvailableForCapture()) && (zone.isPointInZone(particle.getPosition()))) {
    			totalNumberOfParticles++;
    			if (closestFreeParticle == null){
    				closestFreeParticle = particle;
    				distanceOfClosestParticle = captureZoneOrigin.distance(closestFreeParticle.getPosition());
    			}
    			else if (captureZoneOrigin.distance(closestFreeParticle.getPosition()) < distanceOfClosestParticle){
    				closestFreeParticle = particle;
    				distanceOfClosestParticle = captureZoneOrigin.distance(closestFreeParticle.getPosition());
    			}
    		}
    	}
    	
    	return new CaptureZoneScanResult(closestFreeParticle, totalNumberOfParticles);
    }
    
	public void addListener(Listener listener){
		listeners.add(Listener.class, listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(Listener.class, listener);
	}
	
	private void notifyChannelAdded(MembraneChannel channel){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.channelAdded(channel);
		}
	}
	
	private void notifyParticleAdded(Particle particle){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.particleAdded(particle);
		}
	}
	
	/**
	 * Add the provided channel at the specified rotational location.
	 * Locations are specified in terms of where on the circle of the membrane
	 * they are, with a value of 0 being on the far right, PI/2 on the top,
	 * PI on the far left, etc.
	 */
    private void addChannel(MembraneChannelTypes membraneChannelType, double angle){
    	
    	// TODO
    	
    }
    
    /**
     * Remove all particles (i.e. ions) from the simulation.
     */
    private void removeAllParticles(){
    	// Remove all particles.  This is done by telling each particle to
    	// send out notifications of its removal from the model.  All
    	// listeners, including this class, should remove their references in
    	// response.
    	ArrayList<Particle> particlesCopy = new ArrayList<Particle>(particles);
    	for (Particle particle : particlesCopy){
    		particle.removeFromModel();
    	}
    }
    
    /**
     * Remove all membrane channels from the simulation.
     */
    private void removeAllChannels(){
    	// Remove all membrane channels.  This is done by telling each
    	// channel to send out notifications of its removal from the model.
    	// All listeners, including this class, should remove their references
    	// in response.
    	ArrayList<MembraneChannel> membraneChannelsCopy = new ArrayList<MembraneChannel>(membraneChannels);
    	for (MembraneChannel membraneChannel : membraneChannelsCopy){
    		membraneChannel.removeFromModel();
    	}
    }
    
    /**
     * Add a membrane channel that is under user control, meaning that the user
     * is dragging it around.
     * 
     * @param membraneChannel
     */
    public void addUserControlledMembraneChannel(MembraneChannel membraneChannel){
    	assert membraneChannel != null;
    	userControlledMembraneChannel = membraneChannel;
    	notifyChannelAdded(membraneChannel);
    }
    
    /**
     * Release the membrane channel that is currently controlled (i.e. being
     * moved) by the user.  If the user released it within the bounds of the
     * particle chamber and there is a space on the membrane for it, it is
     * moved to the nearest open location on the membrane.  If it is released
     * outside of the particle chamber it is removed from the model. 
     */
    public void releaseUserControlledMembraneChannel(){
    	// Error checking.
    	assert userControlledMembraneChannel != null;
    	
    	if (PARTICLE_CHAMBER_RECT.contains(userControlledMembraneChannel.getCenterLocation())){
    		// The membrane channel was released close enough to the membrane
    		// that an attempt can be made to place it on the membrane. 
    		
    		// Make a list of the open locations on the membrane where the channel
    		// could be placed.
    		ArrayList<Point2D> openLocations = new ArrayList<Point2D>(allowableChannelLocations);
    		for (MembraneChannel membraneChannel : membraneChannels){
    			Point2D channelLocation = membraneChannel.getCenterLocation();
    			Point2D matchingLocation = null;
    			for (Point2D location : openLocations){
    				if (location.equals(channelLocation)){
    					// This position is taken.
    					matchingLocation = location;
    				}
    			}
    			if (matchingLocation != null){
    				// Remove the matching position from the list.
    				openLocations.remove(matchingLocation);
    			}
    			else{
    				System.out.println(getClass().getName() + "Error: Membrane channel not in one of the expected locations.");
    				assert false; // Shouldn't happen, debug if it does.
    			}
    		}
    		
    		if (openLocations.size() == 0){
    			// If there are no open locations, the channel can't be added.
    			// Remove it from the model.
    			userControlledMembraneChannel.removeFromModel();
    		}
    		else{
    			// Find the closest location.
    			Point2D closestOpenLocation = null;
    			for (Point2D openLocation : openLocations){
    				if (closestOpenLocation == null){
    					closestOpenLocation = openLocation;
    				}
    				else{
    					if (openLocation.distance(userControlledMembraneChannel.getCenterLocation()) < closestOpenLocation.distance(userControlledMembraneChannel.getCenterLocation())){
    						closestOpenLocation = openLocation;
    					}
    				}
    			}
    			
    			// Move the channel to the open location.
    			userControlledMembraneChannel.setCenterLocation(closestOpenLocation);
    			
    			// Put the channel on the list of active channels.
    			membraneChannels.add(userControlledMembraneChannel);
    			
    	    	// Listen for notifications from this channel that indicate that it
    	    	// is being removed from the model.
    			final MembraneChannel channelReference = userControlledMembraneChannel;
    	    	userControlledMembraneChannel.addListener(new MembraneChannel.Adapter(){
    	    		@Override
    	    		public void removed() {
    	    			// Take this channel off of the list of membrane channels.
    	    			membraneChannels.remove(channelReference);
    	    		}
    	    	});
    		}
    	}
    	else{
    		// The channel was released by the user in some inappropriate
    		// location, so just remove it from the model.
			userControlledMembraneChannel.removeFromModel();
    	}
    	
    	// Clear the reference to the membrane channel, since it is no longer
    	// controled by the user.
    	userControlledMembraneChannel = null;
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    /**
     * A class for reporting the closest particle to the origin in a capture
     * zone and the total number of particles in the zone.
     */
    public static class CaptureZoneScanResult {
    	final Particle closestFreeParticle;
    	final int numParticlesInZone;
		public CaptureZoneScanResult(Particle closestParticle,
				int numParticlesInZone) {
			super();
			this.closestFreeParticle = closestParticle;
			this.numParticlesInZone = numParticlesInZone;
		}
		protected Particle getClosestFreeParticle() {
			return closestFreeParticle;
		}
		protected int getNumParticlesInZone() {
			return numParticlesInZone;
		}
    }
    
    public interface Listener extends EventListener {
    	/**
    	 * Notification that a channel was added.
    	 * 
    	 * @param channel - Channel that was added.
    	 */
    	public void channelAdded(MembraneChannel channel);
    	
    	/**
    	 * Notification that a particle was added.
    	 * 
    	 * @param particle - Particle that was added.
    	 */
    	public void particleAdded(Particle particle);
    }
    
    public static class Adapter implements Listener{
		public void channelAdded(MembraneChannel channel) {}
		public void particleAdded(Particle particle) {}
    }
}
