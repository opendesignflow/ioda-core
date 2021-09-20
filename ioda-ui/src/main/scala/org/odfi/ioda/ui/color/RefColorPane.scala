package org.odfi.ioda.ui.color

import javafx.beans.property.{SimpleObjectProperty, SimpleStringProperty}
import javafx.scene.canvas.Canvas
import javafx.scene.control.ComboBox
import javafx.scene.layout.{HBox, Pane, Priority}
import javafx.scene.paint.Color
import net.mahdilamb.colormap.{Colormap, Colormaps}
import org.controlsfx.control.SearchableComboBox
import org.odfi.ioda.ui.jfx.JavaFXUtilsTrait

import scala.jdk.CollectionConverters._

class RefColorPane extends HBox with JavaFXUtilsTrait {








  this.setFillHeight(true)

  // Create Selection Box
  //-----------
  val selBox = new ComboBox[String]()
  selBox.getItems.addAll( ColorsmapLister.listDefaultMaps())
  selBox.getSelectionModel.select("sequential.turbo")
  getChildren.add(selBox)


  /*val searchSelBox = new SearchableComboBox[String]()
  searchSelBox.getItems.addAll( ColorsmapLister.listDefaultMaps())
  searchSelBox.getSelectionModel.select("sequential.turbo")
  getChildren.add(searchSelBox)*/

  // Create Canvas
  //---------------
  val canvasPane = new Pane
  val canvas = new Canvas
  canvasPane.getChildren.add(canvas)
  getChildren.add(canvasPane)
  HBox.setHgrow(canvasPane,Priority.ALWAYS)


  canvasPane.widthProperty().addListener(e => canvas.setWidth(canvasPane.getWidth()))
  canvasPane.heightProperty().addListener(e => canvas.setHeight(canvasPane.getHeight()))


  // Selection
  //-----------
  var selectedMapProperty = new SimpleStringProperty("sequential.turbo")
  selectedMapProperty.bind(selBox.getSelectionModel.selectedItemProperty())

  // Update map object when selection changes
  var selectedMapObjectProperty = new SimpleObjectProperty[Colormap](Colormaps.get(selectedMapProperty.get()))
  onJFXPropertyChanged(selectedMapProperty) {
    mapName =>
      selectedMapObjectProperty.set(Colormaps.get(mapName))
  }

  def getMap = {
    selectedMapObjectProperty.get()
  }

  override def layoutChildren(): Unit = {
    super.layoutChildren()

    val positions = getMap.getDefinedPositions.size()

    val gc = canvas.getGraphicsContext2D

    // Draw each color
    val unitWidth = getWidth / positions

    println(s"*** LAYOUT ${canvas.getWidth}x${canvas.getHeight} ***")
    println(s"*** Colors: " + getMap.getDefinedPositions.size())
    println(s"*** Unit width: "+unitWidth)

    val height = getHeight

    getMap.getDefinedPositions.asScala.toList.zipWithIndex.foreach {
      case (colorPosition, i) =>

       // println("Position: "+colorPosition)
        val color = getMap.get(colorPosition)

        // Positions
        val x = Math.floor(i * unitWidth)
        val y = 0
        val width = Math.ceil(unitWidth)


       // gc.clearRect(x, y, width, height)
        val jfxColor = new Color(color.getRed, color.getGreen, color.getBlue, color.getAlpha)
        gc.setFill(jfxColor)
        /*if (i%2==0) {
          gc.setFill(Color.RED)
        } else {
          gc.setFill(Color.BLUE)
        }*/


        gc.fillRect(x, y, width, height)


    }
    // EOF Draw each color


  }


}
