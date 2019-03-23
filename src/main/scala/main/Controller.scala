package main

import java.io.{File, InputStream}

import scalafx.scene.canvas.Canvas
import scalafx.scene.control.ComboBox
import scalafx.scene.layout.VBox
import scalafxml.core.macros.sfxml

@sfxml
class Controller(val canvas: Canvas,
                 val vboxMenu: VBox,
                 val comboBox: ComboBox[String]) {

  val dirName = "/data"
  val files: List[File] = listFiles(dirName)
  val fileNames: List[String] = files.map(file => file.getName).sorted
  var selectedFileName: String = initializeFileSelector(fileNames)
  val jobs: Seq[Job] = readFile(dirName, selectedFileName)

  def readFile(folder: String, fileName: String): Seq[Job] = {
    val stream: InputStream = getClass.getResourceAsStream(folder + "/" + fileName)
    val lines: List[String] = scala.io.Source.fromInputStream(stream).getLines.toList
    val first :: rest = lines

    // Parse first line and set args
    val numberOfJobs :: numberOfMachines :: Nil = first.split("\\s+").map(_.toInt).toList

    // Parse rest of file
    val jobs: Seq[Job] = for {
      (line, i) <- rest.zipWithIndex if i < numberOfJobs
    } yield {
      val jobLine = line.trim.split("\\s+").map(_.toInt)

      // create jobs from job line
      val operations = for (j <- jobLine.indices by 2) yield {
        val machineId = jobLine(j)
        val duration = jobLine(j + 1)
        println(machineId + " " + duration)
        Operation(machineId, duration)
      }

      Job(operations)
    }

    jobs
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
