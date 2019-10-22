package com.idyria.ioda.env

class EnvironmentTraitGlobalPhysical extends EnvironmentTraitGlobalPhysicalTrait {
  
  def isConnected = implementationInstance.isDefined
  
}