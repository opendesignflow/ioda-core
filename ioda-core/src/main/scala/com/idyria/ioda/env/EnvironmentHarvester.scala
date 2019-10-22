package com.idyria.ioda.env

import org.odfi.indesign.core.harvest.Harvester
import com.idyria.ioda.IODA
import com.idyria.osi.ooxoo.core.buffers.structural.xelement

object EnvironmentHarvester extends Harvester {
  
  
  override def doHarvest = {
    
    IODA.config.get.custom.content.getAllOfType[Environment].foreach {
      env => 
        gather(env)
    }
    
    
    
  }
  
  
  def addEnvironment(name:String) = {
    getResourceById[Environment](name) match {
      case None => 
        
        IODA.addEnvironment(name)
      
        
        this.harvest
        
        //saveToAvailableResources(env)
        
        
        
      case Some(_) => sys.error("Cannot add environment, already exists")
    }
  }
  
  
}

