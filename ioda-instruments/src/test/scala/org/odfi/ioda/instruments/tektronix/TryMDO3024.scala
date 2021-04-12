package org.odfi.ioda.instruments.tektronix

import org.odfi.ioda.instruments.nivisa.VISAHarvester
import org.odfi.ioda.instruments.nivisa.tektronix.{MDO3024106, TektronixHarvester}
import org.odfi.ioda.instruments.nivisa.usb.{VISAUSBDevice, VISAUSBHarvester}

object TryMDO3024 extends App {
  
  VISAHarvester.harvest
  
  VISAUSBHarvester.getResourcesOfType[VISAUSBDevice].foreach {
    r => 
      println("R: "+r.getId+"->"+r.getModelID)
  }
  
  TektronixHarvester.getResources.foreach {
    r => 
      println("R: "+r.getId)
  }
  
  val osci = TektronixHarvester.getResource[MDO3024106].get
  
  osci.selectChannel(1)
  osci.getWaveform
 
  
  
}