package main

import scalafx.scene.canvas.Canvas
import scalafxml.core.macros.sfxml

@sfxml
class Controller(private val canvas: Canvas) {
  println(canvas.getWidth)
}
