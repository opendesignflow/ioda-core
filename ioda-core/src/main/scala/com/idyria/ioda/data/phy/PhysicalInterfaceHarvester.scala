package com.idyria.ioda.data.phy

import org.odfi.indesign.core.harvest.Harvester
import com.idyria.ioda.data.phy.comport.COMPortHarvester
import com.idyria.ioda.IODA
import com.idyria.ioda.env.EnvironmentHarvester
import com.idyria.ioda.env.Environment

object PhysicalInterfaceHarvester extends Harvester {
  
  
  this --> COMPortHarvester
 
  
  

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