/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

/**
 * PhetCommonResources is a singleton that provides access to phetcommon's JAR resources.
 */
public class PhetCommonResources {

    // Symbolic names for localized string keys
    public static final String STRING_CLOCK_PLAY = "Common.ClockControlPanel.Play";
    public static final String STRING_CLOCK_PAUSE = "Common.ClockControlPanel.Pause";
    public static final String STRING_CLOCK_STEP = "Common.ClockControlPanel.Step";
    public static final String STRING_CLOCK_RESTART = "Common.ClockControlPanel.Restart";
    public static final String STRING_CLOCK_REWIND = "Common.ClockControlPanel.Rewind";
    public static final String STRING_RESET_ALL = "ControlPanel.button.resetAll";
    public static final String STRING_YES = "Common.choice.yes";
    public static final String STRING_NO = "Common.choice.no";
    public static final String STRING_HELP_MENU_HELP = "Common.HelpMenu.Help";

    // Symbolic names for image resources
    public static final String IMAGE_CLOSE_BUTTON = "buttons/closeButton.png";
    public static final String IMAGE_MINIMIZE_BUTTON = "buttons/minimizeButton.png";
    public static final String IMAGE_MAXIMIZE_BUTTON = "buttons/maximizeButton.png";
    public static final String IMAGE_FAST_FORWARD = "clock/FastForward24.gif";
    public static final String IMAGE_PAUSE = "clock/Pause24.gif";
    public static final String IMAGE_PLAY = "clock/Play24.gif";
    public static final String IMAGE_REWIND = "clock/Rewind24.gif";
    public static final String IMAGE_RESTART = IMAGE_REWIND;
    public static final String IMAGE_STEP_FORWARD = "clock/StepForward24.gif";
    public static final String IMAGE_STOP = "clock/Stop24.gif";

    // preferred physical font names for various ISO language codes
    private static final String PREFERRED_FONTS_RESOURCE = "localization/phetcommon-fonts.properties";

    private static PhetResources INSTANCE = new PhetResources( "phetcommon" );

    /* not intended for instantiation */
    private PhetCommonResources() {
    }

    public static PhetResources getInstance() {
        return INSTANCE;
    }

    /**
     * Reads a list of preferred physical font names from the phetcommon-fonts.properties resource.
     * Returns the names as an array.
     * If no preferred fonts are specified, null is returned.
     */
    public static String[] getPreferredFontNames( Locale locale ) {
        String[] names = null;
        Properties fontProperties = new Properties();
        try {
            fontProperties.load( PhetCommonResources.getInstance().getResourceAsStream( PREFERRED_FONTS_RESOURCE ) );
            String localeString = LocaleUtils.localeToString( locale );
            String key = "preferredFonts." + localeString; // eg, preferredFonts.ja
            String allNames = fontProperties.getProperty( key );
            if ( allNames != null ) {
                names = allNames.split( "," ); // comma separated, no whitespace
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * Convenience method for accessing a localized String from phetcommon.
     *
     * @param key the key for which to look up the String value
     * @return the localized String
     */
    public static String getString( String key ) {
        return INSTANCE.getLocalizedString( key );
    }
    
    /**
     * Convenience method for accessing an image file from phetcommon.
     *
     * @param name the name of the image
     * @return BufferedImage
     */
    public static BufferedImage getImage( String name ) {
        return INSTANCE.getImage( name );
    }
}
