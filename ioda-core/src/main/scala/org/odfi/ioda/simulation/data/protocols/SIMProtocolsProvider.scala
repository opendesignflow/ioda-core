package org.odfi.ioda.simulation.data.protocols

import org.odfi.ioda.simulation.SimulationProvider
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterfaceProvider
import org.odfi.ioda.simulation.data.protocols.text.SIMValuesListPhy

object SIMProtocolsProvider extends SimulationProvider with SIMPhysicalInterfaceProvider {
   
  def listSimulationObjects = List(
      
      ("protocols.text.valueslist","Values List",classOf[SIMValuesListPhy])
      
      );
  
}