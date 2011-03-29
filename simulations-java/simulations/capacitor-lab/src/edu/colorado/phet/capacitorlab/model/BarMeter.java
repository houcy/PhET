// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class for all bar meter model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BarMeter {

    private final BatteryCapacitorCircuit circuit;

    // observable properties
    private final WorldLocationProperty locationProperty;
    private final Property<Boolean> visibleProperty;
    private final Property<Double> valueProperty;

    public BarMeter( final BatteryCapacitorCircuit circuit, final World world, Point3D location, boolean visible ) {

        this.circuit = circuit;
        this.locationProperty = new WorldLocationProperty( world, location );
        this.visibleProperty = new Property<Boolean>( visible );
        this.valueProperty = new Property<Double>( getCircuitValue() );

        // change the value when the circuit changes
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
            public void circuitChanged() {
                setValue( getCircuitValue() );
            }
        } );
    }

    /**
     * Subclasses implement this to get the value being measured from the circuit.
     * @return
     */
    protected abstract double getCircuitValue();

    protected BatteryCapacitorCircuit getCircuit() {
        return circuit;
    }

    public void reset() {
        locationProperty.reset();
        visibleProperty.reset();
        valueProperty.reset();
    }

    public void addLocationObserver( SimpleObserver o ) {
        locationProperty.addObserver( o );
    }

    public void setLocation( Point3D location ) {
        locationProperty.setValue( location );
    }

    public Point3D getLocationReference() {
        return locationProperty.getValue();
    }

    public void addVisibleObserver( SimpleObserver o ) {
        visibleProperty.addObserver( o );
    }

    public Property<Boolean> getVisibleProperty() {
        return visibleProperty;
    }

    public void setVisible( boolean visible ) {
        visibleProperty.setValue( visible );
    }

    public boolean isVisible() {
        return visibleProperty.getValue();
    }

    public void addValueObserver( SimpleObserver o ) {
        valueProperty.addObserver( o );
    }

    protected void setValue( double value ) {
        valueProperty.setValue( value );
    }

    public double getValue() {
        return valueProperty.getValue();
    }
}
