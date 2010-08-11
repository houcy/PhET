package edu.colorado.phet.densityflex.model {
import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Dynamics.b2BodyDef;

public class Cuboid extends DensityObject {

    protected var width:Number;
    protected var height:Number;
    protected var depth:Number;
    private var shapeDef:b2PolygonDef = new b2PolygonDef();
    private var bodyDef:b2BodyDef = new b2BodyDef();
    private var shapeChangeListeners:Array = new Array();

    /**
     * Arbitrary cuboid
     * @param density
     * @param width
     * @param height
     * @param depth
     * @param x
     * @param y
     * @param model
     */
    public function Cuboid(density:Number, width:Number, height:Number, depth:Number, x:Number, y:Number, model:DensityModel) {
        super(x, y, depth / 2 + 1.01, model,density,density*width*height*depth,width*height*depth);

        this.width = width;
        this.height = height;
        this.depth = depth;

        updateBox2DModel();
        
        function volumeChangeListener():void{
            updateDimensions();
        }
        getVolumeProperty().addListener(volumeChangeListener);
        
        setVolume(width*height*depth);
    }

    public override function updateBox2DModel():void {
        updateBodyDef();
        updateShapeDef();
    }

    private function updateBodyDef():void {
        bodyDef.position.Set(getX(), getY());
        bodyDef.fixedRotation = true;
        bodyDef.massData.mass = getMass();
        bodyDef.massData.center.SetZero();
        bodyDef.massData.I = 1.0; // rotational inertia shouldn't matter
    }

    public function setSize(width:Number, height:Number):void {
        this.width = width;
        this.height = height;
        updateShapeDef();
    }

    public function addShapeChangeListener(shapeChangeListener:ShapeChangeListener):void {
        shapeChangeListeners.push(shapeChangeListener);
    }

    private function notifyShapeChanged():void {
        for each (var shapeChangeListener:ShapeChangeListener in shapeChangeListeners) {
            shapeChangeListener.shapeChanged();
        }
    }

    private function updateShapeDef():void {
        shapeDef.friction = 0.3;
        shapeDef.restitution = 0;
        shapeDef.density = getDensity();
        setBody(getModel().getWorld().CreateBody(bodyDef));
        shapeDef.SetAsBox(width / 2, height / 2);
        getBody().CreateShape(shapeDef);
        getBody().SetUserData(this);
        notifyShapeChanged();
        trace("density = " + shapeDef.density);
    }

    public function getWidth():Number {
        return width;
    }

    public function getHeight():Number {
        return height;
    }

    public function getDepth():Number {
        return depth;
    }

    public function getTopY():Number {
        return getY() + height / 2;
    }

    public function getBottomY():Number {
        return getY() - height / 2;
    }

    public function updateDimensions():void {
        this.height=Math.pow(getVolume(),1.0/3.0);
        this.width=Math.pow(getVolume(),1.0/3.0);
        this.depth=Math.pow(getVolume(),1.0/3.0);
        updateShapeDef();
    }
}
}