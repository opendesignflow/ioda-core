package org.odfi.ioda.uwisk

trait commonTrace extends commonTraceTrait {

  def addPipelineTrace(pipeline:wpackageTraitpipeline) = {

    // Only add if the last added trace lement is not the same pipelein
    this.pipelinesAsScala.lastOption match {
      case Some(last) if(last.referencePipeline!=null && last.referencePipeline==pipeline) =>
        last
      case other =>
        val p = this.addPipeline
        p.id = pipeline.getAbsoluteName
        p.referencePipeline = pipeline
        p
    }

  }

}
