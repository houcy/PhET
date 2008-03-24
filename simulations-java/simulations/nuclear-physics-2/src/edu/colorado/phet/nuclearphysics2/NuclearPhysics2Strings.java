/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NuclearPhysics2Strings {
    
    /* not intended for instantiation */
    private NuclearPhysics2Strings() {}
    
    public static final String LABEL_POSITION = NuclearPhysics2Resources.getString( "label.position");
    public static final String LABEL_ORIENTATION = NuclearPhysics2Resources.getString( "label.orientation");
    
    public static final String TITLE_EXAMPLE_MODULE = NuclearPhysics2Resources.getString( "title.exampleModule" );
    
    public static final String TITLE_ALPHA_RADIATION_MODULE = NuclearPhysics2Resources.getString( "ModuleTitle.AlphaDecayModule" );
    public static final String TITLE_FISSION_ONE_NUCLEUS_MODULE = NuclearPhysics2Resources.getString( "ModuleTitle.SingleNucleusFissionModule" );

    public static final String UNITS_ORIENTATION = NuclearPhysics2Resources.getString( "units.orientation");
    public static final String UNITS_TIME = NuclearPhysics2Resources.getString( "units.time" );

    public static final String POLONIUM_211_CHEMICAL_SYMBOL = NuclearPhysics2Resources.getString( "Polonium211Graphic.Symbol" );
    public static final String POLONIUM_211_ISOTOPE_NUMBER = NuclearPhysics2Resources.getString( "Polonium211Graphic.Number" );

    public static final String LEAD_207_CHEMICAL_SYMBOL = NuclearPhysics2Resources.getString( "Lead207Graphic.Symbol" );
    public static final String LEAD_207_ISOTOPE_NUMBER = NuclearPhysics2Resources.getString( "Lead207Graphic.Number" );
    
    public static final String POLONIUM_LEGEND_LABEL = NuclearPhysics2Resources.getString( "<html>Polonium<sup><font size=-1> 211</font><sup></html>" );
    public static final String LEAD_LEGEND_LABEL = NuclearPhysics2Resources.getString( "<html>Lead<sup><font size=-1> 207</font><sup></html>" );

    
}
