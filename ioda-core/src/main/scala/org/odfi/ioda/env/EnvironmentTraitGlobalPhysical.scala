package org.odfi.ioda.env

class EnvironmentTraitGlobalPhysical extends EnvironmentTraitGlobalPhysicalTrait {
  
  def isConnected = implementationInstance.isDefined
  
}