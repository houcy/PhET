/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.quantum.view.PhotonGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * DipoleGraphicManager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModelElementGraphicManager extends MriModel.ChangeAdapter {

    private PhetPCanvas phetPCanvas;
    // The PNode on which all dipole graphics are placed
    private PNode canvas;
    // A map of model elements to their graphics
    private HashMap modelElementToGraphicMap = new HashMap();
    // List of graphic classes that are currently invisible
    private List invisibleGraphicClasses = new ArrayList();


    public ModelElementGraphicManager( PhetPCanvas phetPCanvas, PNode canvas ) {
        this.phetPCanvas = phetPCanvas;
        this.canvas = canvas;
    }

    public void scanModel( MriModel model ) {
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            ModelElement modelElement = (ModelElement)modelElements.get( i );
            modelElementAdded( modelElement );
        }
    }

    public void modelElementAdded( ModelElement modelElement ) {
        PNode graphic = null;
        if( modelElement instanceof Dipole ) {
            graphic = new DipoleGraphic( (Dipole)modelElement );
        }
        if( modelElement instanceof SampleChamber ) {
            graphic = new SampleChamberGraphic( (SampleChamber)modelElement );
        }
        if( modelElement instanceof Electromagnet ) {
            graphic = new ElectromagnetGraphic( (Electromagnet)modelElement );
        }
        if( modelElement instanceof RadiowaveSource ) {
            graphic = new RadiowaveSourceGraphic( (RadiowaveSource)modelElement, phetPCanvas );
        }
        if( modelElement instanceof Photon ) {
            graphic = PhotonGraphic.getInstance( (Photon)modelElement );
        }
        if( modelElement instanceof PlaneWaveMedium ) {
            PlaneWaveMedium pwm = (PlaneWaveMedium)modelElement;
            graphic = new PlaneWaveGraphic( pwm,
                                            pwm.getOrigin(),
                                            pwm.getLength(),
                                            pwm.getLength(),
                                            pwm.getSpeed(),
                                            0.5,
                                            Color.black );
        }

        if( graphic != null ) {
            if( invisibleGraphicClasses.contains( graphic.getClass() ) ) {
                graphic.setVisible( false );
            }
            modelElementToGraphicMap.put( modelElement, graphic );
            canvas.addChild( graphic );
        }
    }

    public void modelElementRemoved( ModelElement modelElement ) {
        PNode graphic = (PNode)modelElementToGraphicMap.get( modelElement );
        if( graphic != null ) {
            canvas.removeChild( graphic );
            modelElementToGraphicMap.remove( modelElement );
        }
    }

    /**
     * Sets the visibility of all graphics of a specified type
     *
     * @param graphicClass
     * @param isVisible
     */
    public void setAllOfTypeVisible( Class graphicClass, boolean isVisible ) {
        Collection graphics = modelElementToGraphicMap.values();
        for( Iterator iterator = graphics.iterator(); iterator.hasNext(); ) {
            Object obj = iterator.next();
            if( graphicClass.isInstance( obj ) ) {
                PNode graphic = (PNode)obj;
                graphic.setVisible( isVisible );
            }
        }

        if( !isVisible ) {
            invisibleGraphicClasses.add( graphicClass );
        }
        else {
            invisibleGraphicClasses.remove( graphicClass );
        }
    }
}
