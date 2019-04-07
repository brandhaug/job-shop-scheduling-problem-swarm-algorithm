package main

import java.io.File

import gantt.{GanttChart, JSSPGanttChart}
import jssp.{JSSP, Job, Machine, OperationTimeSlot, ProblemFileReader}
import scalafx.animation.AnimationTimer
import scalafx.scene.control.{Button, ComboBox, Label}
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._

@sfxml
class Controller(val pane: Pane,
                 val vBoxMenu: VBox,
                 val comboBox: ComboBox[String],
                 val generationLabel: Label,
                 val makeSpanLabel: Label,
                 val startButton: Button,
                 val resetButton: Button) {

  val directoryName = "/problems"
  val files: List[File] = listFiles(directoryName)
  val fileNames: List[String] = files.map(file => file.getName).sorted
  var selectedFileName: String = initializeFileSelector(fileNames)

  var animationTimer: AnimationTimer = _

  // States
  var paused = true


  initialize()

  def initialize(): Unit = {
    initializeGui()
    val (jobs, machines): (Seq[Job], Seq[Machine]) = ProblemFileReader.readFile(directoryName, selectedFileName)
    val jssp: JSSP = new JSSP(jobs, machines)

    animationTimer = AnimationTimer(_ => {
      if (!paused) {
        val (generation, bestSchedule, bestMakeSpan): (Int, Seq[OperationTimeSlot], Int) = jssp.tick()
        render(generation, bestSchedule, bestMakeSpan, machines)
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
  def selectFile(): Unit = {
    selectedFileName = comboBox.getValue.toString
    reset()
  }

  def toggleStart(): Unit = {
    paused = !paused

    if (paused) {
      startButton.setText("Start")
      comboBox.setVisible(true)
    }
    else {
      startButton.setText("Pause")
      comboBox.setVisible(false)
    }
  }

  def reset(): Unit = {
    paused = true
    animationTimer.stop()
    pane.children.clear()
    initialize()
  }
}
