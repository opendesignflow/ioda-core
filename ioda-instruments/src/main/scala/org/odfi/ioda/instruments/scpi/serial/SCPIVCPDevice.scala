package org.odfi.ioda.instruments.scpi.serial

import org.odfi.ioda.instruments.scpi.SCPIDevice

class SCPIVCPDevice(val ttyName : String,val devicePath : String) extends SCPIDevice {
   
  def getId = devicePath
  
  // Configuration
  //var vaus
  
  // Opening
  
  
}