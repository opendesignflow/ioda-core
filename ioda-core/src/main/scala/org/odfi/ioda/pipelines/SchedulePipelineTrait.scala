package org.odfi.ioda.pipelines

import org.odfi.indesign.core.heart.HeartTask

trait ScheduleCollectPipelineTrait extends HeartTask[Any] with Pipeline {


  /**
   * Runs on schedule, to be overwritten
   */
  def collect : Unit

  // Scheduling
  //-------------
  override def doTask: Any =  {
    //println("Collecting")
    this.collect
  }
  def scheduleCollect(delayMS:Long) = {
    this.scheduleAfter = Some(delayMS)
    this.reschedule
    //Heart.pump(this)
  }
}
