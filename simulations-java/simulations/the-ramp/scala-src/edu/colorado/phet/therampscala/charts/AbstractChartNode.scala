package edu.colorado.phet.therampscala.charts

import common.phetcommon.util.DefaultDecimalFormat
import common.phetcommon.view.util.{PhetFont}
import common.motion.graphs._
import common.motion.model._
import common.phetcommon.model.clock.ConstantDtClock
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.{ShadowHTMLNode}
import common.piccolophet.{PhetPCanvas}
import common.timeseries.model.{RecordableModel, TimeSeriesModel}
import java.awt.event.{FocusEvent, FocusListener, ActionEvent, ActionListener}
import java.awt.geom.Point2D
import java.awt.{FlowLayout, Color}
import javax.swing.{JTextField, JPanel, JLabel}
import model.{RampModel}
import umd.cs.piccolo.PNode
import scalacommon.math.Vector2D
import RampResources._
import RampDefaults._

object Defaults {
  def createFont = new PhetFont(15, true)

  def addListener(series: ControlGraphSeries, listener: () => Unit) = {
    series.addListener(new ControlGraphSeries.Adapter() {
      override def visibilityChanged = listener()
    })
  }
}

class RampChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel, showEnergyGraph: Boolean)
        extends AbstractChartNode(transform, canvas, model) {
  val parallelAppliedForceVariable = new DefaultTemporalVariable() {
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => {
    if (inTimeRange(model.getTime))
      parallelAppliedForceVariable.addValue(model.bead.appliedForce.dot(model.bead.getRampUnitVector), model.getTime)
  })

  val appliedForceSeries = new ControlGraphSeries(formatForce("forces.applied".translate), appliedForceColor, abbrevUnused, N, characterUnused, parallelAppliedForceVariable)
  val frictionSeries = new ControlGraphSeries(formatForce("forces.friction".translate), frictionForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.frictionForce))
  val gravitySeries = new ControlGraphSeries(formatForce("forces.Gravity".translate), gravityForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.gravityForce))
  val wallSeries = new ControlGraphSeries(formatForce("forces.Wall".translate), wallForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.wallForce))
  val netForceSeries = new ControlGraphSeries(formatForce("forces.Net".translate), totalForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.totalForce))
  val forceSeriesList = appliedForceSeries :: frictionSeries :: gravitySeries :: wallSeries :: netForceSeries :: Nil

  val totalEnergySeries = new ControlGraphSeries(formatEnergy("energy.total".translate), totalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getTotalEnergy))
  val keSeries = new ControlGraphSeries(formatEnergy("energy.kinetic".translate), kineticEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getKineticEnergy))
  val peSeries = new ControlGraphSeries(formatEnergy("energy.potential".translate), potentialEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getPotentialEnergy))
  val thermalEnergySeries = new ControlGraphSeries(formatEnergy("energy.thermal".translate), thermalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getThermalEnergy))

  val appliedWorkSeries = new ControlGraphSeries(formatWork("work.applied".translate), appliedWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getAppliedWork))
  val gravityWorkSeries = new ControlGraphSeries(formatWork("work.gravity".translate), gravityWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getGravityWork))
  val frictionWorkSeries = new ControlGraphSeries(formatWork("work.friction".translate), frictionWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getFrictiveWork))
  val energyWorkSeriesList = totalEnergySeries :: keSeries :: peSeries :: thermalEnergySeries :: appliedWorkSeries :: gravityWorkSeries :: frictionWorkSeries :: Nil

  val parallelForceControlGraph = new RampGraph(appliedForceSeries, canvas, timeseriesModel, updateableObject, model) {
    setDomainUpperBound(20)
    for (s <- forceSeriesList.tail) addSeries(s)
  }

  parallelForceControlGraph.addControl(new SeriesSelectionControl("forces.parallel-title-with-units".translate, 5) {
    addToGrid(appliedForceSeries, createEditableLabel)
    for (s <- forceSeriesList.tail) addToGrid(s)
  })

  val workEnergyGraph = new RampGraph(totalEnergySeries, canvas, timeseriesModel, updateableObject, model) {
    setEditable(false)
    setDomainUpperBound(20)
    for (s <- energyWorkSeriesList.tail) addSeries(s)
  }
  workEnergyGraph.addControl(new SeriesSelectionControl("forces.work-energy-title-with-units".translate, 7) {
    for (s <- energyWorkSeriesList) addToGrid(s)
  })

  val parallelForcesString = "forces.parallel-title".translate
  val graphs = if (showEnergyGraph) Array(new MinimizableControlGraph(parallelForcesString, parallelForceControlGraph), new MinimizableControlGraph("forces.work-energy-title".translate, workEnergyGraph))
  else Array(new MinimizableControlGraph(parallelForcesString, parallelForceControlGraph))

  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)
  updatePosition()
}

abstract class AbstractChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel) extends PNode {
  def inTimeRange(time: Double) = {
    //    println("time = "+time)
    time <= RampDefaults.MAX_RECORD_TIME
  }

  def createVariable(getter: () => Double) = {
    val variable = new DefaultTemporalVariable()
    model.stepListeners += (() => {
      if (inTimeRange(model.getTime))
        variable.addValue(getter(), model.getTime)
    })
    variable
  }

  def createParallelVariable(getter: () => Vector2D) = {
    val variable = new DefaultTemporalVariable()
    model.stepListeners += (() => {
      if (inTimeRange(model.getTime))
        variable.addValue(getter().dot(model.bead.getRampUnitVector), model.getTime)
    })
    variable
  }

  val recordableModel = new RecordableModel() {
    def getState: Object = model.getTime.asInstanceOf[Object]

    def resetTime = {}

    def clear = {}

    def setState(o: Any) = model.setPlaybackTime(o.asInstanceOf[Double])

    def stepInTime(simulationTimeChange: Double) = {}
  }

  val timeseriesModel = new TimeSeriesModel(recordableModel, new ConstantDtClock(30, 1.0)) { //todo: remove dummy clock
    override def setPlaybackTime(requestedTime: Double) = model.setPlaybackTime(requestedTime) //skip bounds checking in parent
  }
  model.historyClearListeners += (() => timeseriesModel.clear(true))

  val updateableObject = new UpdateableObject {
    def setUpdateStrategy(updateStrategy: UpdateStrategy) = {}
  }
  import RampResources._
  val N = "units.abbr.newtons".translate
  val J = "units.abbr.joules".translate
  val characterUnused = "".literal
  val abbrevUnused = "".literal

  def createEditableLabel(series: ControlGraphSeries) = {
    val panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0))
    val textField = new JTextField(6)
    textField.addActionListener(new ActionListener() {
      def actionPerformed(e: ActionEvent) = {
        setValueFromText()
      }
    })
    def setValueFromText() = try {
      series.getTemporalVariable.setValue(new DefaultDecimalFormat("0.00".literal).parse(textField.getText).doubleValue)
    } catch {
      case re: Exception => {}
    }
    textField.addFocusListener(new FocusListener() {
      def focusGained(e: FocusEvent) = {}

      def focusLost(e: FocusEvent) = setValueFromText()
    })

    textField.setFont(Defaults.createFont)
    textField.setForeground(series.getColor)
    series.getTemporalVariable.addListener(new ITemporalVariable.ListenerAdapter() {
      override def valueChanged = updateLabel()
    })
    def updateLabel() = textField.setText(new DefaultDecimalFormat("0.00".literal).format(series.getTemporalVariable.getValue))

    updateLabel()

    panel.add(textField)
    val unitsLabel = new JLabel(series.getUnits)
    unitsLabel.setFont(Defaults.createFont)
    unitsLabel.setForeground(series.getColor)
    unitsLabel.setBackground(RampDefaults.EARTH_COLOR)
    panel.add(unitsLabel)
    panel.setBackground(RampDefaults.EARTH_COLOR)
    panel
  }

  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val viewBounds = transform.getViewBounds
    val h = viewBounds.getHeight - viewLoc.y
    graphSetNode.setBounds(viewBounds.getX, viewLoc.y, viewBounds.getWidth, h)
  }

  def graphSetNode: PNode
}

class RampGraph(defaultSeries: ControlGraphSeries, canvas: PhetPCanvas, timeseriesModel: TimeSeriesModel, updateableObject: UpdateableObject, model: RampModel)
        extends MotionControlGraph(canvas, defaultSeries, "".literal, "".literal, -2000, 2000, true, timeseriesModel, updateableObject) {
  getJFreeChartNode.getChart.getXYPlot.getRangeAxis.setTickLabelFont(new PhetFont(18, true))
  getJFreeChartNode.getChart.getXYPlot.getDomainAxis.setTickLabelFont(new PhetFont(18, true))
  getJFreeChartNode.setBuffered(false)
  getJFreeChartNode.setPiccoloSeries() //works better on an unbuffered chart
  setDomainUpperBound(RampDefaults.MAX_CHART_DISPLAY_TIME)
  override def createSliderNode(thumb: PNode, highlightColor: Color) = {
    new JFreeChartSliderNode(getJFreeChartNode, thumb, highlightColor) {
      val text = new ShadowHTMLNode(defaultSeries.getTitle)
      text.setFont(new PhetFont(18, true))
      text.setColor(defaultSeries.getColor)
      text.rotate(-java.lang.Math.PI / 2)
      val textParent = new PNode
      textParent.addChild(text)
      textParent.setPickable(false)
      textParent.setChildrenPickable(false)
      addChild(textParent)

      override def updateLayout() = {
        super.updateLayout()
        if (textParent != null)
          textParent.setOffset(getTrackFullBounds.getX - textParent.getFullBounds.getWidth - getThumbFullBounds.getWidth / 2,
            getTrackFullBounds.getY + textParent.getFullBounds.getHeight + getTrackFullBounds.getHeight / 2 - textParent.getFullBounds.getHeight / 2)
      }
      updateLayout()
    }
  }

  //todo: a more elegant solution would be to make MotionControlGraph use an interface, then to write an adapter
  //for the existing recording/playback model, instead of overriding bits and pieces to obtain this functionality

  override def getCursorShouldBeVisible = model.isPlayback
  model addListener updateCursorVisible

  override def getMaxCursorDragTime = model.getMaxRecordedTime
  model addListener updateCursorMaxDragTime

  override def getPlaybackTime = model.getTime
  model addListener updateCursorLocation

  override def createGraphTimeControlNode(timeSeriesModel: TimeSeriesModel) = new GraphTimeControlNode(timeSeriesModel) {
    override def setEditable(editable: Boolean) = {
      super.setEditable(false)
    }
  }

  override def createReadoutTitleNode(series: ControlGraphSeries) = null
}