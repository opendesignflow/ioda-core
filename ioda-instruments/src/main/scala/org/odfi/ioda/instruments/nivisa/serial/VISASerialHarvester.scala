package org.odfi.ioda.instruments.nivisa.serial

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.instruments.nivisa.VISADevice
import org.odfi.ioda.instruments.nivisa.usb.VISAUSBDevice

object VISASerialHarvester extends Harvester {
  
 this.onDeliverFor[VISADevice] {
    case device if (device.isSerial) => 
      gather(new VISASerialDevice(device))
      true
  }
  
}
class VISASerialDevice(val d : VISADevice) extends VISADevice(d) {
  this.deriveFrom(d)
}