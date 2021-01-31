package org.odfi.ioda.ui.dev

import javafx.beans.property.SimpleStringProperty
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.Node
import javafx.scene.control.TextArea

import java.net.URL
import scala.io.Source

class JSPipelineController {


  @FXML
  var jsTextArea : TextArea = _
  val jsTextProperty = new SimpleStringProperty()


  @FXML
  def initialize = {

    // Connect Text to main string property
    jsTextArea.textProperty().bindBidirectional(jsTextProperty)

  }

  def loadJSFile(url:URL) : Unit = {

    jsTextProperty.set(Source.fromURL(url).mkString)

  }
}

object JSPipelineController {

  def load = {

    val loader = new FXMLLoader
    val ui = loader.load[Node](getClass.getClassLoader.getResourceAsStream("ioda/ui/JSPipelineUI.fxml"))
    (ui,loader.getController[JSPipelineController])

  }
}
