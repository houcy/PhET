/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.idealgas.physics.IdealGasSystem;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;


/**
 * A 2 dimensional box
 */
public class Box2D extends CollidableBody {

    private Point2D corner1;
    private Point2D  corner2;
    private Point2D  center;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private Wall leftWall;
    private Object leftWallMonitor = new Object();
    private double leftWallVx = 0;

    // TODO: put the opening characteristics in a specialization of this class.
    private Vector2D[] opening = new Vector2D[]{
        new Vector2D.Float( 0, 0 ),
        new Vector2D.Float( 0, 0 )};

    private Wall[] walls = new Wall[4];
    private IdealGasSystem model;

    public Box2D( IdealGasSystem model ) {
        super();
        this.model = model;
    }

    public Box2D( Point2D corner1, Point2D corner2, IdealGasSystem model ) {
//    public Box2D( Vector2D corner1, Vector2D corner2 ) {
        super();
        this.model = model;
        this.setState( corner1, corner2 );
    }

    public Point2D.Double getCM() {
        throw new RuntimeException( "undefined" );
    }

    public double getMomentOfInertia() {
        throw new RuntimeException( "undefined" );
    }

    public void setBounds( double minX, double minY, double maxX, double maxY ) {
//    public void setBounds( float minX, float minY, float maxX, float maxY ) {
        this.setState( new Point2D.Double( minX, minY ), new Point2D.Double( maxX, maxY ) );
//        this.setState( new Vector2D.Double( minX, minY ), new Vector2D.Double( maxX, maxY ) );
    }

    private void setState( Point2D corner1, Point2D corner2 ) {
//    private void setState( Vector2D corner1, Vector2D corner2 ) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        maxX = Math.max( corner1.getX(), corner2.getX() );
        maxY = Math.max( corner1.getY(), corner2.getY() );
        minX = Math.max( Math.min( Math.min( corner1.getX(), corner2.getX() ), maxX - 20 ), 40 );
        minY = Math.min( corner1.getY(), corner2.getY() );
        center = new Point2D.Double( ( this.maxX + this.minX ) / 2,
                               ( this.maxY + this.minY ) / 2 );
//        center = new Vector2D.Double( ( this.maxX + this.minX ) / 2,
//                               ( this.maxY + this.minY ) / 2 );

        setPosition( new Point2D.Double( minX, minY ) );
//        setPosition( new Vector2D.Double( minX, minY ) );

        // Update the position of the door
        Vector2D[] opening = this.getOpening();
        opening[0].setY( minY );
        opening[1].setY( minY );
        this.setOpening( opening );

        // Left wall - Note: The location of the existing wall doesn't get updated
        // properly, so we have to make a new one
        walls[0] = new VerticalWall( minX, minX, minY, maxY, VerticalWall.FACING_RIGHT );
        leftWall = walls[0];

        // Right wall
        walls[1] = new VerticalWall( maxX, maxX, minY, maxY, VerticalWall.FACING_LEFT );

        // Top wall
        walls[2] = new HorizontalWall( minX, maxX, minY, minY, HorizontalWall.FACING_DOWN );

        // Bottom wall
        walls[3] = new HorizontalWall( minX, maxX, maxY, maxY, HorizontalWall.FACING_UP );

//        this.setChanged();
        this.notifyObservers();
    }

    /**
     * @param physicalSystem
     */
//    public void setPhysicalSystem( PhysicalSystem physicalSystem ) {
//        super.setPhysicalSystem( physicalSystem );
//        for( int i = 0; i < walls.length; i++ ) {
//            walls[i].setPhysicalSystem( physicalSystem );
//        }
//    }

    /**
     *
     */
    public void setOpening( Vector2D[] opening ) {
        this.opening[0] = opening[0];
        this.opening[1] = opening[1];
//        setChanged();
        notifyObservers();
    }

    /**
     *
     */
    public Vector2D[] getOpening() {
        return this.opening;
    }

    protected Wall getLeftWall() {
        return leftWall;
    }

    public boolean isFloor( Wall wall ) {
        return wall == this.walls[3];
    }

    /**
     * @param body
     * @return
     */
    public boolean isInOpening( CollidableBody body ) {

        boolean result = false;
        if( body instanceof SphericalBody ) {
            SphericalBody particle = (SphericalBody)body;
            if( particle.getPosition().getX() >= this.opening[0].getX()
                && particle.getPosition().getX() <= this.opening[1].getX()
                && particle.getPosition().getY() - particle.getRadius() <= this.getMinY() ) {
                result = true;
            }
            else {
                result = false;
            }
        }
        return result;
    }

    /**
     *
     */
    public void stepInTime( float dt ) {
        super.stepInTime( dt );
        synchronized( leftWallMonitor ) {
            leftWall.setVelocity( leftWallVx, 0 );
//            leftWall.setVelocityX( leftWallVx );
        }
    }

    int cntA = 0;

    /**
     * @param particle
     */
    public Wall collideWithParticle( SphericalBody particle ) {

        // Since we can collide with more than one wall in a time step, and we try to handle that in this method, we
        // also have to make sure that we don't thing we've hit a second wall, when we actually only hit one, but the
        // timing of the collision was such that it happened exactly at the end of the time step. In such a case, the
        // particle will still be in contact with the wall at the end of the time step, and we do not want to treat
        // this as another collision. The following variable is used to handle this.
        Wall collidingWall = null;
        Wall previousCollidingWall = null;

        if( !isInOpening( particle ) ) {

            boolean hasCollision = false;
            int cnt = 0;
            do {
                hasCollision = false;

                // See if the particle is hitting any of the walls of the box. If it hits more than one,
                // determine which it hit first
                for( int i = 0; i < walls.length; i++ ) {
                    Wall wall = walls[i];
                    if( detector.areInContact( particle, wall ) ) {
                        collidingWall = wall;
                        break;
                    }
                }
                if( collidingWall != null && collidingWall != previousCollidingWall ) {
                    previousCollidingWall = collidingWall;
                    hasCollision = true;
                    cnt++;
                    CollisionFactory.create( collidingWall, particle ).collide();

                    // Handle giving particle kinetic energy if the wall is moving
                    if( collidingWall == leftWall ) {
                        double vx0 = particle.getVelocity().getX();
                        double vx1 = vx0 + leftWallVx;
                        particle.setVelocity( vx1, particle.getVelocity().getY() );
//                        particle.setVelocityX( vx1 );

                        // Add the energy to the system, so it doesn't get
                        // taken back out when energy conservation is performed
                        model.addKineticEnergyToSystem( leftWallVx );
//                        ( (IdealGasSystem)this.getPhysicalSystem() ).addKineticEnergyToSystem( leftWallVx );
                    }
                }
            } while( hasCollision && cnt < 2 );
        } // if( !isInOpening( particle ) )
        return collidingWall;
    }

    /**
     *
     */
    SphereWallContactDetector detector = new SphereWallContactDetector();

    public boolean isInContactWithParticle( SphericalBody particle ) {

        if( isInOpening( particle ) ) {
            return false;
        }
        // To try to catch escaped particles
        if( this.containsBody( particle ) && this.isOutsideBox( particle ) ) {
            return true;
        }

        for( int i = 0; i < walls.length; i++ ) {
            Wall wall = walls[i];
            if( detector.areInContact( wall, particle ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    private Vector2D closestCornerResult = new Vector2D.Double();

    public Vector2D getClosestCorner( Vector2D point ) {

        double x = Math.abs( point.getX() - minX ) < Math.abs( point.getX() - maxX )
                  ? minX : maxX;

        double y = Math.abs( point.getY() - minY ) < Math.abs( point.getY() - maxY )
                  ? minY : maxY;
        closestCornerResult.setX( x );
        closestCornerResult.setY( y );
        return closestCornerResult;
    }

    /**
     *
     */
    public boolean isOutsideBox( SphericalBody particle ) {
        Point2D p = particle.getPosition();
//        Vector2D p = particle.getPosition();
        double rad = particle.getRadius();
        boolean isInBox = p.getX() - rad >= this.getMinX()
                          && p.getX() + rad <= this.getMaxX()
                          && p.getY() - rad >= this.getMinY()
                          && p.getY() + rad <= this.getMaxY();
        return !isInBox;
    }

    /**
     * @param vx
     */
    public void setLeftWallVelocity( float vx ) {
        synchronized( leftWallMonitor ) {
            leftWallVx = vx;
        }
    }

    // TODO: change references so these methods don't have to be public.
    public double getCorner1X() {
        return corner1.getX();
    }

    public double getCorner1Y() {
        return corner1.getY();
    }

    public double getCorner2X() {
        return corner2.getX();
    }

    public double getCorner2Y() {
        return corner2.getY();
    }

    public Point2D getCenter() {
//    public Vector2D getCenter() {
        return center;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public Wall[] getWalls() {
        return walls;
    }

    public double getContactOffset( Body body ) {
        return 0;
    }

    public float getContactOffset( CollidableBody body ) {
        return 0;
    }
}
