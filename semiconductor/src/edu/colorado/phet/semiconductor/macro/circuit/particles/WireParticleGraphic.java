package edu.colorado.phet.semiconductor.macro.circuit.particles;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 12:55:13 AM
 * Copyright (c) Jan 16, 2004 by Sam Reid
 */
public class WireParticleGraphic extends TransformGraphic {
    WireParticle particle;
    SimpleBufferedImageGraphic imageGraphic;

    public WireParticleGraphic( WireParticle particle, ModelViewTransform2D transform, BufferedImage image ) {
        super( transform );
        this.particle = particle;
        this.imageGraphic = new SimpleBufferedImageGraphic( image );
        particle.addObserver( new SimpleObserver() {
            public void update() {
                WireParticleGraphic.this.update();
            }
        } );
    }

    public void update() {
//        PhetVector modelLoc=particle.getPosition();
//        Point pt=getTransform().modelToView(modelLoc);
//        particle.
    }

    public void paint( Graphics2D graphics2D ) {
        PhetVector modelLoc = particle.getPosition();
        if( modelLoc != null ) {
            Point pt = getTransform().modelToView( modelLoc );
            imageGraphic.setPosition( pt );
            imageGraphic.paint( graphics2D );
        }
    }
}
