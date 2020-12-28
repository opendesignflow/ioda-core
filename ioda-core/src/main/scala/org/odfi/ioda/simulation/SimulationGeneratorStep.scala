package org.odfi.ioda.simulation

import org.odfi.indesign.core.heart.{Heart, HeartTask}
import org.odfi.ioda.pipelines.PipelineWithId
import org.odfi.ioda.uwisk.pipeline.WPipeline

trait SimulationGeneratorStep extends WPipeline with PipelineWithId with HeartTask[Any] {

  var scheduleMS = 1000

  def scheduleTask = {

    this.scheduleEvery = Some(scheduleMS)

    Heart.pump(this)
  }

  def stopTask = {
    Heart.killTask(this)
  }

}
