package edu.colorado.phet.molecularreactions;

import java.awt.*;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

/**
 * edu.colorado.phet.molecularreactions.MRConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRConfig {

    // Version
    public static final String VERSION = "0.00.05";

    // Prefix of the strings bundles
    public static final String LOCALIZATION_BUNDLE = "localization/MRStrings";

    // Debug flag
    public static boolean DEBUG = true;

    // Model constants
    public static double MAX_REACTION_THRESHOLD = 5E2;
    public static double DEFAULT_REACTION_THRESHOLD = MAX_REACTION_THRESHOLD * .7;
    public static final double MAX_SPEED = 3;
    public static final double LAUNCHER_MIN_THETA = -Math.PI / 4;
    public static final double LAUNCHER_MAX_THETA = Math.PI / 4;
    public static final double LAUNCHER_MAX_EXTENSION = 70;

    // View Constants
    public static final int BAR_CHART_MAX_Y = 20;
    public static final int PIE_CHART_DIAM_FACTOR = 3;
    public static final double ENERGY_VIEW_PROFILE_VERTICAL_SCALE = 1;

    // Colors
    public static final Color SPATIAL_VIEW_BACKGROUND = new Color( 255, 255, 225 );
    public static final Color MOLECULE_PANE_BACKGROUND = new Color( 237, 255, 235 );

    // Images
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String STOVE_IMAGE_FILE = IMAGE_DIRECTORY + "stove.png";
    public static final String FLAMES_IMAGE_FILE = IMAGE_DIRECTORY + "flames.gif";
    public static final String ICE_IMAGE_FILE = IMAGE_DIRECTORY + "ice.gif";
    public static final String PUMP_BODY_IMAGE_FILE = IMAGE_DIRECTORY + "pump-body.gif";
    public static final String PUMP_HANDLE_IMAGE_FILE = IMAGE_DIRECTORY + "pump-handle.gif";
}
