package main

import java.io.File

import gantt.{GanttChart, JSSPGanttChart}
import jssp.AlgorithmEnum.AlgorithmEnum
import jssp.{AlgorithmEnum, JSSP, Job, Machine, OperationTimeSlot, ProblemFileReader, State}
import scalafx.animation.AnimationTimer
import scalafx.scene.control.{Button, ComboBox, Label, RadioButton, ToggleGroup}
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._

@sfxml
class Controller(val pane: Pane,
                 val vBoxMenu: VBox,
                 val psoRadioButton: RadioButton,
                 val baRadioButton: RadioButton,
                 val comboBox: ComboBox[String],
                 val generationLabel: Label,
                 val makeSpanLabel: Label,
                 val startButton: Button,
                 val resetButton: Button) {

  val directoryName                  = "/problems"
  val files           : List[File]   = listFiles(directoryName)
  val fileNames       : List[String] = files.map(file => file.getName).sorted
  var selectedFileName: String       = initializeFileSelector(fileNames)

  // Algorithm radio buttons
  val algorithmToggleGroup = new ToggleGroup()
  psoRadioButton.setToggleGroup(algorithmToggleGroup)
  baRadioButton.setToggleGroup(algorithmToggleGroup)
  psoRadioButton.setSelected(true)
  var selectedAlgorithm: AlgorithmEnum = AlgorithmEnum.ParticleSwarmOptimization

  var animationTimer: AnimationTimer = _

  // States
  var paused = true

  initialize()

  def initialize(): Unit = {
    initializeGui()
    val (jobs, machines): (Seq[Job], Seq[Machine]) = ProblemFileReader.readFile(directoryName, selectedFileName)
    val jssp: JSSP = new JSSP(jobs, machines, selectedAlgorithm)

    var previousState: Option[State] = None

    // Since this is just a hardcoded loop, you kind of need to store the previous state somewhere, but at least you're left with only a single var
    // at the root of your call hierarchy, and not scattered around within the state transition logic
    animationTimer = AnimationTimer(_ => {
      if (!paused) {
        val current = jssp.tick(previousState)
        previousState = Some(current)
        render(current.generation, current.bestSchedule, current.bestMakeSpan, machines)
      }
    })
    animationTimer.start()
  }

  /**
    * TODO: https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch
    */
  def render(generation: Int, bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int, machines: Seq[Machine]): Unit = {
    generationLabel.setText("Generation: " + generation)
    makeSpanLabel.setText("Make Span: " + bestMakeSpan)
    initializeGanttChart(bestSchedule, machines)
  }

  def initializeGanttChart(bestSchedule: Seq[OperationTimeSlot], machines: Seq[Machine]): Unit = {
    val jgc: JSSPGanttChart = new JSSPGanttChart(machines.asJava)
    val chart: GanttChart[Number, String] = jgc.initializeGanttChart(bestSchedule.asJava)
    chart.setPrefHeight(800)
    chart.setPrefWidth(1000)
    pane.children.clear()
    pane.children.add(chart)
  }

  def initializeGui(): Unit = {
    startButton.setText("Start")
    comboBox.setVisible(true)
    generationLabel.setText("Generation: -")
    makeSpanLabel.setText("Make span: -")
  }

  def initializeFileSelector(fileNames: List[String]): String = {
    fileNames.foreach(fileName => comboBox += fileName)
    val selectedFileName = fileNames.head
    comboBox.getSelectionModel.select(fileNames.indexOf(selectedFileName))
    selectedFileName
  }

  def listFiles(directoryName: String): List[File] = {
    val path = getClass.getResource(directoryName)
    val folder = new File(path.getFile)
    if (folder.exists && folder.isDirectory) {
      folder.listFiles.toList
    } else {
      List[File]()
    }
  }

  /**
    * ScalaFX functions
    */
  def toggleAlgorithm(): Unit = {
    selectedAlgorithm = {
      if (psoRadioButton.selected()) AlgorithmEnum.ParticleSwarmOptimization
      else if (baRadioButton.selected()) AlgorithmEnum.BeesAlgorithm
      else throw new Error("Error in algorithm radio buttons")
    }

    reset()
  }

  def selectFile(): Unit = {
    selectedFileName = comboBox.getValue.toString
    reset()
  }

  def toggleStart(): Unit = {
    paused = !paused

    if (paused) {
      startButton.setText("Start")
    }
    else {
      startButton.setText("Pause")
    }
  }

  def reset(): Unit = {
    paused = true
    animationTimer.stop()
    pane.children.clear()
    initialize()
  }
}
