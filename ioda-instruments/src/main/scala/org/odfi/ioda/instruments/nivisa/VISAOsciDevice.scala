package org.odfi.ioda.instruments.nivisa

import org.odfi.ioda.instruments.osci.OSCIDevice

abstract class VISAOsciDevice(val baseDevice: VISADevice) extends VISADevice(baseDevice) with OSCIDevice {
  deriveFrom(baseDevice)
  
  
  
  
}