package org.odfi.ioda.pipelines

import org.odfi.ioda.data.protocols.{Protocol, ProtocolWithId}
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import org.odfi.indesign.core.config.model.CommonConfig
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId

@xelement(name = "pipeline", ns = "http:///www.opendesignflow.org/ioda/pipelines/1.0")
trait Pipeline extends Protocol with CommonConfig {

  
  @xattribute(name="environment")
  var _environment : XSDStringBuffer = null
  
  def environment = _environment match {
    case null => "production"
    case defined => defined.toString()
  }
  

  this.configModel = Some(this)

  /**
   * Returns true if this Pipeline environment definition ist contained in the target test envs
   */
  def matchesEnvironment(envs:List[String]) = {
    
    environment.split(",").toList.find(envs.contains(_)).isDefined
    
  }
  
  
 /* /**
   * Elements inside a Pipeline Are supposed to map to other pipelines or implementations
   */
  override def streamIn(du: DataUnit) = {

    if (inHierarchy && du.getElement() != null && du.isHierarchyClose == false) {

      // Get Element mapping
      Pipeline.getPipelineInstance(du.getElement().ns, du.getElement().name) match {
        case Some(pp) =>
          this <= pp
          this.withIOChain(pp) {
            pp <= du
          }
        case None =>
          logWarn("Could not import pipeline element: " + du.element.name+ " -> "+du.getElement().ns)
      }
      
    } else {
      super.streamIn(du)
    }

  }*/

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
  registerPipeline("http:///www.opendesignflow.org/ioda/pipelines/1.0", "pipeline", classOf[DefaultPipeline])

}