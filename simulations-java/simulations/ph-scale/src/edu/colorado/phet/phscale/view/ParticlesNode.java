/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ParticlesNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double ACID_PH_THRESHOLD = 6;
    private static final double BASE_PH_THRESHOLD = 8;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final PBounds _containerBounds;
    private final PNode _particlesParent;
    private final Random _randomX, _randomY;
   
    private Double _pH; // used to watch for pH change in the liquid
    private int _maxParticles;
    private double _diameter;
    private int _transparency;
    private Color _h3oColor, _ohColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
   
    public ParticlesNode( Liquid liquid, PBounds containerBounds ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _containerBounds = new PBounds( containerBounds );
        _randomX = new Random();
        _randomY = new Random();
        _pH = _liquid.getPH();
        
        _particlesParent = new PNode();
        addChild( _particlesParent );
        
        _maxParticles = 5000;
        _diameter = 4;
        _transparency = 128;
        _h3oColor = ColorUtils.createColor( PHScaleConstants.H3O_COLOR, _transparency );
        _ohColor = ColorUtils.createColor( PHScaleConstants.OH_COLOR, _transparency );
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setMaxParticles( int maxParticles ) {
        if ( maxParticles != _maxParticles ) {
            _maxParticles = maxParticles;
            createParticles();
        }
    }
    
    public int getMaxParticles() {
        return _maxParticles;
    }
    
    public void setParticleDiameter( double diameter ) {
        if ( diameter != _diameter ) {
            _diameter = diameter;
            int count = _particlesParent.getChildrenCount();
            for ( int i = 0; i < count; i++ ) {
                ( (ParticleNode) _particlesParent.getChild( i ) ).setDiameter( diameter );
            }
        }
    }
    
    public double getParticleDiameter() {
        return _diameter;
    }
    
    public void setParticleTransparency( int transparency ) {
        if ( transparency != _transparency ) {
            _transparency = transparency;
            setH3OColor( _h3oColor );
            setOHColor( _ohColor );
        }
    }
    
    public int getParticleTransparency() { 
        return _transparency;
    }
    
    public void setH3OColor( Color color ) {
        _h3oColor = ColorUtils.createColor( color, _transparency );
        int count = _particlesParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = _particlesParent.getChild( i );
            if ( node instanceof H3ONode ) {
                node.setPaint( _h3oColor );
            }
        }
    }
    
    public Color getH3OColor() {
        return _h3oColor;
    }
    
    public void setOHColor( Color color ) {
        _ohColor = ColorUtils.createColor( color, _transparency );
        int count = _particlesParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = _particlesParent.getChild( i );
            if ( node instanceof OHNode ) {
                node.setPaint( _ohColor );
            }
        }
    }
    
    public Color getOHColor() {
        return _ohColor;
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    public void setVisible( boolean visible ) {
        if ( visible != getVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
            else {
                _pH = null;
                _particlesParent.removeAllChildren();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        if ( getVisible() ) {
            Double previousPH = _pH;
            _pH = _liquid.getPH();
            if ( _pH == null ) {
                deleteAllParticles();
            }
            else if ( !_pH.equals( previousPH ) ) {
                createParticles();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Particle creation
    //----------------------------------------------------------------------------
    
    /*
     * Deletes all particles.
     */
    private void deleteAllParticles() {
        _particlesParent.removeAllChildren();
    }
    
    /*
     * Creates particle nodes based on the current pH value.
     */
    private void createParticles() {
        
        deleteAllParticles();

        // calculate the ratio of H30 to OH
        final double ratio = ratio_H30_to_OH( _pH.doubleValue() );

        // calculate the number of H30 and OH particles
        final double multiplier = _maxParticles / ratio_H30_to_OH( 0 );
        int numH30, numOH;
        if ( ratio == 1 ) {
            numH30 = numOH = (int) Math.max( 1, multiplier );
        }
        else if ( ratio > 1 ) {
            numH30 = (int) ( multiplier * ratio );
            numOH = (int) Math.max( 1, multiplier );
        }
        else {
            numH30 = (int) Math.max( 1, multiplier );
            numOH = (int) ( multiplier / ratio );
        }

        // create particles, minority species in foreground
        if ( numH30 > numOH ) {
            createH3ONodes( numH30 );
            createOHNodes( numOH );
        }
        else {
            createOHNodes( numOH );
            createH3ONodes( numH30 );
        }
    }
    
    /* 
     * Computes the ratio of H3O to OH.
     * Between pH of 6 and 8, we use the actual log scale.
     * Below 6 and above 8, use a linear scale for "Hollywood" visualization.
     */
    private static double ratio_H30_to_OH( double pH ) {
        double ratio;
        if ( pH >= ACID_PH_THRESHOLD && pH <= BASE_PH_THRESHOLD ) {
            ratio = Liquid.getConcentrationH3O( pH ) / Liquid.getConcentrationOH( pH );
        }
        else if ( pH < ACID_PH_THRESHOLD ) {
            // strong acid
            double multiplier = ACID_PH_THRESHOLD - pH + 1;
            ratio = multiplier * Liquid.getConcentrationH3O( ACID_PH_THRESHOLD ) / Liquid.getConcentrationOH( ACID_PH_THRESHOLD );
        }
        else {
            // strong base
            double multiplier = 1 / ( pH - BASE_PH_THRESHOLD + 1 );
            ratio = multiplier * Liquid.getConcentrationH3O( BASE_PH_THRESHOLD ) / Liquid.getConcentrationOH( BASE_PH_THRESHOLD );
        }
        return ratio;
    }
    
    /*
     * Creates H3O nodes at random locations.
     */
    private void createH3ONodes( int count ) {
        Point2D pOffset = new Point2D.Double();
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( _containerBounds, pOffset );
            ParticleNode p = new H3ONode( _diameter, _h3oColor );
            p.setOffset( pOffset );
            _particlesParent.addChild( p );
        }
    }

    /*
     * Creates OH nodes at random locations.
     */
    private void createOHNodes( int count ) {
        Point2D pOffset = new Point2D.Double();
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( _containerBounds, pOffset );
            ParticleNode p = new OHNode( _diameter, _ohColor );
            p.setOffset( pOffset );
            _particlesParent.addChild( p );
        }
    }

    /*
     * Gets a random point inside some bounds.
     */
    private void getRandomPoint( PBounds bounds, Point2D pOutput ) {
        double x = bounds.getX() + ( _randomX.nextDouble() * bounds.getWidth() );
        double y = bounds.getY() + ( _randomY.nextDouble() * bounds.getHeight() );
        pOutput.setLocation( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /* Base class for all particle nodes */
    private static abstract class ParticleNode extends PPath {

        private Ellipse2D _ellipse;

        public ParticleNode( double diameter, Color color ) {
            super();
            _ellipse = new Ellipse2D.Double();
            setPaint( color );
            setStroke( null );
            setDiameter( diameter );
        }

        public void setDiameter( double diameter ) {
            _ellipse.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
            setPathTo( _ellipse );
        }
    }

    /* H30 particle node, marker class */
    private static class H3ONode extends ParticleNode {
        public H3ONode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

    /* OH particle node, marker class */
    private static class OHNode extends ParticleNode {
        public OHNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

}
