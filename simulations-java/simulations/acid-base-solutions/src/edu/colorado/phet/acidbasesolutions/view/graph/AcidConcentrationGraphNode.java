package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class AcidConcentrationGraphNode extends AbstractConcentrationGraphNode {
    
    private static final int ACID_INDEX = 0;
    private static final int BASE_INDEX = 1;
    
    public AcidConcentrationGraphNode() {
        super();
        setMolecule( ACID_INDEX, ABSImages.HA_MOLECULE, ABSSymbols.HA, ABSConstants.HA_COLOR );
        setMolecule( BASE_INDEX, ABSImages.A_MINUS_MOLECULE, ABSSymbols.A_MINUS, ABSConstants.A_COLOR );
    }
    
    public void setAcidConcentration( double concentration ) {
        setConcentration( ACID_INDEX, concentration );
    }
    
    public void setBaseConcentration( double concentration ) {
        setConcentration( BASE_INDEX, concentration );
    }

}
