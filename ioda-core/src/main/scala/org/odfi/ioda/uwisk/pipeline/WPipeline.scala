package org.odfi.ioda.uwisk.pipeline

import org.odfi.ioda.data.protocols.ProcessingContext
import org.odfi.ioda.data.types.{DataMapMessage, DataMessage}
import org.odfi.ioda.pipelines.{Pipeline, PipelineWithId}
import org.odfi.ubroker.core.message.Message
import javax.persistence.Transient

/**
 * Extends Pipeline to introduce processing context for each run
 */
trait WPipeline extends Pipeline {

  @Transient
  var processingContext: ProcessingContext = new ProcessingContext

  override def down(message: Message): Unit = {
    this.processingContext = new ProcessingContext
    super.down(message)
  }

  def downP(message: Message, p: ProcessingContext): Unit = {
    this.processingContext = p
    super.down(message)
  }


  def onWiskMessage(cl: PartialFunction[(DataMessage, ProcessingContext), Any]) = {
    this.onDownMessage {
      msg =>
        if (cl.isDefinedAt(msg,this.processingContext)) {
          cl(msg, this.processingContext)
        }

    }
  }


}

trait WPipelineWithId extends WPipeline with PipelineWithId
