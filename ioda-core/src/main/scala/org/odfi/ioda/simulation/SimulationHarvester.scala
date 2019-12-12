package org.odfi.ioda.simulation

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.simulation.data.protocols.SIMProtocolsProvider
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterface
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterfaceProvider

object SimulationHarvester extends Harvester {
  
  gatherPermanent(SIMProtocolsProvider)
  
  // Physical
  //---------------
  
  /**
   * Return PhysicalInterface providers
   */
  def getProvidedPhysicalInterfaces = this.getResourcesOfType[SIMPhysicalInterfaceProvider].map {
    provider => 
      provider.listSimulationObjects
  }.flatten
  
  
  
  
  
  
}