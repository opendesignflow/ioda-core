package com.idyria.ioda.simulation.data.protocols

import com.idyria.ioda.simulation.SimulationProvider
import com.idyria.ioda.simulation.data.phy.SIMPhysicalInterfaceProvider
import com.idyria.ioda.simulation.data.protocols.text.SIMValuesListPhy

object SIMProtocolsProvider extends SimulationProvider with SIMPhysicalInterfaceProvider {
   
  def listSimulationObjects = List(
      
      ("protocols.text.valueslist","Values List",classOf[SIMValuesListPhy])
      
      );
  
}