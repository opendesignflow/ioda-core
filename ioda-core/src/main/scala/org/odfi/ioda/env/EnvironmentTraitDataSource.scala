package org.odfi.ioda.env

import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.data.phy.PushPhysicalInterface

class EnvironmentTraitDataSource extends EnvironmentTraitDataSourceTrait {
  
  
  def usesGlobalPhysicalInterface(interface:PhysicalInterface) = {
    this.physicals.globalPhysicals.find {
      gf => 
        gf.references(interface.getId)
    }.isDefined
  }
  
  /**
   * Search Global Physical with an Instance
   * Search 
   */
  def selectPhy = {
    
    
    // Search in Globals
    this.physicals.globalPhysicals.collect {
      case gb if (gb.getReferencedBuffer.isDefined 
          &&  gb.getReferencedBuffer.get.isConnected 
          && gb.getReferencedBuffer.get.implementationInstance.isDefined
          && !gb.getReferencedBuffer.get.implementationInstance.get.isInstanceOf[PushPhysicalInterface]
          && gb.getReferencedBuffer.get.implementationInstance.get.isOpenedNoErrors) =>
         gb.getReferencedBuffer.get.implementationInstance.get
    } match {
      // Found Global
      case globals if (globals.size>0) => globals.toList
      
      // Search for fallback simulation
      case noGlobals => 
        
        this.physicals.simulationPhysicals.collect {
          case sf  if(sf.fallback.toBool && sf.implementationInstance.isDefined) => sf.getImplementation
        }.toList
        
    }
    
  }
  
  
 /* override def checkImmediateErrors = {
    super.checkImmediateErrors
    this.physicalOption match {
      case None => 
        addImmediateError("No Physical Defined")
      case Some(physical) if (physical.refIdOption.isEmpty && physical.simulationRefIdOption.isEmpty) => 
        addImmediateError("Physical Defined but no reference to sim/real physical")
    }
    
  }
  
  override def toString = {
    s"Board $boardId ($name)"
  }
  
  def getPhy = {
    this.physicalOption match {
      case Some(p) if (p.refId!=null) => 
        this.parentReference.get.getPhysicalInterfaceById(p.refId) match {
          case None if (p.simulationRefId!=null) =>
            this.parentReference.get.getPhysicalInterfaceById(p.simulationRefId) 
          case None => None
          case Some(p) => Some(p)
        }
       
      case None => 
        None
    }
  }*/
  
  
}