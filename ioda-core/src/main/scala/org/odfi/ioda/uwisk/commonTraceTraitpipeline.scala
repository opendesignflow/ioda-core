package org.odfi.ioda.uwisk

import org.odfi.ioda.data.protocols.MetadataContainer
import org.odfi.tea.timer.TeaTiming
import org.odfi.tea.timing.TimingSupport

class commonTraceTraitpipeline extends commonTraceTraitpipelineTrait with TimingSupport{

  var referencePipeline: wpackageTraitpipeline = _


  def traceParameters(m:MetadataContainer) = {

  }

  /**
   * Run action
   * @param cl
   */
  def runPipeline(cl: => Any) = {

    // Call
    try {
      val rtime = this.time {

        cl
      }
      this.runTimeOrCreate.total = rtime

      rtime

    } catch {
      case e : Throwable =>
        this.errorOrCreate.message = e.getLocalizedMessage
        throw e
    }


  }

}
