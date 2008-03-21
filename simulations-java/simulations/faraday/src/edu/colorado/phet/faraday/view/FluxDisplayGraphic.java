/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic2;
import edu.colorado.phet.faraday.model.PickupCoil;

/**
 * FluxDisplayGraphic display flux and emf information related to a pickup coil.
 * This is used for debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FluxDisplayGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    private static final DecimalFormat FORMAT = new DefaultDecimalFormat( "###0.00" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _pickupCoilModel;
    private PhetTextGraphic2 _fluxValue, _deltaFluxValue, _emfValue;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param component
     * @param pickupCoilModel
     */
    public FluxDisplayGraphic( Component component, PickupCoil pickupCoilModel ) {
        super();
        
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );
        
        _fluxValue = new PhetTextGraphic2( component, FONT, "?", Color.YELLOW, 0, -25 );
        _deltaFluxValue = new PhetTextGraphic2( component, FONT, "?", Color.YELLOW, 0, 0 );
        _emfValue = new PhetTextGraphic2( component, FONT, "?", Color.YELLOW, 0, 25 );
        
        addGraphic( _fluxValue );
        addGraphic( _deltaFluxValue );
        addGraphic( _emfValue );
        
        update();
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /**
     * Update the display when this graphic becomes visible.
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            update();
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Update the display when the model changes.
     */
    public void update() {
        if ( isVisible() ) {
            double flux = _pickupCoilModel.getFlux();
            double deltaFlux = _pickupCoilModel.getDeltaFlux();
            double emf = _pickupCoilModel.getEmf();

            _fluxValue.setText( "Flux = " + FORMAT.format( flux ) + " W" );
            _deltaFluxValue.setText( "Delta Flux = " + FORMAT.format( deltaFlux ) + " W" );
            _emfValue.setText( "EMF = " + FORMAT.format( emf ) + " V" );
        }
    }
}
