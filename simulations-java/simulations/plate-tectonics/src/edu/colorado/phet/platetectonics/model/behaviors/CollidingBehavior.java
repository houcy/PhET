// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;

public class CollidingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public CollidingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        plate.getLithosphere().moveToFront();
        plate.getCrust().moveToFront();
    }

    @Override public void stepInTime( float millionsOfYears ) {
        timeElapsed += millionsOfYears;
        while ( getPlate().isLeftPlate() && getPlate().getCrust().getTopBoundary().getFirstSample().getPosition().x > -700000 ) {
            getPlate().addLeftSection();
        }
        while ( !getPlate().isLeftPlate() && getPlate().getCrust().getTopBoundary().getLastSample().getPosition().x < 700000 ) {
            getPlate().addRightSection();
        }
        float sign = plate.isLeftPlate() ? 1 : -1;
        final List<Sample> topSamples = getPlate().getCrust().getTopBoundary().samples;
        final Boundary lithosphereBottomBoundary = getPlate().getLithosphere().getBottomBoundary();
        final List<Sample> bottomSamples = lithosphereBottomBoundary.samples;
        float[] oldXes = new float[topSamples.size()];
        float[] newXes = new float[topSamples.size()];
        float[] oldTopYs = new float[topSamples.size()];
        float[] oldBottomYs = new float[bottomSamples.size()];
        float[] oldAreas = new float[topSamples.size() - 1];
        float[] scales = new float[topSamples.size()];
        for ( int i = 0; i < oldXes.length; i++ ) {
            oldXes[i] = topSamples.get( i ).getPosition().x;
            oldTopYs[i] = topSamples.get( i ).getPosition().y;
            oldBottomYs[i] = bottomSamples.get( i ).getPosition().y;
            newXes[i] = computeNewX( millionsOfYears, sign, oldXes[i] );
            if ( i != 0 ) {
                // width times average height
                oldAreas[i - 1] = ( oldXes[i] - oldXes[i - 1] ) * ( ( oldTopYs[i - 1] + oldTopYs[i] ) / 2 - ( oldBottomYs[i - 1] + oldBottomYs[i] ) / 2 );
            }
        }
        scales[0] = ( newXes[1] - newXes[0] ) / ( oldXes[1] - oldXes[0] );
        scales[scales.length - 1] = ( newXes[scales.length - 1] - newXes[scales.length - 2] ) / ( oldXes[scales.length - 1] - oldXes[scales.length - 2] );
        for ( int i = 1; i < scales.length - 1; i++ ) {
            float leftScale = ( newXes[i] - newXes[i - 1] ) / ( oldXes[i] - oldXes[i - 1] );
            float rightScale = ( newXes[i + 1] - newXes[i] ) / ( oldXes[i + 1] - oldXes[i] );
            scales[i] = ( leftScale + rightScale ) / 2;
        }
        for ( Region region : new Region[] { getPlate().getLithosphere(), getPlate().getCrust() } ) {
            for ( int i = 0; i < getPlate().getCrust().getTopBoundary().samples.size(); i++ ) {
                float centerY = ( getPlate().getCrust().getTopBoundary().samples.get( i ).getPosition().y
                                  + getPlate().getCrust().getBottomBoundary().samples.get( i ).getPosition().y ) / 2;
                for ( Boundary boundary : region.getBoundaries() ) {

                    Sample sample = boundary.samples.get( i );

                    final ImmutableVector3F currentPosition = sample.getPosition();
                    final float currentX = currentPosition.x;
                    final float currentY = currentPosition.y;
                    float newX = currentX == 0 ? 0 : computeNewX( millionsOfYears, sign, currentX );
                    float newY = ( currentY - centerY ) / scales[i] + centerY;
                    final float yOffset = newY - currentY;
                    final ImmutableVector3F offset3d = new ImmutableVector3F( newX - currentX, yOffset, 0 );
                    sample.setPosition( currentPosition.plus( offset3d ) );

                    // kind of a weird hack, but it helps us store less amounts of massive information
                    if ( boundary == getPlate().getCrust().getTopBoundary() ) {
                        getPlate().getTerrain().xPositions.set( i, newX );
                        for ( int row = 0; row < getPlate().getTerrain().getNumRows(); row++ ) {
                            final TerrainSample terrainSample = getPlate().getTerrain().getSample( i, row );
                            terrainSample.setElevation( terrainSample.getElevation() + yOffset );
                        }
                    }
                }
            }
        }

        // create some mountains!
        for ( int col = 0; col < getPlate().getCrust().getTopBoundary().samples.size(); col++ ) {
            for ( int row = 0; row < getPlate().getTerrain().getNumRows(); row++ ) {
                final TerrainSample terrainSample = getPlate().getTerrain().getSample( col, row );
                float mountainRatio = (float) MathUtil.clamp( 0, ( terrainSample.getElevation() - 6000 ) / ( 13000 - 6000 ), 1 );
                final float elevationOffset = (float) ( mountainRatio * ( Math.random() * 1000 - 500 ) );
                terrainSample.setElevation( terrainSample.getElevation() + elevationOffset );
                if ( row == getPlate().getTerrain().getFrontZIndex() ) {
                    final Sample sample = getPlate().getCrust().getTopBoundary().samples.get( col );
                    sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( 0, elevationOffset, 0 ) ) );
                }
            }
        }

        // copy elevation from left plate to right plate on the center line
        if ( !getPlate().isLeftPlate() ) {
            for ( int row = 0; row < getPlate().getTerrain().getNumRows(); row++ ) {
                final float elevation = getOtherPlate().getTerrain().getSample( getOtherPlate().getTerrain().getNumColumns() - 1, row ).getElevation();
                getPlate().getTerrain().getSample( 0, row ).setElevation(
                        elevation );
                final Sample sample = getPlate().getCrust().getTopBoundary().samples.get( 0 );
                sample.setPosition( new ImmutableVector3F( sample.getPosition().x, elevation, sample.getPosition().z ) );
            }
        }

        getPlate().getTerrain().elevationChanged.updateListeners();

        // we want to slide along the mantle instead!
//        getPlate().getMantle().getTopBoundary().borrowPosition( getPlate().getLithosphere().getBottomBoundary() );

        // sew aesthenosphere to lithosphere bottom

        float padding = 750;
        int xIndex = 0;
        Sample leftSample = lithosphereBottomBoundary.getFirstSample();
        for ( Sample mantleSample : getPlate().getMantle().getTopBoundary().samples ) {
            // too far to the left
            if ( leftSample.getPosition().x > mantleSample.getPosition().x ) {
                continue;
            }

            int rightIndex = xIndex + 1;

            // too far to the right
            if ( rightIndex > lithosphereBottomBoundary.samples.size() - 1 ) {
                break;
            }
            Sample rightSample = lithosphereBottomBoundary.samples.get( rightIndex );
            while ( rightSample.getPosition().x < mantleSample.getPosition().x && rightIndex + 1 < lithosphereBottomBoundary.samples.size() ) {
                rightIndex++;
                rightSample = lithosphereBottomBoundary.samples.get( rightIndex );
            }

            // couldn't go far enough
            if ( rightSample.getPosition().x < mantleSample.getPosition().x ) {
                break;
            }

            // how leftSample and rightSample surround our x
            assert leftSample.getPosition().x <= mantleSample.getPosition().x;
            assert rightSample.getPosition().x >= mantleSample.getPosition().x;

            // interpolate between their y values
            float ratio = ( mantleSample.getPosition().x - leftSample.getPosition().x ) / ( rightSample.getPosition().x - leftSample.getPosition().x );
            mantleSample.setPosition( new ImmutableVector3F( mantleSample.getPosition().x,
                                                             padding + leftSample.getPosition().y * ( 1 - ratio ) + rightSample.getPosition().y * ratio,
                                                             mantleSample.getPosition().z ) );
        }

        // TODO: different terrain sync so we can handle height differences
//        getPlate().fullSyncTerrain();
    }

    private float computeNewX( float millionsOfYears, float sign, float currentX ) {
        assert !Float.isNaN( millionsOfYears );
        final int exponentialFactor = 45;
        float newX = (float) ( currentX * Math.exp( -Math.pow( millionsOfYears, 2 ) / exponentialFactor ) );
        final float maxXDelta = sign * 30000f / 2 * millionsOfYears;
        final float delta = newX - currentX;
        float ratio = Math.min( 1, Math.abs( currentX / 600000 ) );
        ratio *= 0.75;

        newX = currentX + ( 1 - ratio ) * delta + ratio * maxXDelta;

        assert !Float.isNaN( newX );
        return newX;
    }
}
