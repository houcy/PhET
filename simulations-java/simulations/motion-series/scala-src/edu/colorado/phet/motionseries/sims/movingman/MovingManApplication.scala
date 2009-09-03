package edu.colorado.phet.motionseries.sims.movingman

import java.awt.geom.Rectangle2D
import phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import phet.common.piccolophet.PiccoloPhetApplication
import controls.RampControlPanel
import model.{MotionSeriesModel, AdjustableCoordinateModel, FreeBodyDiagramModel, VectorViewModel}
import graphics._
import java.awt.Color
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}
import javax.swing.JFrame
import scalacommon.ScalaClock
import motionseries.MotionSeriesResources._
import theramp.AbstractRampModule

class BasicMovingManModule(frame: JFrame,
                           clock: ScalaClock,
                           name: String,
                           coordinateSystemFeaturesEnabled: Boolean,
                           showObjectSelectionNode: Boolean,
                           useObjectComboBox: Boolean, //todo: unused
                           showAppliedForceSlider: Boolean,
                           defaultBeadPosition: Double,
                           pausedOnReset: Boolean,
                           initialAngle: Double,
                           showFrictionControl: Boolean,
                           rampLayoutArea: Rectangle2D)
        extends AbstractRampModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val canvas = new BasicMovingManCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, rampLayoutArea)
  setSimulationPanel(canvas)
  val controlPanel = new RampControlPanel(rampModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, false, rampModel, false, showFrictionControl)
  setControlPanel(controlPanel)
  setClockControlPanel(new RecordModelControlPanel(rampModel, canvas, () => new PlaybackSpeedSlider(rampModel), Color.blue, 20))
  rampModel.selectedObject = MotionSeriesDefaults.movingMan
  vectorViewModel.xyComponentsVisible = false
  vectorViewModel.originalVectors = false
  vectorViewModel.parallelComponents = false
}

class BasicMovingManCanvas(model: MotionSeriesModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                           vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                           rampAngleDraggable: Boolean, rampLayoutArea: Rectangle2D)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, rampLayoutArea) {
  override def addHeightAndAngleIndicators() = {}
}

class IntroModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.intro.title".translate, false, true, false, true, -6, false, 0.0, true, MotionSeriesDefaults.defaultViewport)

class GraphingModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.graphing.title".translate, false, false, true, false, -6, false, 0.0, true, MotionSeriesDefaults.forceMotionViewport) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new MovingManChartNode(canvas, rampModel))
}

class MovingManGameModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.game.title".translate, false, false, false, false, -6, false, 0.0, true, MotionSeriesDefaults.forceMotionViewport)

class MovingManApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  addModule(new MovingManGameModule(getPhetFrame, newClock))
}

object MovingManApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "moving-man".literal, classOf[MovingManApplication])
}
