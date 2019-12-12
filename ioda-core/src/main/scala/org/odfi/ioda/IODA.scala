package org.odfi.ioda

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.ioda.env.EnvironmentHarvester
import org.odfi.ioda.data.phy.PhysicalInterfaceHarvester
import com.idyria.osi.ooxoo.core.buffers.structural.AnyXList
import org.odfi.ioda.env.Environment

object IODA extends IndesignModule {
  
  AnyXList(classOf[Environment])
  
  this.onLoad {
    Harvest.addHarvester(EnvironmentHarvester)
    Harvest.addHarvester(PhysicalInterfaceHarvester)
  }
  
  
  this.onShutdown {
    println(s"Saving config...")
    saveConfig
  }
  
  // Env
  //--------------
  def getEnvironments = config match {
    case Some(c) => c.custom.content.getAllOfType[Environment]
    case None => List()
  }
  
  
  def addEnvironment(name:String) = {
    
    getEnvironments.find {
      ev => 
        ev.name.toString == name
    } match {
      case Some(ev) => ev
      case None if (this.config.isDefined) => 
        val env = this.config.get.custom.addContentOfType[Environment]
        env.name = name
        env
      case None => 
        // NO config
        val env = new Environment()
        env.name = name
        env
        
    }
  }
 
  /**
   * Environment is created if not existing
   */
  def onEnvironment[T](name:String)(cl: Environment => T) : T = {
    cl(addEnvironment(name))
  }
  
  
}