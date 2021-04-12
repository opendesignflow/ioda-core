package org.odfi.ioda.instruments.nivisa.tektronix

import org.odfi.ioda.instruments.data.XWaveform
import org.odfi.ioda.instruments.ieee.IEEE4882BinaryBlock
import org.odfi.ioda.instruments.nivisa.usb.VISAUSBDevice

import java.io.File

class MDO3024106 (baseDevice: VISAUSBDevice) extends TekTronixOsci(baseDevice) {
  
  
   
  
  
  def getJPEGHardcopy = {
    this.write("HARDCopy:FORMat JPEG")
    this.write("HARDCopy:LAYout LANdscape")
    this.write("HARDCopy:PORT USB")
    var image = this.readBytes("HARDCopy:START")
    var out = new File("target/test-out")
    out.mkdirs


  }
}