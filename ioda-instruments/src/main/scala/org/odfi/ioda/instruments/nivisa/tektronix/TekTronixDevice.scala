package org.odfi.ioda.instruments.nivisa.tektronix

import org.odfi.ioda.instruments.nivisa.VISADevice

class TekTronixDevice(val baseDevice : VISADevice) extends VISADevice(baseDevice) {
  deriveFrom(baseDevice)
  
}