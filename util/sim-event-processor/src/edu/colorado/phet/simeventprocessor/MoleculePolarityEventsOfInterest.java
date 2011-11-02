// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * @author Sam Reid
 */
public class MoleculePolarityEventsOfInterest {
    //Events to search for in Molecule Polarity Tab 1
    public static EntryList getMoleculePolarityEventsOfInterest() {
        return new EntryList() {{
            add( "check box", "pressed", param( "text", "Bond Dipole" ) );
            add( "check box", "pressed", param( "text", "Partial Charges" ) );
            add( "check box", "pressed", param( "text", "Bond Character" ) );

            add( "radio button", "pressed", param( "description", "Surface type" ), param( "value", "ELECTROSTATIC_POTENTIAL" ) );
            add( "radio button", "pressed", param( "description", "Surface type" ), param( "value", "ELECTROSTATIC_POTENTIAL" ) );
            add( "radio button", "pressed", param( "description", "Surface type" ), param( "value", "NONE" ) );

            add( "radio button", "pressed", param( "description", "Electric field on" ), param( "value", "true" ) );
            add( "radio button", "pressed", param( "description", "Electric field on" ), param( "value", "false" ) );

            add( "mouse", "dragged", param( "atom", "A" ) );
            add( "mouse", "dragged", param( "atom", "B" ) );
        }};
    }
}
