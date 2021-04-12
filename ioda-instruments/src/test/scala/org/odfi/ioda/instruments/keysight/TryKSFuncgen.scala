package org.odfi.ioda.instruments.keysight

import org.odfi.indesign.core.harvest.Harvest
import org.odfi.ioda.instruments.nivisa.VISAHarvester
import org.odfi.ioda.instruments.nivisa.keysight.KeysightHarvester
import org.odfi.ioda.instruments.nivisa.keysight.wavegen.KSTrueForm33200B
import org.odfi.ioda.instruments.nivisa.usb.{VISAUSBDevice, VISAUSBHarvester}

object TryKSFuncgen extends App {
  
  //Harvest.addHarvester(VISAHarvester)
  //Harvest.run
  VISAHarvester.harvest
  VISAHarvester.getResources.foreach {
    r => 
      println("R: "+r)
  }
  
  VISAUSBHarvester.getResources.foreach {
    r => 
      println("R: "+r.getId)
  }
  
  
  KeysightHarvester.getResources.foreach {
    r => 
      println("KS R:" +r.getId)
  }
  
  var wgen = KeysightHarvester.getResource[KSTrueForm33200B].get
  
  wgen.outputSin(1000000,0,2,90)
  
  
  
}