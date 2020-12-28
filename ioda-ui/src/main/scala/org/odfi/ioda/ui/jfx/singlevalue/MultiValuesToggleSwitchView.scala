package org.odfi.ioda.ui.jfx.singlevalue

import javafx.scene.layout.VBox
import org.odfi.ioda.data.types.IntValueMessage
import org.odfi.ioda.uwisk.pipeline.WPipeline

class MultiValuesToggleSwitchView extends VBox with WPipeline {

  var viewsForVCID = Map[String, SingleValueToggleSwitchView]()

  /*this.onDownMessage {
    msg =>
      println("Got Message")
  }*/

  this.onWiskMessage {
    case (svm: IntValueMessage, ctx) =>

      svm.processingContext.logInfo(s"Got value for ${svm.getVirtualChannelAsString}: ${svm.value.get}")
      ensureSingleSwitchForVCID(svm.getVirtualChannelAsString)
  }

  def ensureSingleSwitchForVCID(name: String) = {
    this.viewsForVCID.get(name) match {
      case Some(ui) =>
      case None =>
        // Create UI
        val ui = new SingleValueToggleSwitchView
        ui.acceptVCID(name)
        this.viewsForVCID = this.viewsForVCID + (name -> ui)

        // Add to Pipeline
        this <= ui

        // Add to UI
        this.getChildren.add(ui)

    }
  }

}
