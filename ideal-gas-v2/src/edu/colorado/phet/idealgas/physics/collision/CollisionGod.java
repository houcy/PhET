/**
 * Class: CollisionGod
 * Package: edu.colorado.phet.idealgas.physics.collision
 * Author: Another Guy
 * Date: Jan 20, 2004
 */
package edu.colorado.phet.idealgas.physics.collision;

//import edu.colorado.phet.controller.Config;
import edu.colorado.phet.idealgas.physics.GasMolecule;
import edu.colorado.phet.idealgas.physics.body.IdealGasParticle;
//import edu.colorado.phet.physics.Law;
//import edu.colorado.phet.physics.PhysicalSystem;
//import edu.colorado.phet.physics.body.Body;
//import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.collision.Collision;
import edu.colorado.phet.physics.collision.CollisionFactory;
import edu.colorado.phet.physics.collision.ContactDetector;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * This class takes care of detecting and computing all the collisions in
 * the system. To avoid combinatorial explosions in collision detection,
 * the area in which collisions are to be handled is divided up into regions,
 * and particle-particle collisions are only searched for within each
 * region and those adjacent to it.
 */
public class CollisionGod implements Law {
    private int numRegionsX;
    private int numRegionsY;
    private Region[][] regions;
    private HashMap elementToRegionMap = new HashMap();
    // List to track bodies that are to be removed from the system at the
    // end of the apply() method, to avoid concurrentModificationExceptions
    private ArrayList removalList = new ArrayList();
    private double regionWidth;
    private double regionHeight;
    public static boolean overlappingRegions = true;//false;
    private Rectangle2D.Double bounds;
    private double regionOverlap;
    //    private static boolean skipCollisions=false;

    public CollisionGod( Rectangle2D.Double bounds, int numRegionsX, int numRegionsY ) {
        this.bounds = bounds;
        this.numRegionsX = numRegionsX;
        this.numRegionsY = numRegionsY;
        regions = new Region[numRegionsX][numRegionsY];
        regionWidth = bounds.getWidth() / numRegionsX;
        regionHeight = bounds.getHeight() / numRegionsY;
        regionOverlap = 2 * IdealGasParticle.s_defaultRadius;

        for( int i = 0; i < numRegionsX; i++ ) {
            for( int j = 0; j < numRegionsY; j++ ) {
                if( overlappingRegions ) {
                    regions[i][j] = new Region( bounds.getX() + i * regionWidth, // - regionOverlap,
                                                bounds.getX() + ( ( i + 1 ) * regionWidth ) + regionOverlap,
                                                bounds.getY() + j * regionHeight, // - regionOverlap,
                                                bounds.getY() + ( ( j + 1 ) * regionHeight ) + regionOverlap );
                }
                else {
                    regions[i][j] = new Region( bounds.getX() + (double)i * regionWidth, ( bounds.getX() + (double)( i + 1 ) * regionWidth ) - -Double.MIN_VALUE,
                                                bounds.getY() + (double)j * regionHeight, ( bounds.getY() + (double)( j + 1 ) * regionHeight ) - Double.MIN_VALUE );
                }

                //                regions[i][j] = new Region( bounds.getX() + i * regionWidth, bounds.getX() + ( ( i + 1 ) * regionWidth ) - Double.MIN_VALUE,
                //                                            bounds.getY() + j * regionHeight, bounds.getY() + ( ( j + 1 ) * regionHeight ) - Double.MIN_VALUE );
            }
        }
    }

    public void apply( float time, PhysicalSystem system ) {
        //        if( skipCollisions ) {
        //            return;
        //        }
        //        else {
        List bodies = system.getBodies();
        adjustRegionMembership( bodies );
        doMiscCollisions( bodies );
        doGasToGasCollisions();
        for( int i = 0; i < regions.length; i++ ) {
            Region[] region = regions[i];
            for( int j = 0; j < region.length; j++ ) {
                Region region1 = region[j];
                region1.clear();
            }
            //            }
        }
    }

    /**
     * Makes sure all gas molecules are in the correct regions.
     *
     * @param bodies
     */
    private void adjustRegionMembership( List bodies ) {

        // Put all the gas molecules in the model in the right regions
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body instanceof GasMolecule ) {
                if( overlappingRegions ) {
                    findRegionsFor( body );
                }
                else {
                    if( elementToRegionMap.containsKey( body ) ) {
                        this.placeBody( body );
                    }
                    else {
                        this.addBody( body );
                    }
                }
            }
        }

        // Remove any gas molecules from our internal structures that
        // are no longer in the physical system
        removalList.clear();
        Set placedBodies = elementToRegionMap.keySet();
        for( Iterator iterator = placedBodies.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();
            if( o instanceof GasMolecule ) {
                if( !bodies.contains( o ) ) {
                    removalList.add( o );
                }
            }
        }
        while( !removalList.isEmpty() ) {
            Body body = (Body)removalList.remove( 0 );
            removeBody( body );
        }
    }

    /**
     * Detects and performs collisions in which at least one of
     * the bodies is not a gas molecule
     *
     * @param bodies
     */
    private void doMiscCollisions( List bodies ) {
        ArrayList nonGasBodies = new ArrayList();
        // Find all the bodies that aren't gas molecules
        for( int i = 0; i < bodies.size(); i++ ) {
            Object o = bodies.get( i );
            if( !( o instanceof GasMolecule ) ) {
                nonGasBodies.add( o );
            }
        }
        // Find all collisions between non-gas molecules and other
        // bodies
        for( int i = 0; i < nonGasBodies.size(); i++ ) {
            Body body1 = (Body)nonGasBodies.get( i );
            for( int j = 0; j < bodies.size(); j++ ) {
                Body body2 = (Body)bodies.get( j );
                if( body1 != body2 ) {
                    detectAndDoCollision( body1, body2 );
                }
            }
        }
    }

    private void doGasToGasCollisions() {

        // Do particle-particle collisions. Each region collides with
        // itself and the regions to the right and below.
        for( int i = 0; i < numRegionsX; i++ ) {
            for( int j = 0; j < numRegionsY; j++ ) {
                doRegionToRegionCollision( regions[i][j], regions[i][j] );
                if( !overlappingRegions ) {
                    if( i < numRegionsX - 1 ) {
                        doRegionToRegionCollision( regions[i][j], regions[i + 1][j] );
                    }
                    if( j < numRegionsY - 1 ) {
                        doRegionToRegionCollision( regions[i][j], regions[i][j + 1] );
                    }
                    if( i < numRegionsX - 1 && j < numRegionsY - 1 ) {
                        doRegionToRegionCollision( regions[i][j], regions[i + 1][j + 1] );
                    }
                }
            }

        }
    }

    private void doRegionToRegionCollision( Region region1, Region region2 ) {
        for( int i = 0; i < region1.size(); i++ ) {
            Body body1 = (Body)region1.get( i );
            int jStart = ( region1 == region2 ) ? i + 1 : 0;
            //            if( Config.jStartTest ) {
            //                jStart = i + 1;
            //            }
            for( int j = jStart; j < region2.size(); j++ ) {
                Body body2 = (Body)region2.get( j );
                if( body1 != body2 ) {
                    detectAndDoCollision( body1, body2 );
                }
            }
        }
    }

    private void detectAndDoCollision( Body body1, Body body2 ) {
        if( body1 != body2 && ContactDetector.areContacting( body1, body2 ) ) {
            Collision collision = CollisionFactory.create( body1, body2 );
            if( collision != null ) {
                collision.collide();
            }
        }
    }

    private void addBody( Body body ) {
        if( overlappingRegions ) {
            List regions = findRegionsFor( body );
        }
        else {
            Region region = findRegionFor( body );
            elementToRegionMap.put( body, region );
            if( region == null ) {
                System.out.println( "halt" );
            }
            region.add( body );
        }
    }

    private void removeBody( Body body ) {
        if( overlappingRegions ) {
            int iLimit = numRegionsX;
            int jLimit = numRegionsY;
            for( int i = 0; i < iLimit; i++ ) {
                for( int j = 0; j < jLimit; j++ ) {
                    if( regions[i][j].contains( body ) ) {

                        // If we have found a region in which the particle belongs, we only have to
                        // test for membership in the next region, and no farther
                        iLimit = Math.min( ( i + 2 ), numRegionsX );
                        jLimit = Math.min( ( j + 2 ), numRegionsY );
                        regions[i][j].remove( body );
                    }
                }
            }
        }
        else {

            ( (Region)elementToRegionMap.get( body ) ).remove( body );
            elementToRegionMap.remove( body );
        }
    }

    private List findRegionsForOrig( Body body ) {

        IdealGasParticle igp = (IdealGasParticle)body;
        int iLimit = numRegionsX;
        int jLimit = numRegionsY;
        for( int i = 0; i < iLimit; i++ ) {
            for( int j = 0; j < jLimit; j++ ) {
                if( regions[i][j].belongsIn( body ) ) {

                    // If we have found a region in which the particle belongs, we only have to
                    // test for membership in the next region, and no farther
                    //check for neighbors.

                    iLimit = Math.min( ( i + 2 ), numRegionsX );
                    jLimit = Math.min( ( j + 2 ), numRegionsY );
                    if( !regions[i][j].contains( body ) ) {
                        regions[i][j].add( igp );
                    }
                }
                else {
                    regions[i][j].remove( igp );
                }
            }
        }
        //        return igp.getRegions();
        return null;
    }

    private List findRegionsFor( Body body ) {
        double x = body.getPosition().getX();
        double y = body.getPosition().getY();

        int i = (int)( ( x - bounds.x ) / regionWidth );
        int iPrime = (int)( ( x - regionOverlap - bounds.x ) / ( regionWidth ) );
        int j = (int)( ( y - bounds.y ) / regionHeight );
        int jPrime = (int)( ( y - regionOverlap - bounds.y ) / ( regionHeight ) );
        regions[i][j].add( body );
        if( i != iPrime ) {
            regions[iPrime][j].add(body );
        }
        if( j != jPrime ) {
            regions[i][jPrime].add( body );
        }
        if( i != iPrime && j != jPrime ) {
            regions[iPrime][jPrime].add( body );
        }
//        System.out.println( "i=" + i + ", j=" + j );
        //        Region a = regions[i][j];
        //        Region b = regions[i][jPrime];
        //        Region c = regions[iPrime][jPrime];
        //        Region d = regions[iPrime][j];
        //        Set set = new HashSet();
        //        set.add( a );
        //        set.add( b );
        //        set.add( c );
        //        set.add( d );
        //        List list = Arrays.asList( set.toArray() );
        //        if( !a.contains( body ) ) {
        //            a.add( body );
        //        }
        //        if( !b.contains( body ) ) {
        //            b.add( body );
        //        }
        //        if( !c.contains( body ) ) {
        //            c.add( body );
        //        }
        //        if( !d.contains( body ) ) {
        //            d.add( body );
        //        }
        //        return list;
        return null;
    }

    private Region findRegionFor( Body body ) {
        Region region = null;
        if( Config.regionTest ) {
            int i = (int)( (double)body.getPosition().getX() / regionWidth );
            int j = (int)( (double)body.getPosition().getY() / regionHeight );
            region = regions[i][j];
        }
        else {
            for( int i = 0; region == null && i < numRegionsX; i++ ) {
                for( int j = 0; region == null && j < numRegionsY; j++ ) {
                    if( regions[i][j].belongsIn( body ) ) {
                        region = regions[i][j];
                    }
                }

            }

        }
        return region;
    }

    private void placeBody( Body body ) {
        Region currRegion = (Region)elementToRegionMap.get( body );
        if( currRegion == null ) {
            addBody( body );
        }
        else if( !currRegion.belongsIn( body ) ) {
            currRegion.remove( body );
            addBody( body );
        }
    }

//    private void compute( Particle particle ) {
//
//        double x = particle.getPosition().getX();
//        double y = particle.getPosition().getY();
//
//        int i = (int)( ( x - bounds.x ) / regionWidth );
//        int iPrime = (int)( ( x - bounds.x ) / ( regionWidth + regionOverlap ) );
//        int j = (int)( ( y - bounds.y ) / regionHeight );
//        int jPrime = (int)( ( y - bounds.y ) / ( regionHeight + regionOverlap ) );
//
//        //
//        //
//        //        if( overlappingRegions ) {
//        //            regions[i][j] = new Region( bounds.getX() + i * regionWidth, // - regionOverlap,
//        //                                        bounds.getX() + ( ( i + 1 ) * regionWidth ) + regionOverlap,
//        //                                        bounds.getY() + j * regionHeight, // - regionOverlap,
//        //                                        bounds.getY() + ( ( j + 1 ) * regionHeight ) + regionOverlap );
//        //        }
//        //        else {
//        //
//        //            regions[i][j] = new Region( bounds.getX() + (double)i * regionWidth, ( bounds.getX() + (double)( i + 1 ) * regionWidth ) - -Double.MIN_VALUE,
//        //                                        bounds.getY() + (double)j * regionHeight, ( bounds.getY() + (double)( j + 1 ) * regionHeight ) - Double.MIN_VALUE );
//        //        }
//    }

    /**
     * A region within the physical system
     */
    // Try making it a linked list instead of a HashSet. That way, we can avoid using
    // iterators when going through them
    public class Region extends LinkedList {
        double xMin;
        double xMax;
        double yMin;
        double yMax;

        Region( double xMin, double xMax, double yMin, double yMax ) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        boolean belongsIn( Body body ) {
            boolean result = body.getPosition().getX() >= xMin
                             && body.getPosition().getX() <= xMax
                             && body.getPosition().getY() >= yMin
                             && body.getPosition().getY() <= yMax;
            return result;
        }
    }
}
