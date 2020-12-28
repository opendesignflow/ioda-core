package org.odfi.ioda.ui.jfx.singlevalue

import javafx.collections.ObservableList
import javafx.scene.chart.{Chart, LineChart, NumberAxis, XYChart}
import javafx.scene.control.{Button, CheckBox, Label, TextField}
import javafx.scene.layout.HBox
import org.controlsfx.control.{PopOver, ToggleSwitch}
import org.odfi.ioda.data.types.IntValueMessage
import org.odfi.ioda.pipelines.PipelineForVCID
import org.odfi.ioda.ui.jfx.JavaFXUtilsTrait
import org.odfi.ioda.uwisk.pipeline.WPipeline
import scala.jdk.CollectionConverters._

class SingleValueToggleSwitchView extends HBox with WPipeline with PipelineForVCID with JavaFXUtilsTrait {

  var stackedValues = scala.collection.mutable.ListBuffer[(Int, Boolean)]()


  this.onWiskMessage {
    case (svm: IntValueMessage, ctx) =>
      svm.processingContext.logInfo(s"Got Single Value for Single Toggle switch (${stackedValues.size})")
      onJFXThreadBlocking {
        valField.setText(svm.value.get.toString)
        if (graphGatherEnable.isSelected) {
          this.stackedValues.synchronized {

            stackedValues.addOne(svm.value.get, recalculateSwitch)
          }
        }
      }
  }

  // Name
  val sensorNameLabel = new Label()

  override def acceptVCID(id: String): Unit = {
    super.acceptVCID(id)
    sensorNameLabel.setText(s"Sensor: ${acceptedVCID.get}")
  }

  this.getChildren.add(sensorNameLabel)


  // Value
  val valLabel = new Label("Value: ")
  this.getChildren.add(valLabel)

  val valField = new TextField()
  valField.setEditable(false)
  this.getChildren.add(valField)

  // Threshold
  val thLabel = new Label("Threshold: ")
  this.getChildren.add(thLabel)
  val thField = new TextField()
  thField.setEditable(true)
  thField.setText("0")
  onJFXPropertyChanged(thField.textProperty()) {
    newVal =>
      thSwitch.setSelected(recalculateSwitch)

  }
  this.getChildren.add(thField)


  // Switch
  val thSwitch = new ToggleSwitch()
  onJFXPropertyChanged(valField.textProperty()) {
    newVal =>
      //println("Value updated: " + newVal)
      thSwitch.setSelected(recalculateSwitch)

  }
  this.getChildren.add(thSwitch)

  // Graph Stack
  val graphGatherEnable = new CheckBox()
  graphGatherEnable.setText("Save for Graph")
  graphGatherEnable.setSelected(true)
  onJFXBooleanPropertyChange(graphGatherEnable.selectedProperty()) {
    selected =>
      // On Change to selected, reset stack
      if (selected) {
        this.stackedValues.synchronized {
          this.stackedValues.clear()
        }
      }
  }
  this.getChildren.add(graphGatherEnable)

  // Graph Open
  val graphOpen = new Button("Show Graph")
  onJFXClick(graphOpen) {

    // Pop Over
    val pp = new PopOver()
    pp.setTitle("Sensor graph")

    // Create Chart
    val x = new NumberAxis()
    val y = new NumberAxis()
    val c = new LineChart[Number, Number](x, y)

    // Sensor Data
    val datas = this.stackedValues.map(v => v._1).zipWithIndex.map {
      case (y, x) =>
        new XYChart.Data[Number, Number](x.asInstanceOf[Number], y.asInstanceOf[Number])
    }
    val series = new XYChart.Series[Number, Number]()
    series.setName("Sensor value")
    series.getData.addAll(datas.asJava)

    // Threshold data
    val thdatas = this.stackedValues.map {
      case v if (v._2) => thField.getText.toInt
      case other => thField.getText.toInt
    }.zipWithIndex.map {
      case (y, x) =>
        new XYChart.Data[Number, Number](x.asInstanceOf[Number], y.asInstanceOf[Number])
    }
    val thseries = new XYChart.Series[Number, Number]()
    thseries.setName("Threshold Output")
    thseries.getData.addAll(thdatas.asJava)

    c.getData.add(series)
    c.getData.add(thseries)



    //c.getData

    pp.setContentNode(c)
    pp.show(graphOpen)

  }
  this.getChildren.add(graphOpen)

  def recalculateSwitch = {

    if (valField.getText.length > 0 && thField.getText.length > 0) {
      valField.getText.toInt > thField.getText.toInt
    } else {
      false
    }


  }

}
