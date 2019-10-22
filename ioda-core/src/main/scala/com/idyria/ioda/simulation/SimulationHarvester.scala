package com.idyria.ioda.simulation

import org.odfi.indesign.core.harvest.Harvester
import com.idyria.ioda.simulation.data.protocols.SIMProtocolsProvider
import com.idyria.ioda.simulation.data.phy.SIMPhysicalInterface
import com.idyria.ioda.simulation.data.phy.SIMPhysicalInterfaceProvider

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