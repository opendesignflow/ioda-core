package org.odfi.ioda.env

import org.odfi.ooxoo.core.buffers.datatypes.DateTimeBuffer

class EnvironmentTraitDataPathTraitRunTraitExecution extends EnvironmentTraitDataPathTraitRunTraitExecutionTrait {

  def collectData = {
    this.parentReference.get.collectData
  }

  def startNow = this.start = DateTimeBuffer()
  def stopNow = this.stop = DateTimeBuffer()

  /**
   *
   */
  def startThenStop(cl: => Any) = {
    this.start = DateTimeBuffer()
    try {
      cl
    } finally {
      this.stop = DateTimeBuffer()
    }
  }

}