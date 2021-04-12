package org.odfi.ioda.instruments.scpi.rhodeschwarz

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.ioda.instruments.scpi.serial.SCPIVCPDevice

class RhodeSchwarzVCPDevice(val baseDevice : SCPIVCPDevice,val serial:String) extends HarvestedResource {
  
  this.deriveFrom(baseDevice)
  
  def getId = baseDevice.getId
}