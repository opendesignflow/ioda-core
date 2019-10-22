package com.idyria.platforms.std.simulation

import com.idyria.platforms.std.StdPlatformBaseView
import com.idyria.ioda.simulation.SimulationHarvester
import com.idyria.ioda.simulation.data.phy.SIMPhysicalInterfaceProvider

class SimulationUI extends StdPlatformBaseView {
  
  definePage {
    
    h1("Physical Devices") {
      
    }
    
    semanticDefinitionTable("ID","Description","Class") {
      
      tbodyTrLoopWithDefaultLine("No Physical Simulation Devices Available")(SimulationHarvester.getProvidedPhysicalInterfaces) {
        case  (id,desc,cl) => 
          rtd{
            
          }
          td(id) {
            
          }
          td(desc) {
            
          }
          td(cl.getCanonicalName) {
            
          }
          
      }
      
    }
    
  }
  
}