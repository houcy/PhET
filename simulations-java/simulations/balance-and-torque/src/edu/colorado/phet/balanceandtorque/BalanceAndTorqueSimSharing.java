// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Class where all the user components are defined for items in the sim, which
 * is necessary for the "sim sharing" (a.k.a. data collection) feature.
 *
 * @author John Blanco
 */
public class BalanceAndTorqueSimSharing {
    public static enum UserComponents implements IUserComponent {

        // Tabs
        introTab, balanceLabTab, gameTab,

        // Check box controls
        massLabelsCheckBox, rulersCheckBox, forceFromObjectsCheckBox,
        levelCheckBox,

        // Buttons
        redXRemoveSupportsButton, addSupportsButton, removeSupportsButton,

        // Movable masses
        singleBrick, stackOfTwoBricks, stackOfThreeBricks, stackOfFourBricks,
        mediumTrashCan, mediumBucket, tire, television, sodaBottle, smallRock,
        smallTrashCan, pottedPlant, tinyRock, flowerPot, cinderBlock,
        mediumRock, largeTrashCan, mysteryObject, barrel, fireExtinguisher,
        bigRock, largeBucket, fireHydrant, man, woman, boy, girl, smallBucket

    }
}
