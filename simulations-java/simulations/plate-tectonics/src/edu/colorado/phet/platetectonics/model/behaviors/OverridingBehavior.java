// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.platetectonics.model.PlateMotionPlate;

public class OverridingBehavior extends PlateBehavior {

    public OverridingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );
    }

    @Override public void stepInTime( float millionsOfYears ) {
    }
}
