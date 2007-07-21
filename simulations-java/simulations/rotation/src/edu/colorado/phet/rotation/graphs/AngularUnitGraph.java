package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 11:08:26 AM
 */
public class AngularUnitGraph extends RotationGraph {
    private AngleUnitModel angleUnitModel;
    private String unitsRad;
    private String unitsDeg;
    private double minRad;
    private double maxRad;

    public AngularUnitGraph( PhetPCanvas pSwingCanvas, ISimulationVariable simulationVariable, String label, String title, AngleUnitModel angleUnitModel, String unitsRad, String unitsDeg, double minRad, double maxRad, PImage thumb, RotationModel motionModel, boolean editable, TimeSeriesModel timeSeriesModel, UpdateStrategy updateStrategy, double maxDomainValue, RotationPlatform iPositionDriven ) {
        super( pSwingCanvas, simulationVariable, label, title, unitsRad, minRad, maxRad, thumb, motionModel, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
        this.angleUnitModel = angleUnitModel;
        this.unitsRad = unitsRad;
        this.unitsDeg = unitsDeg;
        this.minRad = minRad;
        this.maxRad = maxRad;
        angleUnitModel.addListener( new AngleUnitModel.Listener() {
            public void changed() {
                updateUnits();
            }
        } );
        updateUnits();
    }

    public void addSeries( final ControlGraphSeries series ) {
        super.addSeries( series );
        series.setUnits( getUnitsString() );
    }

    private void updateUnits() {
        getVerticalAxis().setLabel( getTitle() + " (" + getUnitsString() + ")" );
        setVerticalRange( isRadians() ? minRad : toDegrees( minRad ),
                          isRadians() ? maxRad : toDegrees( maxRad ) );
        for( int i = 0; i < getSeriesCount(); i++ ) {
            ControlGraphSeries series = getControlGraphSeries( i );
            series.setUnits( getUnitsString() );
        }
        getDynamicJFreeChartNode().clear();
        //add back all existing data in the right units
        for( int i = 0; i < getSeriesCount(); i++ ) {
            ControlGraphSeries series = getControlGraphSeries( i );
            for( int k = 0; k < series.getObservableTimeSeries().getSampleCount(); k++ ) {
                TimeData data = series.getObservableTimeSeries().getData( k );
                getDynamicJFreeChartNode().addValue( getSeriesIndex( series ), data.getTime(), getValue( data ) );
            }
        }

        forceUpdateAll();
    }

    private double getValue( TimeData data ) {
        return isRadians() ? data.getValue() : toDegrees( data.getValue() );
    }

    protected void handleDataAdded( int seriesIndex, TimeData timeData ) {
//        super.handleDataAdded( seriesIndex, timeData );
        addValue( seriesIndex, timeData.getTime(), getValue( timeData ) );
    }

    private boolean isRadians() {
        return angleUnitModel.isRadians();
    }

    private double toDegrees( double rad ) {
        return rad * 360.0 / 2 / Math.PI;
    }

    private double toRadians( double degrees ) {
        return degrees / 360.0 * 2 * Math.PI;
    }

    private String getUnitsString() {
        return isRadians() ? unitsRad : unitsDeg;
    }

    protected void handleValueChanged() {
        getSimulationVariable().setValue( viewToModel( getSliderValue() ) );
    }

    private double viewToModel( double sliderValue ) {
        return isRadians() ? sliderValue : toRadians( sliderValue );
    }

    protected void updateSliderValue() {
        getJFreeChartSliderNode().setValue( getValue( getSimulationVariable().getData() ) );
    }
}
