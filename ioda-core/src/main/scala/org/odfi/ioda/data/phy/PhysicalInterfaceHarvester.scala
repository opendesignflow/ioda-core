package org.odfi.ioda.data.phy

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.IODA
import org.odfi.ioda.env.EnvironmentHarvester
import org.odfi.ioda.env.Environment

object PhysicalInterfaceHarvester extends Harvester {


  def findPhysicalInterfaces = PhysicalInterfaceHarvester.findResourcesOfType[PhysicalInterface]
  
  def listPhysicalInterfacesInfos = findPhysicalInterfaces.map(i => (i.getId, i.getDisplayName))
  def listUnusedGlobalPhysicalInterfacesInfos = getUnusedGlobalPhysicalDevices.map(i => (i.getId, i.getDisplayName))
  
   /**
   * Lookup all physical interfaces which are not requested by an environment
   */
  def getUnusedGlobalPhysicalDevices = {
    
    this.findPhysicalInterfaces.filterNot {
      interface => 
       EnvironmentHarvester.getResourcesOfType[Environment].find {
         env => 
           env.usesGlobalPhysicalInterface(interface)
       }.isDefined
    }
    
  }
  
  def getPhysicalDevices = {
    
    this.findPhysicalInterfaces
    
  }
  
}