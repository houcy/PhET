/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Simple model of a DC battery.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Battery {

    // immutable properties
    private final Point3D location;
    private final double length, diameter;
    
    // mutable properties
    private double voltage;
    private Polarity polarity;
    
    private final EventListenerList listeners;
    
    public Battery( Point3D location, double length, double diameter, double voltage ) {
        this.location = new Point3D.Double( location.getX(), location.getY(), location.getZ() );
        this.length = length;
        this.diameter = diameter;
        this.voltage = voltage;
        this.polarity = getPolarity( voltage );
        listeners = new EventListenerList();
    }
    
    public Shape createBodyShapeLocal() {
        return createBodyShape( new Point3D.Double() );
    }
    
    private Shape createBodyShape( Point3D origin ) {
        double x = origin.getX() - ( diameter / 2 );
        double y = origin.getY() - ( length / 2 );
        return new Rectangle2D.Double( x, y, diameter, length );
    }

    public Shape createPositiveTerminalShapeLocal() {
        return createPositiveTerminalShape( new Point3D.Double() );
    }
    
    public Shape createPositiveTerminalShapeGlobal() {
        return createPositiveTerminalShape( location );
    }
    
    private Shape createPositiveTerminalShape( Point3D origin ) {
        final double terminalWidth = diameter / 3;
        final double terminalHeight = diameter / 4;
        double x = origin.getX() - ( terminalWidth / 2 );
        double y;
        if ( polarity == Polarity.POSITIVE ) {
            y = origin.getY() - ( length / 2 ) - ( terminalHeight / 2 );
        }
        else {
            y = origin.getY() + ( length / 2 ) - ( terminalHeight / 2 );
        }
        return new Rectangle2D.Double( x, y, terminalWidth, terminalHeight );
    }
    
    public Shape createNegativeTerminalShapeLocal() {
        return createNegativeTerminalShape( new Point3D.Double() );
    }
    
    public Shape createNegativeTerminalShapeGlobal() {
        return createNegativeTerminalShape( location );
    }
    
    private Shape createNegativeTerminalShape( Point3D origin ) {
        final double terminalWidth = diameter / 1.5;
        final double terminalHeight = diameter / 5;
        double x = origin.getX() - ( terminalWidth / 2 );
        double y;
        if ( polarity == Polarity.POSITIVE ) {
            y = origin.getY() + length/2 - terminalHeight;
        }
        else {
            y = origin.getY() - length/2;
        }
        return new Ellipse2D.Double( x, y, terminalWidth, terminalHeight );
    }
    
    public Point3D getLocationReference() {
        return location;
    }
    
    public double getX() {
        return location.getX();
    }
    
    public double getY() {
        return location.getY();
    }
    
    public double getZ() {
        return location.getZ();
    }
    
    public double getLength() {
        return length;
    }
    
    public double getDiameter() {
        return diameter;
    }
    
    /**
     * Gets the battery voltage.
     * (design doc symbol: V_battery)
     * 
     * @param voltage
     */
    public void setVoltage( double voltage ) {
        if ( voltage != this.voltage ) {
            this.voltage = voltage;
            fireVoltageChanged();
            setPolarity( getPolarity( voltage ) );
        }
    }
    
    public double getVoltage() {
        return voltage;
    }
    
    private void setPolarity( Polarity polarity ) {
        if ( polarity != this.polarity ) {
            this.polarity = polarity;
            firePolarityChanged();
        }
    }
    
    public Polarity getPolarity() {
        return polarity;
    }
    
    private static Polarity getPolarity( double voltage ) {
        return ( voltage >= 0 ) ? Polarity.POSITIVE : Polarity.NEGATIVE;
    }
    
    public boolean topTerminalContains( Point3D p ) {
        if ( polarity == Polarity.POSITIVE ) {
            return createPositiveTerminalShapeGlobal().contains( p.getX(), p.getY() );
        }
        else {
            return createNegativeTerminalShapeGlobal().contains( p.getX(), p.getY() );
        }
    }
    
    public boolean bottomTerminalContains( Point3D p ) {
        if ( polarity == Polarity.NEGATIVE ) {
            return createPositiveTerminalShapeGlobal().contains( p.getX(), p.getY() );
        }
        else {
            return createNegativeTerminalShapeGlobal().contains( p.getX(), p.getY() );
        }
    }
    
    public interface BatteryChangeListener extends EventListener {
        public void voltageChanged();
        public void polarityChanged();
    }
    
    public static class BatteryChangeAdapter implements BatteryChangeListener {
        public void voltageChanged() {}
        public void polarityChanged() {}
    }
    
    public void addBatteryChangeListener( BatteryChangeListener listener ) {
        listeners.add( BatteryChangeListener.class, listener );
    }
    
    public void removeBatteryChangeListener( BatteryChangeListener listener ) {
        listeners.remove( BatteryChangeListener.class, listener );
    }
    
    private void fireVoltageChanged() {
        for ( BatteryChangeListener listener : listeners.getListeners( BatteryChangeListener.class ) ) {
            listener.voltageChanged();
        }
    }
    
    private void firePolarityChanged() {
        for ( BatteryChangeListener listener : listeners.getListeners( BatteryChangeListener.class ) ) {
            listener.polarityChanged();
        }
    }
}
