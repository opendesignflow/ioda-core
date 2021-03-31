package org.odfi.ioda.ui.sim

import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.Node
import javafx.scene.control.{Button, TextField}
import javafx.scene.layout.{HBox, VBox}
import javafx.util.Callback
import org.odfi.ioda.data.protocols.Level0Protocol
import org.odfi.ioda.data.types.IntValuesMessage
import org.odfi.ioda.pipelines.ScheduleCollectPipelineTrait
import org.odfi.ioda.ui.jfx.{JavaFXUtilsTrait, WPipelineWithSideUI}
import org.odfi.ioda.ui.sim.MultiValueGeneratorAController.getClass
import org.odfi.ioda.uwisk.pipeline.WPipelineWithId

class MultiValueGeneratorAController extends JavaFXUtilsTrait with WPipelineWithId with WPipelineWithSideUI with ScheduleCollectPipelineTrait {

  @FXML
  var valuesCount: TextField = _

  @FXML
  var singleValuesContainer: HBox = _
  var singleValuesControllers = List[SingleValueGeneratorAController]()

  @FXML
  var sendValuesButton: Button = _

  @FXML
  def initialize = {
    println("Init Controller for " + getClass.getCanonicalName)
    rebuildSingleValues

    // NUmber of values
    //-----------
    onJFXPropertyChanged[String](valuesCount.textProperty()) {
      value =>
        try {
          val newCount = value.toInt
          rebuildSingleValues
        } catch {
          case e: Throwable =>

        }
    }

    // Sending Handler
    //-------------
    onJFXClick(sendValuesButton) {

      collect


    }


  }

  override def loadSideUI = {
    val loader = new FXMLLoader
    //loader.setController(this)
    loader.setControllerFactory(new Callback[Class[_],AnyRef] {
      override def call(param: Class[_]): AnyRef = {
        MultiValueGeneratorAController.this
      }
    })
    val vb = loader.load[VBox](getClass.getClassLoader.getResourceAsStream("ioda/ui/sim/MultiValueGeneratorA.fxml"))
    vb
  }

  def rebuildSingleValues = {

    // Clear
    singleValuesContainer.getChildren.clear()

    // Rebuild UI
    singleValuesControllers = (0 until valuesCount.getText().toInt).map {
      i =>

        val (ui, controller) = SingleValueGeneratorAController.load
        singleValuesContainer.getChildren.add(ui)
        controller

    }.toList
  }

  /**
   * Runs on schedule, to be overwritten
   */
  override def collect: Unit = {

    if (singleValuesControllers.size>0) {
      val valuesLine = singleValuesControllers.map(_.getValue).mkString(",")
      println(s"Sending $valuesLine")

      val message = new IntValuesMessage
      message.values = singleValuesControllers.map(_.getValue)
      this.down(message)
    }

  }
}

object MultiValueGeneratorAController {

  def load = {
    val loader = new FXMLLoader
    val vb = loader.load[VBox](getClass.getClassLoader.getResourceAsStream("ioda/ui/sim/MultiValueGeneratorA.fxml"))
    val controller = loader.getController[MultiValueGeneratorAController]
    (vb, controller)
  }


}