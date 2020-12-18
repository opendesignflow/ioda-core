package com.idyria.platforms.std



import org.odfi.indesign.core.config.ooxoo.OOXOOConfigModule
import java.io.File
import org.odfi.indesign.core.module.IndesignModule

import org.odfi.ioda.IODA

object StdPlatform extends IndesignModule {

  var platformName  = "ioda-std-platform"
  var autoCleanPlatform = false
  def setName(str:String) = {
    platformName = platformName;
  }
  
  this.onLoad {
    
    autoCleanPlatform match {
      case true => 
        OOXOOConfigModule.setCleanConfigFolder(new File(platformName))
      case false =>
        OOXOOConfigModule.setConfigFolder(new File(platformName))
    }
    
    
    requireModule(IODA)
    requireModule(OOXOOConfigModule)
    //Harvest.addHarvester(PhysicalInterfaceHarvester)
  }
  
  this.onStart {
    
  }

  this.onShutdown {
    IODA.moveToShutdown
  }

}