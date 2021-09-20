package org.odfi.ioda.uwisk.pipeline


object PipelinesRegistry {


  // Register
  //--------------
  var steps: Map[String, Class[_ <: WPipeline]] = Map()

  def registerStep(id: String, cl: Class[_ <: WPipeline]) = {
    this.synchronized {
      this.steps = this.steps + (id -> cl)
    }
  }

  // Load
  //-----------------

  def loadPipeline(str: String) = {

    steps.getOrElse(str, sys.error(s"Pipeline with id $str is not defined")).getDeclaredConstructor().newInstance().asInstanceOf[WPipeline]

  }


}
