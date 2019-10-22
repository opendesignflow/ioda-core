package com.idyria.platforms.std.devices

import com.idyria.platforms.std.StdPlatformBaseView
import com.idyria.ioda.data.phy.PhysicalInterfaceHarvester
import com.idyria.ioda.data.phy.PhysicalInterface

class DevicesUI extends StdPlatformBaseView {
 
  definePage {
    
    h1("Devices") {
      
    }
    
    
    h1("Physical Interfaces") {
      
    }
    
    "ui table" :: table {
      thead("ID","Type","Display Name")
      
      tbodyTrLoopWithDefaultLine("No Devices")(PhysicalInterfaceHarvester.findResourcesOfType[PhysicalInterface]) {
        resource => 
          
          td(resource.getId) {
            
          }
          td(resource.getClass.getSimpleName) {
            
          }
          td(resource.getDisplayName) {
            
          }
      }
      
      
    }
    
    
  }
  
}