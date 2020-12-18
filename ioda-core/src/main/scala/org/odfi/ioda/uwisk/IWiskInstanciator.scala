package org.odfi.ioda.uwisk

import org.odfi.ioda.pipelines.Pipeline
import org.odfi.ioda.uwisk.pipeline.WPipeline

trait IWiskInstanciator {

  def newInstance(cl:Class[_]) : WPipeline
  def newInstanceFromString(str:String) : WPipeline

}
