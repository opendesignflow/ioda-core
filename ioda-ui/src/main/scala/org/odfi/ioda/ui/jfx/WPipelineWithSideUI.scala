package org.odfi.ioda.ui.jfx

import javafx.scene.Node
import org.odfi.ioda.uwisk.pipeline.WPipeline

trait WPipelineWithSideUI extends WPipeline with JavaFXUtilsTrait {


  def loadSideUI : Node = {
    null
  }

}
