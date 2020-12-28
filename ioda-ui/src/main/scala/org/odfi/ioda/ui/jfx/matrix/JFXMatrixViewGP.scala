package org.odfi.ioda.ui.jfx.matrix

import javafx.scene.control.Label
import javafx.scene.layout.{Background, ColumnConstraints, GridPane, Priority, RowConstraints}
import javafx.scene.paint.{Color, Paint}
import javafx.scene.shape.Rectangle
import org.odfi.indesign.core.module.jfx.JavaFXUtilsTrait
import org.odfi.ioda.data.protocols.matrix.DoubleMatrixValueMessage
import org.odfi.ioda.uwisk.pipeline.WPipeline

/*
import com.innovationlab.board.data.MatrixValueMessage
import org.odfi.indesign.core.module.jfx.JavaFXUtilsTrait
import com.innovationlab.board.data.transform.DataCollector
import org.odfi.indesign.core.module.jfx.JFXRun
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import scala.collection.convert.DecorateAsScala
import com.innovationlab.ILModule*/

class JFXMatrixViewFlexible extends GridPane with JavaFXUtilsTrait with WPipeline {


  var uiBuild = false

  var matrixValues : List[List[Double]] = _

  this.gridLinesVisibleProperty().setValue(true)
  this.setGridLinesVisible(true)
  //this.backgroundProperty().set(new Background(Color.AZURE))
  this.setStyle("-fx-background-color:azure")

  this.onDownMessage {
    case msg : DoubleMatrixValueMessage =>

      msg.processingContext.logInfo("Got Matrix message to display")
      onJFXThreadLater {
        this.updateValues(msg.data.get)
      }

  }


  def rebuildUI = (uiBuild,matrixValues) match {

      // Build UI
    case (false,mValues) if (mValues!=null) =>

      println("Rebuilding UI")

      // Clear content
      //--------------
      this.getChildren.clear()
      this.getColumnConstraints.clear()

      // Create Rows and columns
      this.matrixValues.zipWithIndex.foreach {
        case (colValues, row) =>

          // Set Constraints
          //if (this.getRowConstraints.size()<this.matrixValues.size) {
            val rowConstraints = new RowConstraints()
            rowConstraints.setVgrow(Priority.ALWAYS)
            rowConstraints.setFillHeight(true)
          //rowConstraints.setPrefHeight(100)

            this.getRowConstraints.add(rowConstraints)
         // }

          // Columns
          colValues.zipWithIndex.foreach {
            case (colValue , col) =>

              /*val rect = new Rectangle()
              rect.setStyle("-fx-background-color:green")
              rect.setFill(Color.ALICEBLUE)
              rect.setWidth(100)
              rect.setHeight(100)
              this.add(rect,col,row)*/
              val lbl =  new Label(s"TT $row : $col")
              //val lbl = new Rectangle()
              lbl.setStyle("-fx-background-color:green")
              lbl.setMaxSize(Double.MaxValue,Double.MaxValue)
              //lbl.setManaged(true)
              //lbl.setWidth(Double.MaxValue)
              this.add(lbl,col,row)
              GridPane.setFillHeight(lbl,true)
              GridPane.setFillWidth(lbl,true)
              GridPane.setHgrow(lbl,Priority.ALWAYS)
              GridPane.setVgrow(lbl,Priority.ALWAYS)
              println("Added rect")

              // Set Constraints
              if (this.getColumnConstraints.size()<colValues.size) {
                val colConstraints = new ColumnConstraints()
                colConstraints.setHgrow(Priority.ALWAYS)
                colConstraints.setFillWidth(true)
                //colConstraints.setPrefWidth(100)
                this.getColumnConstraints.add(colConstraints)
              }

          }
      }

      uiBuild = true


    case other =>

  }

  def updateValues(values: List[List[Double]]) = {

    try {
      // Ensuire UI Is build
      this.matrixValues = values
      rebuildUI
    } catch {
      case e : Throwable =>
        e.printStackTrace()
    }


    // Update values
    val allValues = this.matrixValues.flatten
    allValues.zipWithIndex.foreach {
      case (v,i) =>
        val actualValue = v.toLong.toDouble
        val color = valueToColor(actualValue)
        //this.getChildren.get(i).asInstanceOf[Label].setStyle(s"-fx-background-color: rgb(${color.getRed.toInt},${color.getGreen.toInt},${color.getBlue.toInt})")
        val lbl = this.getChildren.get(i).asInstanceOf[Label]
          lbl.setText(actualValue.toString)
        val styleString = s"-fx-background-color: hsb(${color.getHue.toInt},${(color.getSaturation*100).toInt}%,${(color.getBrightness*100).toInt}%)"
          lbl.setStyle(styleString)
        println("Style: "+styleString)
    }

  }

  def valueToColor(v:Double) = {

    var range = Color.BLUE.getHue() - Color.RED.getHue()
    var valueRange = 1024.0 - 0.0
    var valuesStep = valueRange / range
    var valueInSteps = (v / valuesStep)

    var hue = Color.BLUE.getHue() - valueInSteps

    println("Value: "+v+" -> "+valueInSteps+" -> "+hue)

     Color.hsb(hue, 1.0, 0.8);
  }

}

/*
class JFXMatrixViewGP(val transformer: DataCollector[MatrixValueMessage]) extends GridPane with JavaFXUtilsTrait with DecorateAsScala {

  var size = 50
  var matrixWidth = -1
  var matrixHeight = -1

  var matrixesRectangles = Array[Array[Rectangle]]()

  //this.setGridLinesVisible(true)
  this.setHgap(0)
  this.setVgap(0)

  transformer.onUpdated {

    JFXRun.onJavaFX {

      transformer.getResource[MatrixValueMessage] match {
        case Some(message) =>

          //-- Clear UI if needed
          (message.width.data, message.height.data) match {
            case (w, h) if (w != matrixWidth || h != matrixHeight) =>
              println(s"Recreate UI: $w , $h , $matrixWidth , $matrixHeight")
              getChildren.clear()
            case _ =>
          }

          matrixWidth = message.width
          matrixHeight = message.height

          //-- Create UI if needed
          getChildren.size() match {

            case 0 =>
              //println(s"Creating UI: " + message.pixels.size)

              matrixesRectangles = new Array[Array[Rectangle]](message.width)

              //-- Create all Rectangles
              (0 until message.width.data).foreach {
                column =>

                  matrixesRectangles(column) = new Array[Rectangle](message.height)

                  (0 until message.height.data).foreach {
                    row =>

                      //println(s"Added rect")
                      var r = new Rectangle

                      var pixel = message.pixels(row * column)

                      //-- Set Position bases on width/height
                      // r.setX(column * size)
                      // r.setY(row * size)

                      //r.setWidth(size)
                      //r.setHeight(size)

                      //-- Style

                      /*r.setStrokeWidth(0.0)
                      r.setStroke(Color.BLACK)
                      r.setStrokeType(StrokeType.CENTERED)*/
                      r.setOpacity(0.0)

                      //println("V: "+pixel.value)

                      r
                      //uiGroup.getChildren.add(r)
                      matrixesRectangles(column)(row) = r
                      add(r, column, row)

                  }
              }

            case _ =>
          }

          //-- Target value range for heat map
          var targetMin = ILModule.config.get.getInt("matrix.MatrixValueRangeAdapter.targetRange.min", 0)
          var targetMax = ILModule.config.get.getInt("matrix.MatrixValueRangeAdapter.targetRange.max", 4096)

          //-- Update colours
          message.pixels.zipWithIndex.foreach {
            case (pixel, i) =>

              /*if(pixel.value>0) {
                 println(s"Pixel ${pixel.x}:${pixel.y} -> " + pixel.value)
              }*/

              // Color Gradient
              //-------------
              var r = matrixesRectangles(pixel.x)(pixel.y)
              var range = Color.BLUE.getHue() - Color.RED.getHue()
              var valueRange = targetMax - targetMin
              var valuesStep = valueRange / range
              var valueInSteps = (pixel.value.data / valuesStep).toInt
              var hue = Color.BLUE.getHue() - valueInSteps

              var color = Color.hsb(hue, 1.0, 0.8);

              //var rectangle = uiGroup.getChildren.get(i).asInstanceOf[Rectangle]
              r.setFill(color)

              // Opacity with Threshold
              //---------
              if (hue < (Color.BLUE.getHue() - 10)) {
                r.setOpacity(1.0)
              } else {
                r.setOpacity(0.0)
              }

            //-- Value
            //var hue = Color.BLUE.getHue() + (Color.RED.getHue() - Color.BLUE.getHue()) * (pixel.value - min) / (max - min);

            // println(s"-> "+ Color.BLUE.getHue()+" -> "+Color.RED.getHue())

          }

          //-- Update scaling
          updateScale

        case None =>

      }

    }
  }

  onJFXReadonlyDoubleChange(this.prefWidthProperty()) {
    v =>
      updateScale
  }

  onJFXReadonlyDoubleChange(this.prefHeightProperty()) {
    v =>
      updateScale
  }

  def updateScale = {

    var targetWidth = this.getPrefWidth
    var targetHeight = this.getPrefHeight

    var message = transformer.getResource[MatrixValueMessage] match {
      case Some(message) =>

        var pixelWidth = targetWidth / message.width
        var pixelHeight = targetHeight / message.height

        this.getChildren.asScala.foreach {
          case r: Rectangle =>
            r.setWidth(pixelWidth)
            r.setHeight(pixelHeight)
          case other =>
        }

      case None =>
    }

    /*
    // Rect width
    var w = this.matrixWidth * size
    var h = this.matrixHeight * size

    // Panel Scale
    var scaleX = this.getWidth / w
    var scaleY = this.getHeight / h

    println(s"Update scaling on ${hashCode} to: mw=$w,w=${getWidth} , my=$h,h=${getHeight}, $scaleX , $scaleY")

    // Update scale
    var scalingTransform = new Scale
    scalingTransform.setX(scaleX)
    scalingTransform.setY(scaleY)

    gp.getTransforms.clear()
    uiGroup.getTransforms.clear()
    // uiGroup.getTransforms.add(new Translate((getWidth - w) / 2, (getHeight - h) / 2))
    //uiGroup.getTransforms.add(new Translate((w - getWidth) / 2, (h - getHeight) / 2))

    gp.getTransforms.add(scalingTransform)
    //uiGroup.getTransforms.add(scalingTransform)*/

  }
}*/