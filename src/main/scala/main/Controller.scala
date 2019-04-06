package main

import java.io.{File, InputStream}

import scalafx.scene.canvas.Canvas
import scalafx.scene.control.ComboBox
import scalafx.scene.layout.VBox
import scalafxml.core.macros.sfxml
import swarm.JSSP

@sfxml
class Controller(val canvas: Canvas,
                 val vboxMenu: VBox,
                 val comboBox: ComboBox[String]) {

  val directoryName = "/data"
  val files: List[File] = listFiles(directoryName)
  val fileNames: List[String] = files.map(file => file.getName).sorted
  var selectedFileName: String = initializeFileSelector(fileNames)
  val (jobs, machines): (Seq[Job], Seq[Machine]) = readFile(directoryName, selectedFileName)
  val jssp: JSSP = new JSSP(jobs, machines)

  def readFile(directoryName: String, fileName: String): (Seq[Job], Seq[Machine]) = {
    val stream: InputStream = getClass.getResourceAsStream(directoryName + "/" + fileName)
    val lines: List[String] = scala.io.Source.fromInputStream(stream).getLines.toList
    val first :: rest = lines

    // Parse first line and set args
    val numberOfJobs :: numberOfMachines :: Nil = first.trim.split("\\s+").map(_.toInt).toList

    // Parse rest of file
    val jobs: Seq[Job] = for {
      (line, jobId) <- rest.zipWithIndex if jobId < numberOfJobs
    } yield {
      val jobLine = line.trim.split("\\s+").map(_.toInt)

      // create jobs from job line
      val operations: Seq[Operation] = for (j <- jobLine.indices by 2) yield {
        val machineId = jobLine(j)
        val operationDuration = jobLine(j + 1)
        Operation(machineId, jobId, operationDuration)
      }

      Job(jobId, operations)
    }

    val machines: Seq[Machine] = for (_ <- 0 until numberOfMachines) yield {
      Machine()
    }

    (jobs, machines)
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

  def selectFile(): Unit = {
    selectedFileName = comboBox.getValue.toString
  }

  def initializeFileSelector(fileNames: List[String]): String = {
    fileNames.foreach(fileName => comboBox += fileName)
    val selectedFileName = fileNames.head
    comboBox.getSelectionModel.select(fileNames.indexOf(selectedFileName))
    selectedFileName
  }

  def reset(): Unit = {

  }
}
