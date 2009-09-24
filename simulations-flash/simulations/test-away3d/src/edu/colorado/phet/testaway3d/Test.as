﻿package edu.colorado.phet.testaway3d {
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.MovieClip;
	import flash.display.Sprite;
    import flash.events.Event;
	import flash.events.MouseEvent;
	
	import away3d.cameras.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.filter.*;
	import away3d.core.geom.*;
	import away3d.core.light.*;
	import away3d.core.math.*;
	import away3d.core.render.*;
	import away3d.events.*;
	import away3d.lights.*;
	import away3d.materials.*;
	import away3d.primitives.*;

	public class Test extends MovieClip {
		
		//engine variables
		var scene:Scene3D;
		var camera:HoverCamera3D;
		var fogfilter:FogFilter;
		var renderer:IRenderer;
		var view:View3D;
		
		//scene objects
		var cube:Cube;
		
		//navigation variables
		var move:Boolean = false;
		var startMouseX:Number;
		var startMouseY:Number;
		var startMiddle : Number3D;
		
		var poolWidth : Number = 1000;
		var poolHeight : Number = 500;
		var poolDepth : Number = 500;
		var waterHeight: Number = 420;
		var far : Number = 5000;
		
		var poolTop : Plane;
		var poolFront : Plane;
		
		public function Test() {
			init();
		}
		
		public function init():void {
			initEngine();
			initObjects();
			initListeners();
		}
		
		public function initEngine():void {
			scene = new Scene3D();
			
			//camera = new HoverCamera3D({focus:50, distance:1000, mintiltangle:0, maxtiltangle:90});
			camera = new HoverCamera3D();
			camera.focus = 50;
			camera.distance = 1000;
			camera.mintiltangle = 0;
			camera.maxtiltangle = 90;
			
			camera.targetpanangle = camera.panangle = 180;
			camera.targettiltangle = camera.tiltangle = 10;
			
			//fogfilter = new FogFilter({material:new ColorMaterial(0x000000), minZ:500, maxZ:2000});
			fogfilter = new FogFilter();
			fogfilter.material = new ColorMaterial(0x000000);
			fogfilter.minZ = 500;
			fogfilter.maxZ = 20000;
			
			//renderer = new BasicRenderer( fogfilter );
			//renderer = new BasicRenderer();
			renderer = Renderer.INTERSECTING_OBJECTS;
			
			//view = new View3D({scene:scene, camera:camera, renderer:renderer});
			view = new View3D();
			view.scene = scene;
			view.camera = camera;
			view.renderer = renderer;
			
			view.addSourceURL("srcview/index.html");
			addChild(view);
		}
		
		public function updateWater() : void {
			poolFront.y = -poolHeight + waterHeight / 2;
			poolFront.height = waterHeight;
			poolTop.y = -poolHeight + waterHeight;
		}
		
		public function initObjects():void {
			
			poolFront = new Plane({ y: -poolHeight + waterHeight / 2, width: poolWidth, height: waterHeight, rotationX: 90, material: new ShadingColorMaterial( 0x0088FF, {alpha: 0.4} ) });			
			scene.addChild( poolFront );
			poolFront.mouseEnabled = false;
			poolTop = new Plane({ y: -poolHeight + waterHeight, z: poolDepth / 2, width: poolWidth, height: poolDepth, material: new ShadingColorMaterial( 0x0088FF, {alpha: 0.4} ) });
			scene.addChild( poolTop );
			poolTop.mouseEnabled = false;
			
			// back of pool
			scene.addChild( new Plane({ y: -poolHeight / 2, z:poolDepth, width: poolWidth, height: poolHeight, rotationX: 90, material: new ShadingColorMaterial( 0xAAAAAA ) }) );			
			
			// bottom of pool
			scene.addChild( new Plane({ y: -poolHeight, z:poolDepth / 2, width: poolWidth, height: poolDepth, material: new ShadingColorMaterial( 0xAAAAAA ) }) );			
			
			// sides of pool
			scene.addChild( new Plane({ x: poolWidth / 2, y: -poolHeight / 2, z: poolDepth / 2, width: poolHeight, height: poolDepth, rotationZ: 90, material: new ShadingColorMaterial( 0xAAAAAA ) }) );			
			scene.addChild( new Plane({ x: -poolWidth / 2, y: -poolHeight / 2, z: poolDepth / 2, width: poolHeight, height: poolDepth, rotationZ: -90, material: new ShadingColorMaterial( 0xAAAAAA ) }) );			
			
			// ground behind pool
			scene.addChild( new Plane({ z: ( (far - poolDepth) / 2 ) + poolDepth, width: poolWidth, height: far - poolDepth, material: new ShadingColorMaterial( 0x00AA00 ) }) );
			
			// ground to the sides of the pool
			scene.addChild( new Plane({ x: far / 2 + poolWidth / 2, z: far / 2, width: far, height: far, material: new ShadingColorMaterial( 0x00AA00 ) }) );
			scene.addChild( new Plane({ x: -far / 2 - poolWidth / 2, z: far / 2, width: far, height: far, material: new ShadingColorMaterial( 0x00AA00 ) }) );
			
			// front of earth beneath the pool
			scene.addChild( new Plane({ y: -far / 2 - poolHeight, width: poolWidth, height: far, rotationX: 90, material: new ShadingColorMaterial( 0xAA7733 ) }) );
			
			// front of earth to the sides
			scene.addChild( new Plane({ x: far / 2 + poolWidth / 2, y: -far / 2, width: far, height: far, rotationX: 90, material: new ShadingColorMaterial( 0xAA7733 ) }) );
			scene.addChild( new Plane({ x: -far / 2 - poolWidth / 2, y: -far / 2, width: far, height: far, rotationX: 90, material: new ShadingColorMaterial( 0xAA7733 ) }) );
			
			cube = new Cube({ x: 300, y: 160, z: 101, width: 100, height: 100, depth: 100, segmentsW: 10, segmentsH: 10, material: new ShadingColorMaterial( 0xFF0000, {ambient: 0xFF0000, specular:0xFFFFFF}) });
			scene.addChild(cube);
			
			var light:DirectionalLight3D = new DirectionalLight3D({color:0xFFFFFF, ambient:0.2, diffuse:0.75, specular:0.1});
			light.x = 10000;
			light.z = -35000;
			light.y = 50000;
			scene.addChild(light);
			
		}
		
		public function initListeners():void {
			scene.addEventListener(MouseEvent3D.MOUSE_UP, onSceneMouseUp);
			
			addEventListener(Event.ENTER_FRAME, onEnterFrame);
			stage.addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			stage.addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
			stage.addEventListener(Event.RESIZE, onResize);
			stage.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
			onResize();
		}
		
		public function medianPoint( m : Mesh ) : Number3D {
			var num : Number = 0;
			var kx : Number = 0;
			var ky : Number = 0;
			var kz : Number = 0;
			for each( var v : Vertex in m.vertices ) {
				num += 1.0;
				kx += v.position.x;
				ky += v.position.y;
				kz += v.position.z;
			}
			return new Number3D( kx / num, ky / num, kz / num );
		}
		
		public function medianScreenPoint( m : Mesh ) : Number3D {
			var num : Number = 0;
			var kx : Number = 0;
			var ky : Number = 0;
			var kz : Number = 0;
			for each( var v : Vertex in m.vertices ) {
				num += 1.0;
				var sv : ScreenVertex = camera.screen( m, v );
				kx += sv.x + view.x;
				ky += sv.y + view.y;
				kz += sv.z;
			}
			return new Number3D( kx / num, ky / num, kz / num );
		}
		
		public function onSceneMouseUp( e: MouseEvent3D ) : void {
			//if (e.object is Mesh) {
				//var mesh:Mesh = e.object as Mesh;
				//mesh.material = new WireColorMaterial();
			//}
		}
		
		public function onEnterFrame(event:Event):void {			
			camera.hover();  
			view.render();
		}
		
		public function onMouseDown( event:MouseEvent ) : void {
			startMouseX = stage.mouseX;
			startMouseY = stage.mouseY;
			if( view.mouseObject == cube ) {
				move = true;
				startMiddle = medianScreenPoint( cube );
			}
			stage.addEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);
		}
		
		public function onMouseMove( event : MouseEvent ) : void {
			if( move ) {
				var curCenter : Number3D = cube.position;
				var projected : Number3D = camera.unproject( stage.mouseX - view.x, stage.mouseY - view.y );
				projected.add( projected, new Number3D( camera.x, camera.y, camera.z ) );
				var cameraVertex : Vertex = new Vertex( camera.x, camera.y, camera.z );
				var rayVertex : Vertex = new Vertex( projected.x, projected.y, projected.z );
				var cubePlane : Plane3D = new Plane3D();
				cubePlane.fromNormalAndPoint( new Number3D( 0, 0, 1 ), new Number3D( 0, 0, 0 ) );
				trace( cubePlane.a );
				trace( cubePlane.b );
				trace( cubePlane.c );
				trace( cubePlane.d );
				var intersection : Vertex = cubePlane.getIntersectionLine( cameraVertex, rayVertex );
				trace( "-" );
				trace( "box: " + String( curCenter ) );
				trace( cameraVertex );
				trace( rayVertex );
				trace( "intersect: " + String( intersection ) );
				cube.x = intersection.x;
				cube.y = intersection.y;
			}
		}
		
		public function onMouseUp( event:MouseEvent ) : void {
			move = false;
			stage.removeEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);     
		}
		
		public function onStageMouseLeave( event:Event ) : void {
			move = false;
			stage.removeEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);     
		}
		
		public function onResize( event:Event = null ) : void {
			view.x = stage.stageWidth / 2;
			view.y = stage.stageHeight / 2;
		}

	}
}