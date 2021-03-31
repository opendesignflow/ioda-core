package org.odfi.ioda.ui.jfx.schedule

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.{Button, Label, Spinner}
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.layout.HBox
import org.odfi.ioda.pipelines.ScheduleCollectPipelineTrait
import org.odfi.ioda.ui.jfx.JavaFXUtilsTrait

class ScheduleCollectHBox(val pipeline : ScheduleCollectPipelineTrait) extends HBox with JavaFXUtilsTrait {

  this.setFillHeight(true)
  this.setAlignment(Pos.CENTER_LEFT)

  // State
  var running = new SimpleBooleanProperty(false)
  var stopped = new SimpleBooleanProperty(true)

  // Time in ms
  val timeSpinner = new Spinner[Integer]()
  timeSpinner.setValueFactory(new IntegerSpinnerValueFactory(0,4096))
  timeSpinner.getValueFactory.setValue(1000)
  timeSpinner.disableProperty().bindBidirectional(running)
  timeSpinner.setEditable(true)

  // Start - stop
  val start = new Button("Start")
  start.disableProperty().bindBidirectional(running)

  val stop = new Button("Stop")
  stop.disableProperty().bindBidirectional(stopped)


  this.getChildren.addAll(new Label("Schedule every ms: "),timeSpinner,start,stop)

  // Actions
  onJFXClick(start) {
    try {
      this.pipeline.scheduleCollect(timeSpinner.getValue.toLong)
      running.set(true)
      stopped.set(false)
    } catch {
      case e : Throwable =>
        e.printStackTrace()
        running.set(false)
        stopped.set(true)
    }

  }

  onJFXClickThread(stop,Some("Stopping")) {
    this.pipeline.killWait
    onJFXThreadLater {
      running.set(false)
      stopped.set(true)
    }

  }


}
