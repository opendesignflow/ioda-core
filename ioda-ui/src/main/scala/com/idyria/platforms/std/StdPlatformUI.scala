package com.idyria.platforms.std

/*
import org.odfi.wsb.fwapp.DefaultSite
import org.odfi.wsb.fwapp.jmx.FWAPPJMX
import com.idyria.platforms.std.devices.DevicesUI
import com.idyria.platforms.std.environment.EnvironmentUI
import com.idyria.platforms.std.simulation.SimulationUI
import com.idyria.platforms.std.simulation.devices.StdSimulationDevicesUI
import com.idyria.platforms.std.datapath.DataPathUI
import com.idyria.platforms.std.datasources.DataSourcesUI
import org.odfi.wsb.fwapp.Site
import com.idyria.platforms.std.statistics.GeneralStatisticsView

object StdPlatformUI extends Site("/ui")   {
  
  "/" view classOf[StdPlatformWelcome]
  
  "/environments" is {
    
    view(classOf[EnvironmentUI])
  
    "/datasources" is {
      view(classOf[DataSourcesUI])
    }
    
    "/datapaths" is {
      view(classOf[DataPathUI])
    }
    
    "/simulation/devices" is {
      view(classOf[StdSimulationDevicesUI])
    }
  }
  
  "/datapaths"  view(classOf[DataPathUI])
  
  "/devices" is {
    view(classOf[DevicesUI])
  }
  
  "/simulation" is {
    
    view (classOf[SimulationUI])
    
  }
  
  "/statistics" is {
    view(classOf[GeneralStatisticsView])
  }
  
  /*
  this.onLoad {
    //println("load STDUI")
    requireModule(StdPlatform)
    this.config.get.supportStringKey("ui.logo", "", "Logo Configuration")
    this.config.get.supportIntKey("ui.port", 8886, "UI Port")
    
  }
  this.onStart {
     this.listenWithJMXClose(this.config.get.supportGetInt("ui.port").get)
  }
  
 
  this.onInit {
    //this.config.get.supportStringKey("ui.logo", "", "Logo Configuration")
  }
  this.onShutdown {
    StdPlatform.moveToShutdown
  }*/
  
}*/