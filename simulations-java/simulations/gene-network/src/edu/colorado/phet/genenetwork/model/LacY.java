/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacZ, which is the model element that breaks up the
 * lactose.
 * 
 * @author John Blanco
 */
public class LacY extends SimpleModelElement {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------

	private static final double SIZE = 8; // In nanometers.
	private static final Paint ELEMENT_PAINT = new GradientPaint(new Point2D.Double(-SIZE, 0), 
			new Color(0, 200, 0), new Point2D.Double(SIZE * 5, 0), Color.WHITE);
	private static final double EXISTENCE_TIME = 25; // Seconds.
	
	// Attachment point for glucose.  Note that glucose generally only
	// attaches when it is bound up in lactose, so this is essentially the
	// lactose offset too.
	private static final Dimension2D GLUCOSE_ATTACHMENT_POINT_OFFSET = new PDimension(0, SIZE/2);
	
	// Amount of time that lactose is attached before it is "digested",
	// meaning that it is broken apart and released.
	private static final double LACTOSE_ATTACHMENT_TIME = 0.5;  // In seconds.
	
	// Amount of time after releasing one lactose molecule until it is okay
	// to start trying to attach to another.  This is needed to prevent the
	// LacZ from getting into a state where it can never fade out.
	private static final double RECOVERY_TIME = 0.250;  // In seconds.
	
	// These are used to determine whether a lactose molecule is close enough
	// that this molecule should try to grab it after it has been moved by
	// the user.
	private static final double LACTOSE_IMMEDIATE_GRAB_DISTANCE = 7; // In nanometers.
	private static final double LACTOSE_GRAB_DISTANCE = 15; // In nanometers.
	
	// For use in positioning in the cell membrane.
	private static final Random RAND = new Random();

	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------

	private Glucose glucoseAttachmentPartner = null;
	private AttachmentState glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private double lactoseAttachmentCountdownTimer;
	private double recoverCountdownTimer;
	
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------

	public LacY(IGeneNetworkModelControl model, Point2D initialPosition, boolean fadeIn) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT, fadeIn, EXISTENCE_TIME);
		if (model != null){
			setMotionStrategy(new StillnessMotionStrategy());
		}
	}
	
	public LacY(IGeneNetworkModelControl model, boolean fadeIn) {
		this(model, new Point2D.Double(), fadeIn);
	}
	
	public LacY(){
		this(null, false);
	}
	
	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	
	
	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		if (!isUserControlled()){			
			if (getExistenceState() == ExistenceState.EXISTING &&
				glucoseAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
				isEmbeddedInMembrane()){
				
				// Look for some lactose to attach to.
				glucoseAttachmentPartner = 
					getModel().getNearestLactose(getPositionRef(), PositionWrtCell.OUTSIDE_CELL, true);
				
				getEngagedToLactose();
			}
			else if (glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT){
				// See if the glucose is close enough to finalize the attachment.
				if (getGlucoseAttachmentPointLocation().distance(glucoseAttachmentPartner.getLacZAttachmentPointLocation()) < ATTACHMENT_FORMING_DISTANCE){
					// Finalize the attachment.
					completeAttachmentOfGlucose();
				}
			}
			else if (glucoseAttachmentState == AttachmentState.ATTACHED){
				lactoseAttachmentCountdownTimer -= dt;
				setOkayToFade(true);
			}
			else if (glucoseAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVALABLE){
				recoverCountdownTimer -= dt;
				if (recoverCountdownTimer <= 0){
					// Recovery is complete.
					glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
				}
			}
		}
	}
	
	@Override
	protected void setMotionStrategy(AbstractMotionStrategy motionStrategy) {
		// TODO Auto-generated method stub
		super.setMotionStrategy(motionStrategy);
	}

	private void getEngagedToLactose(){
		
		if (glucoseAttachmentPartner != null){
			// We found a lactose that is free, so start the process of
			// attaching to it.
			if (glucoseAttachmentPartner.considerProposalFrom(this) != true){
				assert false;  // As designed, this should always succeed, so debug if it doesn't.
			}
			else{
				glucoseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
				
				// Prevent fadeout from occurring while attached to lactose.
				setOkayToFade(false);
			}
		}
	}

	@Override
	public void setDragging(boolean dragging) {
		if (dragging && !isUserControlled()){
			// The user has grabbed this element.  Have it release any pending
			// attachments.
			if (glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT){
				glucoseAttachmentPartner.detach(this);
				glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
				glucoseAttachmentPartner = null;
				recoverCountdownTimer = 0;  // We are good to reattach as soon as we are released.
			}
		}
		else if (!dragging && isUserControlled()){
			// The user has released this element.  See if there are any
			// potential partners nearby.
			if (glucoseAttachmentPartner == null){
				checkForNearbyLactoseToGrab();
			}
		}
		super.setDragging(dragging);
	}

	/**
	 * Complete that process of attaching to glucose.  Created to avoid
	 * duplication of code.
	 */
	private void completeAttachmentOfGlucose() {
		glucoseAttachmentPartner.attach(this);
		glucoseAttachmentState = AttachmentState.ATTACHED;
		// Start the attachment timer/counter.
		lactoseAttachmentCountdownTimer = LACTOSE_ATTACHMENT_TIME;
	}
	
	private static Shape createShape(){
		// Start with a circle.
		RoundRectangle2D startingShape = new RoundRectangle2D.Double(-SIZE / 2, -SIZE / 2, SIZE, SIZE, SIZE / 3, SIZE / 3);
		Area area = new Area(startingShape);
		
		// Get the shape of a lactose molecule and shift it to the appropriate
		// position.
		Shape lactoseShape = Lactose.getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, SIZE/2 );
		lactoseShape = transform.createTransformedShape(lactoseShape);
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lactoseShape));
		return area;
	}

	@Override
	protected void onTransitionToExistingState() {
		// Pick a point on the cell membrane and set a motion strategy to get
		// there.
		// TODO: This should work with the model at some point to find free
		// locations.  This is essentially a rapid prototype at this point.
		double xDest = getModel().getInteriorMotionBounds().getCenterX() + RAND.nextDouble() * getModel().getInteriorMotionBounds().getWidth() / 2;
		double yDest = getModel().getCellMembraneRect().getCenterY();
		// Extend the motion bounds to allow this to move into the membrane.
		Rectangle2D motionBounds = getModel().getInteriorMotionBoundsAboveDna();
		motionBounds.setFrame(motionBounds.getX(), motionBounds.getY(), motionBounds.getWidth(),
				motionBounds.getHeight() + getModel().getCellMembraneRect().getHeight());
		// Set a motion strategy that will move this LacY to a spot on the
		// membrane.
		setMotionStrategy(new LinearMotionStrategy(motionBounds, getPositionRef(), 
				new Point2D.Double(xDest, yDest), 10));
	}
	
	/**
	 * Force a detach from the glucose molecule.  This was created to support
	 * the case where the user grabs the glucose molecule when it is attached
	 * to LacI, but may have other used.
	 * 
	 * @param glucose
	 */
	public void detach(Glucose glucose){
		
		// Make sure we are in the expect state.
		assert glucose == glucoseAttachmentPartner;
		assert glucoseAttachmentState == AttachmentState.ATTACHED || glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;
		
		// Clear the state variables that track attachment to lactose.
		glucoseAttachmentPartner = null;
		glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		recoverCountdownTimer = RECOVERY_TIME;
		
		// It is now okay for the LacZ to fade out of existence if it needs to.
		setOkayToFade(true);
	}
	
	/**
	 * Request an immediate attachment for a glucose molecule (which should
	 * be half of a lactose molecule).  This is generally used when the
	 * glucose was manually moved by the user to a location that is quite
	 * close to this lacY.
	 * 
	 * @param glucose
	 * @return true if it can attach, false if it already has a different
	 * partner.
	 */
	public boolean requestImmediateAttach(Glucose glucose){
		
		// Shouldn't get a request for its current partner.
		assert glucose != glucoseAttachmentPartner;
		
		if (glucoseAttachmentState == AttachmentState.ATTACHED){
			// We are already attached to a glucose molecule, so we can attach
			// to a different one.
			return false;
		}
		
		if (glucoseAttachmentPartner != null){
			// We were moving towards attachment to a different glucose, so
			// break off the engagement.
			glucoseAttachmentPartner.detach(this);
		}
		
		// Attach to this new glucose molecule.
		if (glucose.considerProposalFrom(this) != true){
			// This should never happen, since the glucose requested the
			// attachment, so it should be debuged if it does.
			System.err.println(getClass().getName() + "- Error: Proposal refused by element that requested attachment.");
			assert false;
		}
		
		// Everything should now be clear for finalizing the actual attachment.
		glucoseAttachmentPartner = glucose;
		completeAttachmentOfGlucose();
		
		return true;
	}
	
	/**
	 * Get the location in absolute space of the attachment point for this
	 * type of model element.
	 */
	public Point2D getGlucoseAttachmentPointLocation(){
		return new Point2D.Double(getPositionRef().getX() + GLUCOSE_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + GLUCOSE_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public static Dimension2D getGlucoseAttachmentPointOffset() {
		return new PDimension(GLUCOSE_ATTACHMENT_POINT_OFFSET);
	}
	
	/**
	 * Check if there is any lactose in the immediate vicinity and, if so,
	 * attempt to establish a pending attachment with it.  This is generally
	 * used when the user moves this element manually to some new location.
	 */
	private void checkForNearbyLactoseToGrab(){
		assert glucoseAttachmentPartner == null;  // Shouldn't be doing this if we already have a partner.
		Glucose nearestLactose = getModel().getNearestLactose(getPositionRef(), PositionWrtCell.OUTSIDE_CELL, false);
		if (nearestLactose != null && nearestLactose.getPositionRef().distance(getPositionRef()) < LACTOSE_GRAB_DISTANCE){
			if (nearestLactose.breakOffPendingAttachments(this)){
				// Looks like the lactose can be grabbed.
				glucoseAttachmentPartner = nearestLactose;
				getEngagedToLactose();
				if (nearestLactose.getPositionRef().distance(getPositionRef()) < LACTOSE_IMMEDIATE_GRAB_DISTANCE){
					// Attach right now.
					completeAttachmentOfGlucose();
				}
			}
		}
	}
	
	private static final double ERROR_TOLERENCE = 0.01;
	private boolean isEmbeddedInMembrane(){
		boolean isEmbeddedInMembrane = false;
		Rectangle2D cellMembraneRect = getModel().getCellMembraneRect();
		if ( cellMembraneRect != null &&
			 getPositionRef().getY() + ERROR_TOLERENCE > cellMembraneRect.getCenterY() &&
			 getPositionRef().getY() - ERROR_TOLERENCE < cellMembraneRect.getCenterY()){
			
			isEmbeddedInMembrane = true;
		}
		
		return isEmbeddedInMembrane;
	}
}
