name := "jssp-swarm"

version := "0.1"

scalaVersion := "2.12.8"

// ScalaFX: https://github.com/scalafx/scalafx
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.181-R13"

// Scala FXML: https://github.com/vigoo/scalafxml
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.4"