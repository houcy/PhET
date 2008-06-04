/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Double;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.SVGNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class is the "view" for the particle container.  This is where the
 * information about the nature of the image that is used to depict the
 * container is encapsulated.
 *
 * @author John Blanco
 */
public class ParticleContainerNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Only a portion of the image will correspond to the location of the
    // particle container within the model.  The rest of the image is "fluff",
    // meaning that it provides visual cues to the user.  These constants
    // define where within the image the particle container should be mapped.
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT   = 0.35;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT  = 0.05;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_BOTTOM = 0.2;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_TOP    = 0.05;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final ParticleContainer m_container;
    private final MultipleParticleModel m_model;
    private PPath m_particleArea;
    private PImage m_cupImage;

    private double m_containmentAreaWidth;
    private double m_containmentAreaHeight;
    private double m_containmentAreaOffsetX;
    private double m_containmentAreaOffsetY;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode(PhetPCanvas canvas, MultipleParticleModel model) throws IOException {
        
        super();

        m_model               = model;
        m_container           = model.getParticleContainer();
        
        // Internal initialization.
        m_containmentAreaWidth  = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containmentAreaHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();

        // Set up the image that will be used.
        m_cupImage = StatesOfMatterResources.getImageNode(StatesOfMatterConstants.COFFEE_CUP_IMAGE);
        
        // Scale the cup image based on the size of the container.
        double neededImageWidth = 
            m_containmentAreaWidth / (1 - NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT - NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT);
        m_cupImage.setScale( neededImageWidth / m_cupImage.getWidth() );
        
        // Calculate the offset for the area within the overall image where
        // the particles will be contained.
        m_containmentAreaOffsetX = neededImageWidth * NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT;
        m_containmentAreaOffsetY = m_cupImage.getFullBounds().height * NON_CONTAINER_IMAGE_FRACTION_FROM_TOP;
        
        // Add the particle area, which is where the particles will be
        // maintained, which is a rectangular area that matches the size of
        // the container in the model.
        m_particleArea = new PPath(new Rectangle2D.Double(0, 0, m_containmentAreaWidth, m_containmentAreaHeight));
        m_particleArea.setOffset( m_containmentAreaOffsetX, m_containmentAreaOffsetY );
        m_particleArea.setStrokePaint( Color.RED );
        m_particleArea.setStroke( new BasicStroke(50) );
        
        addChild(m_cupImage);
        addChild(m_particleArea);
        
        // Position this node so that the origin of the canvas, i.e. position
        // x=0, y=0, is at the lower left corner of the container.
        double xPos = -m_cupImage.getFullBoundsReference().width * NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT;
        double yPos = -m_cupImage.getFullBoundsReference().height * (1 - NON_CONTAINER_IMAGE_FRACTION_FROM_BOTTOM);
        setOffset( xPos, yPos );
        
        // TODO: JPB TBD - For testing.
//        PPath containerRect = new PPath(new Rectangle2D.Double(1000, 1000, 240, 240));
//        containerRect.setStrokePaint( Color.RED );
//        addChild(containerRect);
        
        // Set ourself to be non-pickable so that we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        update();
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public List getParticleNodes() {
        return Collections.unmodifiableList(m_particleArea.getChildrenReference());
    }

    public ParticleNode getParticleNode(int i) {
        return (ParticleNode)m_particleArea.getChild(i);
    }

    public void addParticleNode(ParticleNode particleNode) {
        m_particleArea.addChild(particleNode);
    }
    
    public void reset(){
        // TODO: JPB TBD.
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    // TODO: JPB TBD - Is this needed?
    private void update() {
    }
}