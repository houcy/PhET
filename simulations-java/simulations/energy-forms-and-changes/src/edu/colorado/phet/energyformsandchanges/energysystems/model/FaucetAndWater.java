// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

/**
 * Class that represents a faucet that can be turned on to provide mechanical
 * energy to other energy system elements.
 *
 * @author John Blanco
 */
public class FaucetAndWater extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final Vector2D OFFSET_FROM_CENTER_TO_WATER_ORIGIN = new Vector2D( 0.065, 0.08 );
    private static final double MAX_ENERGY_PRODUCTION_RATE = 200; // In joules/second.
    private static final double ENERGY_CHUNK_VELOCITY = 0.07; // In meters/second.
    private static final double FLOW_PER_CHUNK = 0.4;  // Empirically determined to get desired energy chunk emission rate.
    private static final double MAX_WATER_WIDTH = 0.015; // In meters.
    private static final double MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER = 0.5; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    public final Property<Double> flowProportion = new Property<Double>( 0.0 );
    public final BooleanProperty enabled = new BooleanProperty( true );

    // Shape of the water coming from the faucet.  The shape is specified
    // relative to the water origin.
    public final Property<Shape> waterShape = new Property<Shape>( null );

    private double flowSinceLastChunk = 0;
    private final BooleanProperty energyChunksVisible;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected FaucetAndWater( BooleanProperty energyChunksVisible ) {
        super( EnergyFormsAndChangesResources.Images.FAUCET_ICON );
        this.energyChunksVisible = energyChunksVisible;

        flowProportion.addObserver( new SimpleObserver() {
            public void update() {
                updateWaterShape();
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {

        if ( isActive() ) {
            // Check if time to emit an energy chunk and, if so, do it.
            flowSinceLastChunk += flowProportion.get() * dt;
            if ( flowSinceLastChunk > FLOW_PER_CHUNK ) {
                energyChunkList.add( new EnergyChunk( EnergyType.MECHANICAL,
                                                      getPosition().plus( OFFSET_FROM_CENTER_TO_WATER_ORIGIN ),
                                                      new Vector2D( 0, -ENERGY_CHUNK_VELOCITY ),
                                                      energyChunksVisible ) );
                flowSinceLastChunk = 0;
            }

            // Update energy chunk positions.
            for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {

                // Make the chunk fall.
                energyChunk.translateBasedOnVelocity( dt );

                // Remove it if it is out of visible range.
                if ( getPosition().plus( OFFSET_FROM_CENTER_TO_WATER_ORIGIN ).distance( energyChunk.position.get() ) > MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER ) {
                    energyChunkList.remove( energyChunk );
                }
            }
        }

        // Generate the appropriate amount of energy.
        return new Energy( EnergyType.MECHANICAL, MAX_ENERGY_PRODUCTION_RATE * flowProportion.get() * dt, -Math.PI / 2 );
    }

    @Override public void deactivate() {
        super.deactivate();
        enabled.set( false );
    }

    @Override public void activate() {
        super.activate();
        enabled.set( true );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectFaucetButton;
    }

    private void updateWaterShape() {
        if ( flowProportion.get() == 0 ) {
            waterShape.set( new Rectangle2D.Double( 0, 0, 1E-7, 1E-7 ) );
        }
        else {
            double waterWidth = flowProportion.get() * MAX_WATER_WIDTH;
            waterShape.set( new Rectangle2D.Double( -waterWidth / 2,
                                                    -MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER,
                                                    waterWidth,
                                                    MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER ) );
        }
    }
}
