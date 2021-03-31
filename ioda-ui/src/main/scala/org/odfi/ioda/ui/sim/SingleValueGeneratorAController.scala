package org.odfi.ioda.ui.sim

import javafx.beans.property.SimpleIntegerProperty
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.control.{CheckBox, Slider, Spinner, TextField}
import javafx.scene.layout.{Background, BackgroundFill, Pane, VBox}
import org.odfi.ioda.ui.color.ColorMapper
import org.odfi.ioda.ui.jfx.JavaFXUtilsTrait

import scala.util.Random

class SingleValueGeneratorAController extends JavaFXUtilsTrait {

  @FXML
  var maxValue: TextField = _

  @FXML
  var minValue: TextField = _

  @FXML
  var sensorBits: Spinner[Integer] = _

  @FXML
  var sensorValue: Spinner[Integer] = _

  @FXML
  var randomize: CheckBox = _

  @FXML
  var sensorValueSlider: Slider = _

  @FXML
  var colorPane: Pane = _
  val colorMapper = new ColorMapper

  var latestValue = new SimpleIntegerProperty()

  @FXML
  def initialize = {

    // Default randomize
    this.randomize.setSelected(true)

    // Default min Max with sensor bits
    //--------------


    // Bits
    //------------
    this.sensorBits.setValueFactory(new IntegerSpinnerValueFactory(0, 16))
    this.sensorBits.valueFactoryProperty().get().setValue(12)
    updatedSensorBits(12)
    onJFXIntPropertyChange(sensorBits.valueProperty()) {
      v =>
        updatedSensorBits(v)
    }

    //  Slider to Spinner
    //  Spinner to Slider
    //---------
    onJFXDoublePropertyChange(sensorValueSlider.valueProperty()) {
      d =>
        if (this.sensorValue.getValue != d.toInt) {
          this.sensorValue.valueFactoryProperty().get().setValue(d.toInt)

        }
        latestValue.set(d.toInt)

    }
    onJFXIntPropertyChange(sensorValue.valueProperty()) {
      v =>
        if (this.sensorValueSlider.getValue.toInt != v) {
          this.sensorValueSlider.setValue(v.toDouble)
        }
        latestValue.set(v)
    }

    // this.sensorValue.valueFactoryProperty().get().setValue(10)

    // Latest Value
    //---------
    onJFXPropertyChanged(this.latestValue) {
      v =>

        val bg = new Background(new BackgroundFill(colorMapper.mapHeat(v.doubleValue()), null, null))
        colorPane.setBackground(bg)

    }

  }

  /**
   * When the sensor bits are updated
   *
   * @param v
   */
  def updatedSensorBits(v: Int) = {


    updateMinMax(0, Math.pow(2, v).toInt - 1)


  }

  def updateMinMax(min: Int, max: Int) = {

    // Update Min and Max
    //-----------------
    this.sensorValue.setValueFactory(new IntegerSpinnerValueFactory(min, max))
    this.minValue.setText(min.toString)
    this.maxValue.setText(max.toString)
    this.sensorValueSlider.setMin(min.toDouble)
    this.sensorValueSlider.setMax(max.toDouble)

    // Cap sensor value
    //------------
    val actualValue = this.sensorValue.getValue
    if (actualValue < min) {
      updateValue(min)

    } else if (actualValue > max) {
      updateValue(max)
    }

  }

  def updateValue(v: Int) = {
    onJFXThreadLater {
      this.sensorValueSlider.adjustValue(v)
      this.sensorValue.valueFactoryProperty().get().setValue(v)
    }

  }

  def getValue = {

    val resValue = if (randomize.isSelected) {
      val v = Random.between(minValue.getText.toInt, maxValue.getText.toInt).toInt
      updateValue(v)
      v
    } else {
      sensorValue.getValue.toInt
    }

    resValue

  }

}

object SingleValueGeneratorAController {

  def load = {
    val loader = new FXMLLoader
    val vb = loader.load[VBox](getClass.getClassLoader.getResourceAsStream("ioda/ui/sim/SingleValueGeneratorA.fxml"))
    val controller = loader.getController[SingleValueGeneratorAController]
    (vb, controller)
  }
}