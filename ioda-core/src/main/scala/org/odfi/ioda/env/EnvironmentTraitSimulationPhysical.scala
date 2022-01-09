package org.odfi.ioda.env

import org.odfi.ooxoo.core.buffers.structural.xelement
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterface
import org.odfi.tea.errors.TImmediateError
import org.odfi.ioda.data.phy.PhysicalInterface


/**
 * After Readin from config, initiate the implementation
 */
class EnvironmentTraitSimulationTraitPhysical extends EnvironmentTraitSimulationTraitPhysicalTrait  {
  
  // Post read
  //---------------
  override def postStreamIn = {
    super.postStreamIn
    
    repairErrors
  }
  

  
  // Checks
  //-------------------
  
  override def checkImmediateErrors = {
    super.checkImmediateErrors
    if (this.implementationInstance.isEmpty ) {
      addError(new TImmediateError("Implementation Not Instantiated"))
    }
    
  }
  
    override def repairErrors = {
    

    ensureInstance
    
  }
 
  
}