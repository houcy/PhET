package edu.colorado.phet.ohm1d.regions;

import edu.colorado.phet.ohm1d.volt.WireRegion;
import edu.colorado.phet.ohm1d.phet_ohm1d.wire1d.WireParticle;

import java.util.ArrayList;

public class AndRegion implements WireRegion {
    ArrayList list = new ArrayList();

    public void addRegion( WireRegion wr ) {
        list.add( wr );
    }

    public boolean contains( WireParticle wp ) {
        for( int i = 0; i < list.size(); i++ ) {
            if( ( (WireRegion)list.get( i ) ).contains( wp ) ) {
                return true;
            }
        }
        return false;
    }
}
