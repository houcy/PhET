package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.semiconductor.macro.SemiconductorModule;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatterySpinner;
import edu.colorado.phet.semiconductor.macro.circuit.particles.WireParticle;
import edu.colorado.phet.semiconductor.macro.circuit.particles.WireParticleGraphic;
import edu.colorado.phet.semiconductor.macro.doping.*;
import edu.colorado.phet.semiconductor.macro.energy.states.Speed;
import edu.colorado.phet.semiconductor.util.RectangleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 7:17:44 PM
 * Copyright (c) Feb 7, 2004 by Sam Reid
 */
public class CircuitSection implements ModelElement, Graphic, DopantDropListener, ConductionListener, Speed {

    MacroCircuit circuit;
    MacroCircuitGraphic circuitGraphic;
    private SemiconductorModule module;
    private ModelViewTransform2D transform;
    ArrayList particles = new ArrayList();
    ArrayList particleGraphics = new ArrayList();
    private BatterySpinner batterySpinner;
    ArrayList dopantSlots = new ArrayList();
    ArrayList circuitListeners = new ArrayList();
    private JButton jb;
    private ArrayList dopantChangeListeners = new ArrayList();
    private boolean conductionAllowed;
    private Shape dopantRect;
    private double macroSpeed = 0;

    public CircuitSection( SemiconductorModule module, ModelViewTransform2D transform, double x, double y, double width, double height, int numDopantRegions ) throws IOException {
        this.module = module;
        this.transform = transform;
        double resistorThickness = 1;
        circuit = new MacroCircuit( x, y, width, height, resistorThickness );
        circuitGraphic = new MacroCircuitGraphic( circuit, transform );

        double dx = .5;
        double length = circuit.getLength();
        int numParticles = (int)( length / dx + 1 );
        double particleX = 0;
        for( int i = 0; i < numParticles; i++ ) {
            WireParticle p = new WireParticle( particleX, circuit );
            particles.add( p );
            Graphic wireParticleGraphic = new WireParticleGraphic( p, transform, MacroCircuitGraphic.getParticleImage() );
            particleGraphics.add( wireParticleGraphic );
            particleX += dx;
        }

        batterySpinner = new BatterySpinner( circuit.getBattery() );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                relayoutBatterySpinner();
                relayoutClearButton();
            }
        } );
        jb = new JButton( "Clear Dopants" );
        jb.setEnabled( false );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearDopants();
            }
        } );
        addDopantSlots( numDopantRegions );
    }

    private void clearDopantSlots() {
        dopantSlots.clear();
    }

    private void addDopantSlots( int numDopantRegions ) {
        Color slotColor = ( new Color( 220, 155, 225 ) );
//        Color slotColor=Color.yellow;
        for( int i = 0; i < numDopantRegions; i++ ) {
            //get shape for the dopant.
            Shape dopantSlotShape = getDopantSlotShape( i, numDopantRegions );
//            System.out.println( "dopantSlotShape = " + dopantSlotShape );
            DopantSlot ds = null;
            try {
                ds = new DopantSlot( null, dopantSlotShape, transform, slotColor );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            dopantSlots.add( ds );
        }
    }

    private void clearDopants() {
        for( int i = 0; i < numDopantSlots(); i++ ) {
            dopantSlotAt( i ).setDopantType( null );
        }
        updateCircuitListeners();
        updateDopantChangeListeners();
        jb.setEnabled( false );
    }

    public Shape getDopantSlotShape( int index, int numDopantSlots ) {
        index++;
        double resLen = circuit.getResistor().getLength();
        double dist = index * 2 - 1;
        double maxDist = numDopantSlots * 2;
        double myfrac = dist / maxDist;
        double segmentWidth = resLen / numDopantSlots;
        PhetVector center = circuit.getResistor().getLocation( myfrac * resLen );
        double height = circuit.getResistor().getHeight();

        RoundRectangle2D.Double rect = createCenteredRect( center, segmentWidth, height, .3, .3 );
//        System.out.println( "x=" + rect.getX() + ", y=" + rect.getY() + ", width=" + rect.getWidth() + ", h=" + rect.getHeight() );
        return rect;
    }

    private RoundRectangle2D.Double createCenteredRect( PhetVector center, double width, double height, double dx, double dy ) {
        return new RoundRectangle2D.Double( center.getX() - width / 2, center.getY() - height / 2, width, height, dx, dy );
    }

    public int numDopantSlots() {
        return dopantSlots.size();
    }

    public DopantSlot dopantSlotAt( int i ) {
        return (DopantSlot)dopantSlots.get( i );
    }

    private void relayoutClearButton() {
        Point viewPtRes = transform.modelToView( circuit.getResistor().getStartPosition() );
        jb.setBounds( viewPtRes.x, viewPtRes.y - 75, jb.getPreferredSize().width, jb.getPreferredSize().height );
    }

    public void stepInTime( double v ) {
        if( conductionAllowed ) {
            for( int i = 0; i < particles.size(); i++ ) {
                WireParticle wireParticle = (WireParticle)particles.get( i );
                wireParticle.setSpeed( getMacroSpeed() );
                wireParticle.stepInTime( v );
            }
        }
    }

    private double getMacroSpeed() {
        return macroSpeed;
    }

    public double getSpeed() {
        double volts = getCircuit().getBattery().getVoltage();
        double scale = 1.0 / 15.0;
//        double scale=1.0/25.0;
        double speed = volts * scale;
        if( speed >= 0 && speed < .021 ) {
            speed = .021;
        }
        if( speed <= 0 && speed > -.021 ) {
            speed = .021;
        }
        if( speed < 0 ) {
            speed = -speed;
        }
//        System.out.println("Volts="+volts+", speed = " + speed);
        return speed;
    }

    private void relayoutBatterySpinner() {
        Point viewPtBatt = transform.modelToView( circuit.getBattery().getEndPosition() );
        JSpinner batterySp = this.batterySpinner.getSpinner();
        batterySp.setBounds( viewPtBatt.x, viewPtBatt.y + batterySp.getPreferredSize().height, batterySp.getPreferredSize().width, batterySp.getPreferredSize().height );
        batterySp.repaint();
    }

    public void paint( Graphics2D graphics2D ) {
        circuitGraphic.paint( graphics2D );
//        dopantGraphic.paint(graphics2D);
        for( int i = 0; i < dopantSlots.size(); i++ ) {
            DopantSlot ds = dopantSlotAt( i );
            ds.paint( graphics2D );
        }
        for( int i = 0; i < particleGraphics.size(); i++ ) {
            Graphic graphic = (Graphic)particleGraphics.get( i );
            graphic.paint( graphics2D );
        }
    }

    public MacroCircuit getCircuit() {
        return circuit;
    }

    public BatterySpinner getBatterySpinner() {
        return batterySpinner;
    }

    PhetVector getCenter( Rectangle2D rect ) {
        return new PhetVector( rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2 );
    }

    public void addDopantChangeListener( DopantChangeListener dcl ) {
        dopantChangeListeners.add( dcl );
    }

    public void dopantDropped( DopantGraphic dopant ) {
        DopantSlot closest = null;
        double closestDist = 0;
        dopantRect = dopant.getShape();//the guy being dropped
        for( int i = 0; i < numDopantSlots(); i++ ) {
            DopantSlot ds = dopantSlotAt( i );
            Shape shape = ds.getViewShape();
            double dist = dopant.getCenter().getSubtractedInstance( getCenter( shape ) ).getMagnitude();
            boolean ok = dopantSlotAt( i ).getViewShape().intersects( dopantRect.getBounds2D() );
            if( ok && ( closest == null || dist < closestDist ) ) {
                closest = ds;
                closestDist = dist;
            }
        }
        if( closest != null ) {
            closest.setDopantType( dopant.getType() );
            updateCircuitListeners();
            module.removeDopantGraphic( dopant );
            jb.setEnabled( true );
            updateDopantChangeListeners();
        }
    }

    PhetVector getCenter( Shape s ) {
        Rectangle2D r = s.getBounds2D();
        return RectangleUtils.getCenter( r );
    }

    private void updateDopantChangeListeners() {
        for( int i = 0; i < dopantChangeListeners.size(); i++ ) {
            DopantChangeListener listener = (DopantChangeListener)dopantChangeListeners.get( i );
            listener.dopingChanged( this );
        }
    }

    public void addCircuitListener( CircuitListener cl ) {
        circuitListeners.add( cl );
    }

    private void updateCircuitListeners() {
        for( int i = 0; i < circuitListeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)circuitListeners.get( i );
            circuitListener.circuitChanged( this );
        }
    }

    public MacroCircuitGraphic getCircuitGraphic() {
        return circuitGraphic;
    }

    public JButton getClearDopantButton() {
        return jb;
    }

    public void setConductionAllowed( boolean allowed ) {
        this.conductionAllowed = allowed;
    }

    public boolean isDiodeType( DopantType n, DopantType p ) {
        if( numDopantSlots() == 2 && dopantSlotAt( 0 ).getDopantType() == n && dopantSlotAt( 1 ).getDopantType() == p ) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setSingleSection() {
        setSectionCount( 1 );
    }

    public void setDoubleSection() {
        setSectionCount( 2 );
    }

    public void setTripleSection() {
        setSectionCount( 3 );
    }

    private void setSectionCount( int count ) {
//        clearDopants();
        clearDopantSlots();
        addDopantSlots( count );
    }

    public void setMacroSpeed( double avg ) {
        this.macroSpeed = avg;
    }
}
