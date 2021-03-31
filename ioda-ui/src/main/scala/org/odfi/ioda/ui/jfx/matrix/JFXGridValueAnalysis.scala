package org.odfi.ioda.ui.jfx.matrix

import javafx.animation.FadeTransition
import javafx.scene.control.Label
import javafx.scene.layout.{GridPane, Pane, Priority, StackPane, VBox}
import javafx.util.Duration
import org.odfi.ioda.data.types.IntValuesMessage
import org.odfi.ioda.ui.jfx.JavaFXUtilsTrait
import org.odfi.ioda.uwisk.pipeline.{WPipeline, WPipelineWithId}

/**
 * Class to Make a grid of values with analys
 */
class JFXGridValueAnalysis extends VBox with WPipeline with JavaFXUtilsTrait {


  // Size
  //----------
  var rowsCount = 2
  var columnsCount = 2

  // Grid Pane for blocks
  // - Add to parent VBox and let grow
  //---------

  var blockPanes = new GridPane
  blockPanes.setGridLinesVisible(true)
  this.getChildren.add(blockPanes)
  VBox.setVgrow(blockPanes, Priority.ALWAYS)

  this.setStyle("-fx-background-color:blue")

  // Single Element Node
  //--------------
  class SubBlock extends StackPane {

    val textLabel = new Label("Hello")
    this.getChildren.add(textLabel)

    /*onJFXBooleanPropertyChange(this.visibleProperty()) {
      case true =>
        val t = new FadeTransition
        t.setNode(textLabel)
        t.setFromValue(1.0)
        t.setToValue(0.0)
        t.setDuration(new Duration(500))
        t.playFromStart()
      case false =>
    }*/

    onJFXMouseEvent(this.onMouseEnteredProperty()) {
      ev =>
        println("Entered")
        val t = new FadeTransition
        t.setNode(textLabel)
        t.setFromValue(0.0)
        t.setToValue(1.0)
        t.setDuration(new Duration(500))
        t.playFromStart()
    }
    onJFXMouseEvent(this.onMouseExitedProperty()) {
      ev =>
        println("Exited")
        val t = new FadeTransition
        t.setNode(textLabel)
        t.setFromValue(1.0)
        t.setToValue(0.0)
        t.setDuration(new Duration(500))
        t.playFromStart()
    }

  }

  /**
   * Rebuild the UI
   */
  def rebuildUI = {

    // Clear
    blockPanes.getChildren.clear()

    // Grid
    //----------
    (0 until rowsCount).foreach {
      y =>
        (0 until columnsCount).foreach {
          x =>

            // Block
            val b = new SubBlock
            blockPanes.add(b, x, y)
            GridPane.setVgrow(b, Priority.ALWAYS)
            GridPane.setHgrow(b, Priority.ALWAYS)

        }
    }

  }

  this.onWiskMessage {
    case (m: IntValuesMessage, _) =>

      println("Received value: " + m.values)
      m.values.zipWithIndex.foreach {
        case (v, i) =>
          val sb = blockPanes.getChildren.get(i).asInstanceOf[SubBlock]
          sb.textLabel.setText(v.toString)
      }
  }


}
