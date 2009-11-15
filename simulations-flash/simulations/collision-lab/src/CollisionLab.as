﻿//CollisionLab M.Dubson Nov 5, 2009
//source code resides in /collision-lab
//main class instance
package{
	import flash.display.*;  
	
	public class CollisionLab extends Sprite{  //should the main class extend MovieClip or Sprite?
		var myModel:Model;
		var myMainView:MainView;
		var stageW:Number;
		var stageH:Number;
		
		public function CollisionLab(){
			myModel = new Model();
			this.stageW = this.stage.stageWidth;
			this.stageH = this.stage.stageHeight;
			myMainView = new MainView(myModel, this.stageW, this.stageH);
			this.addChild(myMainView);
			this.myMainView.initialize();
		}//end of constructor
		
	}//end of class
}//end of package