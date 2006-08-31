/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.BoxGraphic;
import edu.colorado.phet.molecularreactions.view.CompositeMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.SpatialSimpleMoleculeGraphic;
import edu.colorado.phet.collision.Box2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * SpatialView
 * <p/>
 * A view of an MRModel that shows all the molecules in the model, and the box containing them, in a 2D layout.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpatialView extends PNode {
    Color background = new Color( 255, 255, 200 );
    PNode moleculeLayer = new PNode();
    PNode bondLayer = new PNode();
    PNode boxLayer = new PNode();

    public SpatialView( MRModel model ) {
        PPath canvas = new PPath( new Rectangle2D.Double( 0,0, 300, 400), new BasicStroke( 1 ));
        canvas.setPaint( background );
        addChild( canvas );

        // Add the layers to the canvas in their z order
        addChild( bondLayer );
        addChild( moleculeLayer );
        addChild( boxLayer );

        // Create the graphic manager and add required factories to it
        ModelElementGraphicManager megm = new ModelElementGraphicManager( model, canvas );
        megm.addGraphicFactory( new SimpleMoleculeGraphicFactory( moleculeLayer ) );
        megm.addGraphicFactory( new BoxGraphicFactory() );
        megm.addGraphicFactory( new CompositeMoleculeGraphicFactory() );
        megm.addGraphicFactory( new ProvisionalBondGraphicFactory() );
        megm.scanModel();
    }


    private class CompositeMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected CompositeMoleculeGraphicFactory() {
            super( CompositeMolecule.class, bondLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new CompositeMoleculeGraphic( (CompositeMolecule)modelElement );
        }
    }

    private class SimpleMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected SimpleMoleculeGraphicFactory( PNode moleculeLayer ) {
            super( SimpleMolecule.class, moleculeLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new SpatialSimpleMoleculeGraphic( (SimpleMolecule)modelElement ) ;
        }
    }

    private class BoxGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected BoxGraphicFactory() {
            super( Box2D.class, boxLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new BoxGraphic( (Box2D)modelElement ) ;
        }
    }

    private class ProvisionalBondGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected ProvisionalBondGraphicFactory() {
            super( ProvisionalBond.class, bondLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new ProvisionalBondGraphic( (ProvisionalBond)modelElement ) ;
        }
    }

    
}
