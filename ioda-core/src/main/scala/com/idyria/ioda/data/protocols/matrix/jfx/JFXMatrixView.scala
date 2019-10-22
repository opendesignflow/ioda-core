package com.idyria.ioda.data.protocols.matrix.jfx

import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import javafx.scene.paint.Color
import javafx.scene.Group
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import org.odfi.indesign.core.module.jfx.JFXRun
import javafx.scene.layout.GridPane
import org.odfi.indesign.core.module.jfx.JavaFXUtilsTrait
import javafx.scene.layout.Pane
import com.idyria.ioda.data.protocols.Protocol
import javafx.stage.Stage
import com.idyria.ioda.data.protocols.ProtocolWithId
import javafx.scene.Scene
import com.idyria.ioda.data.protocols.matrix.DoubleMatrixValueMessage
import scala.collection.convert.DecorateAsScala
import javafx.scene.layout.Region
import javafx.scene.control.Label
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
/*
class JFXMatrixView extends GridPane with Protocol with JavaFXUtilsTrait with DecorateAsScala {

  var size = 50
  var matrixWidth = -1
  var matrixHeight = -1

  var matrixesRectangles = Array[Array[Rectangle]]()

  this.setHgap(0)
  this.setVgap(0)

  /* var min = 0
  var max = 1024
  var gp = new GridPane
  var uiGroup = new Group

  //getChildren.add(gp)*/

  this.onDownMessage {
    case message: DoubleMatrixValueMessage =>

      //println(s"S updating UI")
      onJFXThreadBlocking {
        //println(s"In updating UI")
        //-- Clear UI if needed
        (message.getColumns, message.getRows) match {
          case (w, h) if (w != matrixWidth || h != matrixHeight) =>
            logFine[JFXMatrixView](s"Recreate UI: $w , $h , $matrixWidth , $matrixHeight")
            getChildren.clear()
          case _ =>
        }

        matrixWidth = message.getColumns
        matrixHeight = message.getRows

        //-- Create UI if needed
        getChildren.size() match {

          case 0 =>
            //println(s"Creating UI: " + message.pixels.size)

            matrixesRectangles = new Array[Array[Rectangle]](message.getRows)

            //-- Create all Rectangles
            (0 until message.getColumns).foreach {
              column =>

                matrixesRectangles(column) = new Array[Rectangle](message.getRows)

                (0 until message.getRows).foreach {
                  row =>

                    //println(s"Added rect")
                    /* var r = new Rectangle

                    var pixel = message.data.get(row)(column)

                    //-- Set Position bases on width/height
                    // r.setX(column * size)
                    // r.setY(row * size)

                    r.setWidth(size)
                    r.setHeight(size)

                    //-- Style

                    r.setStrokeWidth(1.0)
                    r.setStroke(Color.BLACK)
                    r.setStrokeType(StrokeType.CENTERED)

                    //println("V: "+pixel.value)

                    r
                    //uiGroup.getChildren.add(r)
                    gp.add(r, column, row)

                    var range = Color.BLUE.getHue() - Color.RED.getHue()
                    var valueRange = max - min
                    var valuesStep = valueRange / range
                    var valueInSteps = (pixel / valuesStep).toInt
                    var hue = Color.BLUE.getHue() - valueInSteps

                    var color = Color.hsb(hue, 1.0, 0.8);

                    //var rectangle = uiGroup.getChildren.get(i).asInstanceOf[Rectangle]
                    r.setFill(color)*/

                    //println(s"Added rect")
                    var r = new Rectangle

                    var pixel = message.data.get(row)(column)

                    //-- Set Position bases on width/height
                    // r.setX(column * size)
                    // r.setY(row * size)

                    //r.setWidth(size)
                    //r.setHeight(size)

                    //-- Style

                    /*r.setStrokeWidth(0.0)
                      r.setStroke(Color.BLACK)
                      r.setStrokeType(StrokeType.CENTERED)*/
                    r.setOpacity(1.0)

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
        var targetMin = 0
        var targetMax = 1024

        //-- Update colours
        (0 until message.getColumns).foreach {
          column =>

            (0 until message.getRows).foreach {
              row =>

                /*if(pixel.value>0) {
                 println(s"Pixel ${pixel.x}:${pixel.y} -> " + pixel.value)
              }*/

                var pixel = message.data.get(row)(column)
                //println(s"Pixel ${column}:${row} -> " + pixel)

                // Color Gradient
                //-------------
                var r = matrixesRectangles(column)(row)
                var range = Color.BLUE.getHue() - Color.RED.getHue()
                var valueRange = targetMax - targetMin
                var valuesStep = valueRange / range
                var valueInSteps = (pixel / valuesStep).toInt
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
        }

        //-- Update scaling
        updateScale

        /*
        //-- Update colours
        (0 until message.getRows).foreach {
          row =>
            (0 until message.getColumns).foreach {
              column =>

                val r = gp.getChildren.get(row * column).asInstanceOf[Rectangle]
                var pixel = message.data.get(row)(column)
                var range = Color.BLUE.getHue() - Color.RED.getHue()
                var valueRange = max - min
                var valuesStep = valueRange / range
                var valueInSteps = (pixel / valuesStep).toInt
                var hue = Color.BLUE.getHue() - valueInSteps

                var color = Color.hsb(hue, 1.0, 0.8);

                //var rectangle = uiGroup.getChildren.get(i).asInstanceOf[Rectangle]
                r.setFill(color)

            }

        }*/

        //println(s"EOF updating UI")

      }
    // println(s"Done updating UI")

    // EOF JFX

  }
  // EOF on down

  onJFXReadonlyDoubleChange(this.widthProperty()) {
    v =>
      updateScale
  }

  onJFXReadonlyDoubleChange(this.heightProperty()) {
    v =>
      updateScale
  }

  def updateScale = {

    //setBackground(new Background(new BackgroundFill(Pain)))
    //setStyle("-fx-background-color: red")
    //var targetWidth = this.getPrefWidth
    //var targetHeight = this.getPrefHeight
    var targetWidth = getWidth
    var targetHeight = getHeight

    var pixelWidth = (targetWidth / matrixWidth)
    var pixelHeight = (targetHeight / matrixHeight)

    logFine[JFXMatrixView]("PX: " + pixelWidth + " -> " + pixelHeight)
    logFine[JFXMatrixView]("PX2: " + getWidth + " -> " + getHeight)
    logFine[JFXMatrixView]("PX3: " + getPrefWidth + " -> " + getPrefHeight)
    this.getChildren.asScala.foreach {
      case r: Rectangle =>
        r.setWidth(pixelWidth)
        r.setHeight(pixelHeight)
      //r.setWidth(5)
      //r.setHeight(5)
      case other =>
    }

  }

}

class JFXMatrixStage extends Stage with ProtocolWithId with JavaFXUtilsTrait {

  // Create Grid
  //---------------
  val grid = new GridPane
  grid.setPrefWidth(Region.USE_COMPUTED_SIZE)
  grid.setPrefHeight(Region.USE_COMPUTED_SIZE)

  grid.setMaxSize(Double.MaxValue, Double.MaxValue);

  grid.setGridLinesVisible(true)
  grid.setHgap(5)
  grid.setVgap(5)
  var scene = new Scene(grid)
  this.setScene(scene)
  this.show()

  var views = Map[String, JFXMatrixView]()

  this.onConfigModelUpdated {
    config.get.supportIntKey("width", 800, "Stage Width")
    config.get.supportIntKey("height", 600, "Stage Height")

    config.get.supportIntKey("columns", 2, "Number of Columns")

    this.setWidth(config.get.supportGetInt("width").get)
    this.setHeight(config.get.supportGetInt("height").get)

  }

  this.onDownMessage {
    m =>

      // Create Matrix View?
      onJFXThreadBlocking {

        println("Got message for: " + m.virtualChannel + "-> " + views.size)
        
        val matrixView = views.getOrElse(m.virtualChannel.get, {
          val matrix = new JFXMatrixView
          // matrix.setPrefWidth(Region.USE_COMPUTED_SIZE)
          // matrix.setPrefHeight(Region.USE_COMPUTED_SIZE)
          matrix.setMaxSize(Double.MaxValue, Double.MaxValue);
          matrix.setMinSize(0, 0)
          logFine[JFXMatrixStage]("Adding matrix -> " + views.size % config.get.supportGetInt("columns").get)

          grid.add(matrix, views.size % config.get.supportGetInt("columns").get, views.size / config.get.supportGetInt("columns").get)

          //grid.add(new Label(s"Matrix ${m.virtualChannel}"), views.size % config.get.supportGetInt("columns").get, views.size / config.get.supportGetInt("columns").get)

          //-- Update constraints
          var rows = (views.size / config.get.supportGetInt("columns").get) + 1
          var columns = config.get.supportGetInt("columns").get
          grid.getColumnConstraints.clear()
          (0 until columns).foreach {
            i =>
              var c = new ColumnConstraints()

              c.setPercentWidth(100 / columns);
              c.setHgrow(Priority.ALWAYS)
              grid.getColumnConstraints.add(c)
          }

          grid.getRowConstraints.clear()
          (0 until rows) foreach {
            i =>

              var c = new RowConstraints
              c.setVgrow(Priority.ALWAYS)
              c.setPercentHeight(100 / rows)
              grid.getRowConstraints.add(c)
          }

          views = views + (m.virtualChannel.get.toString -> matrix)

          matrix
        })

        matrixView.down(m)

      }

    /* mview.setPrefWidth(config.get.supportGetInt("width").get)
      mview.setPrefHeight(config.get.supportGetInt("height").get)
      mview.down(m)*/

    /*onJFXThreadBlocking {
        this.show()
      }*/

  }

}


/*
class JFXMatrixView(transformer: DataCollector[MatrixValueMessage]) extends JFXTransformPanel(transformer) with JavaFXUtilsTrait {

  var size = 50
  var matrixWidth = -1
  var matrixHeight = -1
  var min = 0
  var max = 1024

  var gp = new GridPane
  var uiGroup = new Group

  getChildren.add(gp)

  transformer.onUpdated {

    JFXRun.onJavaFX {

      transformer.getResource[MatrixValueMessage] match {
        case Some(message) =>

          //-- Clear UI if needed
          (message.width.data, message.height.data) match {
            case (w, h) if (w != matrixWidth || h != matrixHeight) =>
              println(s"Recreate UI: $w , $h , $matrixWidth , $matrixHeight")
              uiGroup.getChildren.clear()
            case _ =>
          }

          matrixWidth = message.width
          matrixHeight = message.height

          //-- Create UI if needed
          uiGroup.getChildren.size() match {

            case 0 =>
              //println(s"Creating UI: " + message.pixels.size)

              //-- Create all Rectangles
              (0 until message.height.data).foreach {
                row =>
                  (0 until message.width.data).foreach {
                    column =>

                      //println(s"Added rect")
                      var r = new Rectangle

                      var pixel = message.pixels(row * column)

                      //-- Set Position bases on width/height
                      // r.setX(column * size)
                      // r.setY(row * size)

                      r.setWidth(size)
                      r.setHeight(size)

                      //-- Style

                      r.setStrokeWidth(1.0)
                      r.setStroke(Color.BLACK)
                      r.setStrokeType(StrokeType.CENTERED)

                      //println("V: "+pixel.value)

                      r
                      //uiGroup.getChildren.add(r)
                      gp.add(r, column, row)

                      var range = Color.BLUE.getHue() - Color.RED.getHue()
                      var valueRange = max - min
                      var valuesStep = valueRange / range
                      var valueInSteps = (pixel.value.data / valuesStep).toInt
                      var hue = Color.BLUE.getHue() - valueInSteps

                      var color = Color.hsb(hue, 1.0, 0.8);

                      //var rectangle = uiGroup.getChildren.get(i).asInstanceOf[Rectangle]
                      r.setFill(color)

                  }
              }

            case _ =>
          }

          //-- Update colours
          message.pixels.zipWithIndex.foreach {
            case (pixel, i) =>

            /*if(pixel.value>0) {
                 println(s"Pixel ${pixel.x}:${pixel.y} -> " + pixel.value)
              }*/

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

  onJFXReadonlyDoubleChange(this.widthProperty()) {
    v =>
      updateScale
  }

  onJFXReadonlyDoubleChange(this.heightProperty()) {
    v =>
      updateScale
  }

  def updateScale = {

    var targetWidth = this.getWidth
    var targetHeight = this.getHeight
    
    var message = transformer.getResource[MatrixValueMessage] match {
      case Some(message) => 
        
        var pixelWidth = targetWidth / message.width
        var pixelHeight = targetWidth / message.height
        
        
        
        
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
}
*/*/

