/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.FrameSequence;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.model.Force1DModel;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Dec 6, 2004
 * Time: 8:53:15 AM
 * Copyright (c) Dec 6, 2004 by Sam Reid
 */

public class LeanerGraphic extends PhetImageGraphic {
    private FrameSequence animation;
    private PhetGraphic target;
    private Force1DPanel forcePanel;
    private Force1DModule module;
    private double max = 500.0;
    private FrameSequence flippedAnimation;
    private BufferedImage standingStill;

    public LeanerGraphic( final Force1DPanel forcePanel, final PhetGraphic target ) throws IOException {
        super( forcePanel, (BufferedImage)null );
        this.forcePanel = forcePanel;
        this.target = target;
        this.module = forcePanel.getModule();
        standingStill = ImageLoader.loadBufferedImage( "images/standing-man.png" );
        animation = new FrameSequence( "images/pusher-leaning/pusher-leaning", 15 );
        BufferedImage[] flipped = new BufferedImage[animation.getNumFrames()];
        for( int i = 0; i < flipped.length; i++ ) {
            flipped[i] = BufferedImageUtils.flipX( animation.getFrame( i ) );
        }
        flippedAnimation = new FrameSequence( flipped );
        super.setImage( animation.getFrame( 0 ) );
        final long startTime = System.currentTimeMillis();
        target.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                long dt = System.currentTimeMillis() - startTime;
                if( module.getForceModel().getAppliedForce() != 0 ) {
                    update( false );
                }
                if( dt < 5000 ) {
                    update( true );
                }
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                setVisible( phetGraphic.isVisible() );
            }

        } );
        module.getForceModel().addListener( new Force1DModel.Listener() {
            public void appliedForceChanged() {
                update( false );
            }

            public void gravityChanged() {
            }

            public void wallForceChanged() {
            }
        } );
        setIgnoreMouse( true );
        update( true );

    }

    private BufferedImage getFrame( boolean facingRight ) {
        double appliedForce = Math.abs( module.getForceModel().getAppliedForce() );
        int index = (int)( animation.getNumFrames() * appliedForce / max );
        if( index >= animation.getNumFrames() ) {
            index = animation.getNumFrames() - 1;
        }
        if( module.getForceModel().getAppliedForce() == 0 ) {
            return standingStill;
        }
        if( facingRight ) {
            return animation.getFrame( index );
        }
        else {
            return flippedAnimation.getFrame( index );
        }
    }

    public void screenSizeChanged() {
        update( true );
    }

    private void update( boolean forceLocation ) {
        boolean facingRight = true;
        double app = module.getForceModel().getAppliedForce();
        if( app < 0 ) {
            facingRight = false;
        }
        BufferedImage frame = getFrame( facingRight );
//        if( frame != null ) {
//        BufferedImage frame = animation.getFrame( index );
        int x = 0;
        int y = 0;
        int STEP_CLOSER = 5;
        if( facingRight ) {
            x = (int)( target.getBounds().getX() - frame.getWidth() ) + STEP_CLOSER;
            y = forcePanel.getWalkwayGraphic().getFloorY() - getHeight();
        }
        else {
            x = (int)( target.getBounds().getX() + target.getWidth() ) - STEP_CLOSER;
            y = forcePanel.getWalkwayGraphic().getFloorY() - getHeight();
//            frame = flippedAnimation.getFrame( index );
        }
        if( app != 0 || forceLocation ) {
            setLocation( x, y );
        }
//        }
        setImage( frame );
    }
}
