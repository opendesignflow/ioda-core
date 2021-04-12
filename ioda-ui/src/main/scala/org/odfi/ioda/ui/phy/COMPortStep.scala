package org.odfi.ioda.ui.phy

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.{Button, ComboBox}
import javafx.scene.layout.HBox
import org.odfi.ioda.data.phy.state.{PhyClosedMessage, PhyOpenedMessage}
import org.odfi.ioda.instruments.serial.{COMPort, COMPortHarvester}
import org.odfi.ioda.ui.jfx.JavaFXUtilsTrait
import org.odfi.ioda.uwisk.pipeline.{WPipeline, WPipelineWithId}
import org.odfi.tea.thread.ThreadLanguage

/**
 * This Step can provide access to a comport
 */
class COMPortStep extends HBox with  WPipeline with ThreadLanguage with JavaFXUtilsTrait {


  val portBox = new ComboBox[COMPort]()
  val open = new Button("Open")
  open.setDisable(true)
  val close = new Button("Close")
  close.setDisable(true)
  close.setCancelButton(true)

  val refresh = new Button("Refresh")

  this.getChildren.addAll(portBox,open,close,refresh)

  // Status
  //----------
  val opened = new SimpleBooleanProperty(true)
  open.disableProperty().bindBidirectional(opened)
  val closed = new SimpleBooleanProperty(true)
  close.disableProperty().bindBidirectional(closed)

  //  Refresh
  //------------
  this.onJFXClickThread(refresh,Some("Loading")) {


    COMPortHarvester.harvest

    onJFXThread {
      // Deselect
      //portBox.getSelectionModel.clearSelection()
      COMPortHarvester.getResourcesOfType[COMPort].foreach {
        port =>

          if (!portBox.getItems.contains(port)) {
            portBox.getItems.add(port)
          }

      }

      // Autoselect
      if (portBox.getSelectionModel.isEmpty && ! portBox.getItems.isEmpty) {
        portBox.getSelectionModel.select(0)
      }
    }


  }

  // Select and open
  //------------
  this.onJFXReadOnlyObjectPropertyChanged(  this.portBox.getSelectionModel.selectedItemProperty()) {
    case null =>
      this.opened.set(false)
      this.closed.set(false)

    case port if (port.isOpened) =>
      this.opened.set(true)
      this.closed.set(false)
    case other =>
      this.opened.set(false)
      this.closed.set(true)

  }

  this.onJFXClick(open) {

    portBox.getSelectionModel.getSelectedItem match {
      case null =>
      case port if (port.isOpened) =>
        this.opened.set(true)
        this.closed.set(false)
      case port =>
        try {
          port.open
          port.clearReceivedLineInBuffer
          port.setLineIgnorePrefix("#")
          this.opened.set(true)
          this.closed.set(false)
        } catch {
          case e : Throwable =>
            e.printStackTrace()
            this.opened.set(false)
            this.closed.set(true)
        }


    }

  }

  onJFXClick(close) {
    portBox.getSelectionModel.getSelectedItem match {
      case null =>
      case other =>
        try {
          other.close

        } catch {
          case e : Throwable =>
            e.printStackTrace()
        } finally {
          this.opened.set(false)
          this.closed.set(true)
        }
    }
  }

  // Visibility
  //--------------
  onJFXBooleanPropertyChange(visibleProperty()) {
    v =>

      if (v) {
        println("Visible")
      }

  }

  // State and Reactions
  //---------

  /**
   * Send a Phy Open Message
   */
  onJFXBooleanPropertyChange(opened) {
    case true =>

      // Send on another thread!
      fork {
        val msg = new PhyOpenedMessage(this.portBox.getSelectionModel.getSelectedItem)
        this.down(msg)
      }


    case false =>
  }

  onJFXBooleanPropertyChange(closed) {
    case true =>

      // Send on another thread to avoid UI Freezing!
      fork {
        val msg = new PhyClosedMessage(this.portBox.getSelectionModel.getSelectedItem)
        this.down(msg)
      }


    case false =>
  }


}
