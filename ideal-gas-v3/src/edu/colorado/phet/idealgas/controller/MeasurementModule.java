/**
 * Class: MeasurementModule
 * Class: edu.colorado.phet.idealgas.controller
 * User: Ron LeMaster
 * Date: Sep 16, 2004
 * Time: 7:56:59 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.view.RulerGraphic;
import edu.colorado.phet.idealgas.view.monitors.EnergyHistogramDialog;
import edu.colorado.phet.idealgas.view.monitors.PressureSliceGraphic;

import java.util.ResourceBundle;

public class MeasurementModule extends IdealGasModule {

    private static ResourceBundle localizedStrings;
    static {
        localizedStrings = ResourceBundle.getBundle( "localization/MeasurementModule" );
    }

    private EnergyHistogramDialog histogramDlg;
    private DefaultInteractiveGraphic rulerGraphic;
    private PressureSlice pressureSlice;
    private AbstractClock clock;
    private boolean pressureSliceEnabled;
    private PressureSliceGraphic pressureSliceGraphic;

    public MeasurementModule( AbstractClock clock ) {
        super( clock, localizedStrings.getString( "Measurements" ));
        this.clock = clock;
        setControlPanel( new MeasurementControlPanel( this ) );
        rulerGraphic = new RulerGraphic( getApparatusPanel() );
    }

    public void activate( PhetApplication application ) {
        super.activate( application );

        // Set up the energy histogramDlg. Note that we can't do this in the constructor
        // because we a reference to the application's Frame
        histogramDlg = new EnergyHistogramDialog( application.getApplicationView().getPhetFrame(),
                                                  (IdealGasModel)getModel() );
        histogramDlg.setVisible( true );
    }

    public void deactivate( PhetApplication app ) {
        histogramDlg.setVisible( false );
    }

    public void setRulerEnabed( boolean rulerEnabled ) {
        if( rulerEnabled ) {
            getApparatusPanel().addGraphic( rulerGraphic, Integer.MAX_VALUE );
        }
        else {
            getApparatusPanel().removeGraphic( rulerGraphic );
        }
        getApparatusPanel().repaint();
    }

    public void setPressureSliceEnabled( boolean pressureSliceEnabled ) {
        if( pressureSlice == null ) {
            pressureSlice = new PressureSlice( getBox(), (IdealGasModel)getModel(), clock );
        }
        this.pressureSliceEnabled = pressureSliceEnabled;
        this.pressureSliceEnabled = pressureSliceEnabled;
        if( pressureSliceEnabled ) {
            getModel().addModelElement( pressureSlice );
            pressureSliceGraphic = new PressureSliceGraphic( getApparatusPanel(),
                                                             pressureSlice,
                                                             getBox() );
            addGraphic( pressureSliceGraphic, 20 );
        }
        else {
            getApparatusPanel().removeGraphic( pressureSliceGraphic );
            getModel().removeModelElement( pressureSlice );
        }
    }


}
