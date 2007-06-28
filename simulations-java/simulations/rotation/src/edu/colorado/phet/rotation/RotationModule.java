package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.timeseries.model.TimeModelClock;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.view.RotationSimulationPanel;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:05:16 AM
 */
public class RotationModule extends AbstractRotationModule {

    private VectorViewModel vectorViewModel;

    protected RotationModel createModel( TimeModelClock clock ) {
        return new RotationModel( clock );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel() {
        return new RotationSimulationPanel( this );
    }

    public VectorViewModel getVectorViewModel() {
        if( vectorViewModel == null ) {//state is constructed lazily outside of constructor for use in template method
            vectorViewModel = new VectorViewModel();
        }
        return vectorViewModel;
    }

}
