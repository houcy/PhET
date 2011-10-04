// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroShaker;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * This shaker adds sodium nitrate to the model when shaken
 *
 * @author Sam Reid
 */
public class SodiumNitrateShaker extends MicroShaker {
    public SodiumNitrateShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, MicroModel model ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type, model );
    }

    @Override protected void addCrystal( MicroModel model, ImmutableVector2D outputPoint ) {

        //Attempt 100 times to randomly create a crystal with a correct balance of components
        //If no success after 100 random tries, just take the last attempt
        //This tends to work in much less than 100 tries, such as 3-4 tries
        SodiumNitrateCrystal crystal = null;
        int count = 0;
        while ( crystal == null || crystal.count( Sodium.class ) != crystal.count( Nitrate.class ) && count++ < 100 ) {
            crystal = new SodiumNitrateCrystal( outputPoint, randomAngle() ) {{grow( 6 );}};
        }
        model.addSodiumNitrateCrystal( crystal );
    }
}