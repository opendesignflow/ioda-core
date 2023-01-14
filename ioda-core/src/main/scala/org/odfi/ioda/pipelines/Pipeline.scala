package org.odfi.ioda.pipelines

import org.odfi.ioda.data.protocols.{Protocol, ProtocolWithId}
import org.odfi.ooxoo.core.buffers.structural.xelement
import org.odfi.indesign.core.config.model.CommonConfig
import org.odfi.ooxoo.core.buffers.structural.DataUnit
import org.odfi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import org.odfi.ooxoo.core.buffers.structural.xattribute
import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId

trait Pipeline extends Protocol with CommonConfig {

  @xattribute(name="environment")
  var _environment : XSDStringBuffer = null
  
  def environment = _environment match {
    case null => "production"
    case defined => defined.toString()
  }


  /**
   * Returns true if this Pipeline environment definition ist contained in the target test envs
   */
  def matchesEnvironment(envs:List[String]) = {
    
    environment.split(",").toList.exists(envs.contains(_))
    
  }

}
trait PipelineWithId extends Pipeline with HarvestedResourceDefaultId
class DefaultPipeline extends Pipeline with HarvestedResourceDefaultId {

}
object Pipeline {

  var elementsMap = Map[(String, String), Class[_ <: Pipeline]]()

  def registerPipeline(ns: String, name: String, cl: Class[_ <: Pipeline]) = {
    this.elementsMap = this.elementsMap + ((ns, name) -> cl)
  }

  def getPipelineInstance(ns: String, name: String) = {
    this.elementsMap.find {
      case ((sns, sname),cl) =>
        println(s"Searching against: "+sns+" -> "+sname)
        (ns == sns && name == sname)
    } match {
      case Some(cl) => Some(cl._2.getDeclaredConstructor().newInstance())
      case other => None
    }
  }

  // Defaults
  registerPipeline("http:///repo.opendesignflow.org/ioda/pipelines/1.0", "pipeline", classOf[DefaultPipeline])

}