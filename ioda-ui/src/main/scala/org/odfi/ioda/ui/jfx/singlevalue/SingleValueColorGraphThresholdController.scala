package org.odfi.ioda.ui.jfx.singlevalue

import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty, SimpleStringProperty, StringProperty}
import javafx.embed.swing.SwingNode
import javafx.geometry.{Insets, Orientation}
import javafx.scene.Node
import javafx.scene.chart.{LineChart, NumberAxis, XYChart}
import javafx.scene.control.{CheckBox, Label, Separator, TextField}
import javafx.scene.layout.{Background, BackgroundFill, BorderPane, GridPane, HBox, Pane, Priority, Region, VBox}
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.xy.XYSeries
import org.odfi.ioda.data.types.IntValuesMessage
import org.odfi.ioda.ui.jfx.WPipelineWithSideUI
import org.odfi.ioda.uwisk.pipeline.{WPipeline, WPipelineWithId}
import org.jfree.data.xy.XYSeriesCollection
import org.odfi.ioda.ui.color.{ColorMapper, RefColorPane}
import org.odfi.ioda.ui.phy.state.UIResetMessage

import java.util
import scala.jdk.CollectionConverters._

class SingleValueColorGraphThresholdController extends WPipelineWithId with WPipelineWithSideUI {



  // Side Top Pane
  //-----------------------
  val topPane = new BorderPane()

  // Top controls
  //----------
  val topControls = new VBox()
  topControls.setFillWidth(true)
  topControls.setSpacing(5)

  //-- Color Mapper overview
  val colorRef = new RefColorPane
  //colorRef.setMinHeight(50)
  topControls.getChildren.add(colorRef)

  // Create Color Mapper and update selected map based on UI component
  val colorMapper = new ColorMapper
  onJFXPropertyChanged(colorRef.selectedMapObjectProperty) {
    map =>
     colorMapper.selectedMap = map
  }

  //-- Animated and Buffer Size
  val ctrlAnimated = new SimpleBooleanProperty(false)
  val ctrlAnimatedBox = new CheckBox("Animated Graph")
  ctrlAnimatedBox.selectedProperty().bindBidirectional(ctrlAnimated)

  val ctrlBufferSize = new SimpleIntegerProperty(64)
  val ctrlBufferField = new TextField(ctrlBufferSize.get().toString)
  onJFXPropertyChanged(ctrlBufferField.textProperty()) {
    case null =>
    case "" =>
    case strValue =>
      try {
        assert(strValue.toInt>16,"Should be more than 16")
        assert(strValue.toInt<512,"Less than 512")
        ctrlBufferSize.set(strValue.toInt)
      } catch {
        case e : Throwable =>
          println(s"Could not update buf size $strValue: "+e.getLocalizedMessage)
      }
  }

  topControls.getChildren.add(new HBox(ctrlAnimatedBox, new Separator(Orientation.VERTICAL), new Label("Buffer Size (16-512): "),ctrlBufferField))

  // Main Grid
  //---------------
  val mainGrid = new GridPane
  //mainGrid.setGridLinesVisible(true)

  def initMainGrid(fields: Int) = {

    mainGrid.getChildren.clear()


    try {
      (0 until fields).grouped(2).zipWithIndex.foreach {
        case (r, row) =>

          r.foreach {
            i =>
              val col = i % 2
              println(s"Adding element $col $row")
              val element = new SensorField
              mainGrid.getChildren.add(element)
              element.thField.textProperty().bindBidirectional(thresholdText)
              GridPane.setConstraints(element,col, row)
              GridPane.setFillHeight(element, true)
              GridPane.setFillWidth(element, true)
              GridPane.setHgrow(element,Priority.ALWAYS)
              GridPane.setVgrow(element,Priority.ALWAYS)
              GridPane.setMargin(element,new Insets(5,5,5,5))

          }


      }
    } catch {
      case e : Throwable =>
        e.printStackTrace()
    }



  }

  // Grid Elements
  //----------
  val thresholdText = new SimpleStringProperty()

  class SensorField extends HBox with WPipeline {

    // Inits
    this.setFillHeight(true)


    //-- Color Pane
    //-------

    val colorPane = new Pane
    colorPane.setMinWidth(100)
    HBox.setHgrow(colorPane,Priority.ALWAYS)
    //colorPane.setStyle("-fx-background-color: red")
    this.getChildren.add(colorPane)

    onJFXMouseClicked(colorPane){
      case ev if (ev.getClickCount==2) =>

        if (this.getChildren.contains(controlsBox)) {
          this.getChildren.remove(controlsBox)
        } else {
          this.getChildren.add(controlsBox)
        }


        //controlsBox.setVisible(!controlsBox.isVisible)
      case ev =>




    }


    //-- Controls
    //----------------
    val controlsBox = new VBox()
    this.getChildren.add(controlsBox)

    //---- Chart
    //--------------
    val xAxis = new NumberAxis()
    val yAxis = new NumberAxis()
    val chart = new LineChart[Number,Number](xAxis,yAxis)
    chart.setLegendVisible(false)
    chart.animatedProperty().bindBidirectional(ctrlAnimated)

    var rollingBufferSize = ctrlBufferSize.get()
    var rollingBuffer = new Array[Number](rollingBufferSize+1)
    var rollingBufferIndex = 0

    onJFXPropertyChanged(ctrlBufferSize) {
      bufSize =>

        val newBufSize = bufSize.intValue()
        println(s"*** Resetting buffers  to $newBufSize ")


        // Reset Array
        // Copy old to new starting from the end index to keep the latest values
        val newArray = new Array[Number](newBufSize+1)
        val oldStart = (rollingBufferSize - newBufSize) match { case neg if (neg<0) => 0 ; case other => other }
        val size = rollingBufferIndex match {
          case larger if (larger>newBufSize) => newBufSize
          case other => rollingBufferIndex
        }
        System.arraycopy(rollingBuffer,oldStart,newArray,0, size)

        // Change variables
        rollingBufferSize = newBufSize
        rollingBufferIndex = size

        // Update Chart
        series.getData.clear()

    }


    val series = new XYChart.Series[Number,Number]
    chart.getData.add(series)

    /*val cSeries = new XYSeries("Sensor Value")
    val cDataSet = new XYSeriesCollection
    cDataSet.addSeries(cSeries)

    val lineChart = ChartFactory.createXYLineChart("Sensor Values","A","B",cDataSet)
    val snode = new SwingNode
    snode.setContent(new ChartPanel(lineChart))*/
    val chartPane = new BorderPane()
    chartPane.setCenter(chart)
    chartPane.setMinWidth(50)
    chartPane.setMinHeight(50)
    controlsBox.getChildren.add(chartPane)

    //----- Threshold
    //-------------------
    val thLabel = new Label("Threshold")
    val thField = new TextField()
    //controlsBox.getChildren.add(new HBox(thLabel, thField))

    //----- Value
    //--------------

    // Props
    val latestValueProp = new SimpleIntegerProperty()
    val latestValueTextProp = new SimpleStringProperty()

    // On change -> Update text and add to chart
    onJFXPropertyChanged(latestValueProp) {
      v =>
        // Update Text
        latestValueTextProp.set(v.toString)

    }

    // Labels
    val latestValueLabel = new Label("Latest Value: ")
    val latestValueText = new Label()
    latestValueText.textProperty.bindBidirectional(latestValueTextProp)
    controlsBox.getChildren.add(new HBox(latestValueLabel, latestValueText))

    //----- Max Value
    //--------------
    var maxValue = 0
    val maxValueLabel = new Label("Max Value: ")
    val maxValueLabelText = new Label()
    //latestValueText.textProperty.bindBidirectional(latestValueTextProp)
    controlsBox.getChildren.add(new HBox(maxValueLabel, maxValueLabelText))

    // Dispatch
    //-------------
    def dispatchValue(v:Int) = {

      // Latest Value
      //-------------
      latestValueProp.set(v)

      // Max Value
      //--------------
      if (v>maxValue) {

        maxValue = v
        maxValueLabelText.setText(v.toString)
      }

      // Update Chart
      //-------------

      //-- add
      rollingBuffer(rollingBufferIndex) = v

      //-- If at the end, shift everything
      if (rollingBufferIndex==rollingBufferSize) {

       System.arraycopy(rollingBuffer,1,rollingBuffer,0,rollingBufferSize)
       // rollingBuffer = rollingBuffer.slice(1,rollingBufferSize)

        series.getData().setAll(rollingBuffer.zipWithIndex.map{case (v,i) =>new XYChart.Data[Number,Number](i,v) }.toList.asJava)

      } else {
        rollingBufferIndex +=  1
        series.getData().add(new XYChart.Data[Number,Number](series.getData.size(),v))
      }

      //series.getData().add(new XYChart.Data[Number,Number](series.getData.size(),v))

      // Update Color
      val bg = new Background(new BackgroundFill(colorMapper.mapHeat(v.doubleValue()),null,null))
      colorPane.setBackground(bg)

    }


  }

  // Add All and return top
  //---------------
  topPane.setCenter(mainGrid)
  topPane.setTop(topControls)

  override def loadSideUI: Node = topPane


  // State
  //-------------

  def dispatchValues(ints:List[Int])= {

    ints.zipWithIndex.foreach {
      case (v,i) =>
        if (i<mainGrid.getChildren.size()) {
          mainGrid.getChildren.get(i).asInstanceOf[SensorField].dispatchValue(v)
        }
    }

  }

  // Receive
  this.onWiskMessage {
    case (ints: IntValuesMessage, _) =>
      //println("Received Values: " + ints.values.size)

      // Init?
      //----------
      onJFXThreadLater {
      //  println("Grid has children: "+mainGrid.getChildren.size())
        if (mainGrid.getChildren.size() == 0) {

          initMainGrid(ints.values.size)
        }
        // Update values
        dispatchValues(ints.values)
      }

    case (reset : UIResetMessage,_) =>
      onJFXThreadLater {
        mainGrid.getChildren.clear()
      }
  }

}
