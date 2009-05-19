/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayControl;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageNode;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageType;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode;
import edu.colorado.phet.nuclearphysics.view.MultiNucleusDecayLinearTimeChart;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the radiometric
 * element model is displayed.
 *
 * @author John Blanco
 */
public class DecayRatesCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 900;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.29; // 0 = all the way up, 1 = all the way down.
    
    // Constants that control where the charts are placed.
    private final double PROPORTION_CHART_FRACTION = 0.45;   // Fraction of canvas for proportion chart.
    
    // Constants that control the appearance of the canvas.
    private static final Color BUCKET_AND_BUTTON_COLOR = Color.red;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DecayRatesModel _model;
    private HashMap _mapNucleiToNodes = new HashMap();
    private AtomicNucleus.Listener _decayEventListener;
    private NuclearDecayProportionChart _proportionsChart;
    private PNode _particleLayer;
    private PNode _graphLayer;
	private BucketOfNucleiNode _bucketNode;
	private GradientButtonNode _addMultipleNucleiButtonNode;
    
    //----------------------------------------------------------------------------
    // Builder + Constructor
    //----------------------------------------------------------------------------
    
    public DecayRatesCanvas( DecayRatesModel decayRatesModel ) {

    	_model = decayRatesModel;
    	
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR, 
                        getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Add the PNodes that will act as layers for the particles and graphs.
        _particleLayer = new PNode();
        addWorldChild(_particleLayer);
        _graphLayer = new PNode();
        addScreenChild(_graphLayer);
        
        // Create and add the node the represents the bucket from which nuclei
        // can be extracted and added to the play area.
        Rectangle2D _bucketRect = _model.getHoldingAreaRect();
        _bucketNode = new BucketOfNucleiNode( _bucketRect.getWidth(), _bucketRect.getHeight(), BUCKET_AND_BUTTON_COLOR );
        _particleLayer.addChild(_bucketNode);
        _bucketNode.setOffset( _bucketRect.getX(), _bucketRect.getY() );
        
        // Add the diagram that will depict the relative concentration of
        // pre- and post-decay nuclei.
        _proportionsChart = new NuclearDecayProportionChart.Builder(Carbon14Nucleus.HALF_LIFE * 3.2, 
        		Carbon14Nucleus.HALF_LIFE, NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL, 
        		NuclearPhysicsConstants.CARBON_COLOR).
        		postDecayElementLabel(NuclearPhysicsStrings.NITROGEN_14_CHEMICAL_SYMBOL).
        		postDecayLabelColor(NuclearPhysicsConstants.NITROGEN_COLOR).
        		pieChartEnabled(true).
        		showPostDecayCurve(true).
        		timeMarkerLabelEnabled(true).
        		build();
        _graphLayer.addChild(_proportionsChart);
        
        // Register with the model for notifications of nuclei coming and
        // going.
        _model.addListener( new NuclearDecayListenerAdapter(){
            public void modelElementAdded(Object modelElement){
            	handleModelElementAdded(modelElement);
            };

            public void modelElementRemoved(Object modelElement){
            	handleModelElementRemoved(modelElement);
            };
            
            public void nucleusTypeChanged(){
            	_proportionsChart.clear();
            }
        });
        
        // Create a listener for decay events so the chart can be informed.
        _decayEventListener = new AtomicNucleus.Adapter(){
            public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){

            	if (atomicNucleus instanceof AbstractDecayNucleus){
            		AbstractDecayNucleus nucleus = (AbstractDecayNucleus)atomicNucleus;
            		if (nucleus.hasDecayed()){
            			// This was a decay event.  Inform the chart.
            			_proportionsChart.addDecayEvent(nucleus.getAdjustedActivatedTime(), 
            					_model.getPercentageDecayed());
            		}
            	}
            }
        };
        
        // Add the button that allows the user to add multiple nuclei at once.
        // Position it just under the bucket and scale it so that its size is
        // proportionate to the bucket.
        _addMultipleNucleiButtonNode = new GradientButtonNode(NuclearPhysicsStrings.ADD_TEN, 12, BUCKET_AND_BUTTON_COLOR);
        double addButtonScale = (_bucketRect.getWidth() / _addMultipleNucleiButtonNode.getFullBoundsReference().width) * 0.4;
        _addMultipleNucleiButtonNode.scale(addButtonScale);
        _addMultipleNucleiButtonNode.setOffset(_bucketRect.getCenterX() - _addMultipleNucleiButtonNode.getFullBoundsReference().width / 2, 
        		_bucketRect.getMaxY());
        _particleLayer.addChild(_addMultipleNucleiButtonNode);

        // Register to receive button pushes.
        _addMultipleNucleiButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	addMultipleNucleiFromBucket( 10 );
            }
        });


        
        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            
            /**
             * This method is called when the canvas is resized.  In response,
             * we generally pass this event on to child nodes that need to be
             * aware of it.
             */
            public void componentResized( ComponentEvent e ) {
                update();
            }
        } );
        
        // Add a listener for when the clock gets reset.
        _model.getClock().addClockListener( new ClockAdapter() {
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // When the simulation time is reset, clear the chart.
            	_proportionsChart.clear();
            }
        });
    }

    /**
     * Update the layout on the canvas.
     */
	public void update() {
		
		super.update();
		
		_proportionsChart.componentResized(new Rectangle2D.Double( 0, 0, getWidth(), getHeight() * PROPORTION_CHART_FRACTION ) );
		_proportionsChart.setOffset(0, getHeight() - _proportionsChart.getFullBoundsReference().height * 1.02);
		
	}
	
    /**
     * Extract the specified number of nuclei from the bucket and place them
     * on the canvas.  If there aren't enough in the bucket, add as many as
     * possible.
     * 
     * @param numNucleiToAdd - Number of nuclei to add.
     * @return - Number of nuclei actually added.
     */
    private int addMultipleNucleiFromBucket(int numNucleiToAdd){
    	
    	int numberOfNucleiObtained;
    	for (numberOfNucleiObtained = 0; numberOfNucleiObtained < numNucleiToAdd; numberOfNucleiObtained++){
    		AtomicNucleusNode nucleusNode = _bucketNode.extractAnyNucleusFromBucket();
    		if (nucleusNode == null){
    			// The bucket must be empty, so there is nothing more to do.
    			break;
    		}
    		else{
    			// Make the node a child of the appropriate layer on the canvas. 
    			_particleLayer.addChild(nucleusNode);
    			
    			// Move the nucleus to an open location outside of the holding
    			// area.
    			nucleusNode.getNucleusRef().setPosition(_model.findOpenNucleusLocation());
    			
    			// Activate the nucleus so that it will decay.
    			AtomicNucleus nucleus = nucleusNode.getNucleusRef();
    			if (nucleus instanceof NuclearDecayControl){
    				((NuclearDecayControl)nucleus).activateDecay();
    			}
    		}
    	}
    	
    	return numberOfNucleiObtained;
    }
    
	private void handleModelElementAdded(Object modelElement) {

    	if (modelElement instanceof AtomicNucleus){
    		// A new nucleus has been added to the model.  Create a
    		// node for it and add it to the nucleus-to-node map.
    		AtomicNucleus nucleus = (AtomicNucleus) modelElement;
    		AtomicNucleusImageNode atomicNucleusNode = 
    			new AtomicNucleusImageNode( nucleus, AtomicNucleusImageType.GRADIENT_SPHERE );
    		
    		// Map this node and nucleus together.
    		_mapNucleiToNodes.put(nucleus, atomicNucleusNode);
    		
    		if ( _model.isNucleusInHoldingArea(nucleus)){
    			// The nucleus is in the holding area in the model, so place
    			// it in the bucket in order to convey this to the user.
    			_bucketNode.addNucleus(atomicNucleusNode);
    		}
    		else{
    			// The nucleus is outside of the holding area, so just add it
    			// directly to the appropriate layer.
    			_particleLayer.addChild( atomicNucleusNode );
    		}
    		
    		// Listen to the nucleus for decay events.
    		((AtomicNucleus)modelElement).addListener(_decayEventListener);
    	}
    	else {
    		System.err.println("WARNING: Unrecognized model element added, unable to create node for canvas.");
    	}
	}

	/**
	 * Handle a notification from the model that indicates that an element
	 * (e.g. a nucleus) was removed.  This generally means that the
	 * corresponding view elements should also go away.
	 * 
	 * @param modelElement
	 */
    private void handleModelElementRemoved(Object modelElement) {
    	
    	if (modelElement instanceof AtomicNucleus){
    		AtomicNucleusNode nucleusNode = (AtomicNucleusNode)_mapNucleiToNodes.get(modelElement);
    		if (nucleusNode == null){
    			System.err.println(this.getClass().getName() + ": Error - Could not find node for removed model element.");
    		}
    		else {
    			((AtomicNucleus)modelElement).removeListener(_decayEventListener);
    			
    			// Remove the node from the canvas.
    			PNode child = _particleLayer.removeChild( nucleusNode );
    			if (child == null){
        			System.err.println(this.getClass().getName() + ": Error - Could not remove nucleus from canvas.");
    			}
    		}
    		_mapNucleiToNodes.remove( modelElement );
    	}
	}
    
    /**
     * Reset all the nuclei back to their pre-decay state.
     */
    private void resetAllNuclei(){
        Set entries = _mapNucleiToNodes.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            AtomicNucleus nucleus = (AtomicNucleus)entry.getKey();
            nucleus.reset();
        }
    }

	/**
     * Sets the view back to the original state when sim was first started.
     */
    public void reset(){
    }
}
