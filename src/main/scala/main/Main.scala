package main

import java.io.IOException
import java.net.URL

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver}
import scalafx.Includes._

object Main extends JFXApp {
  val resource: URL = getClass.getResource("/GUI.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: GUI.fxml")
  }

  val root = FXMLView(resource, NoDependencyResolver)
  root.getStylesheets.add(getClass.getResource("/styles.css").toExternalForm)

  stage = new PrimaryStage() {
    title = "FXML Demo"
    scene = new Scene(root)
  }
}
