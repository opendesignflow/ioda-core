package org.odfi.ioda.instruments.nivisa

import org.odfi.ioda.instruments.nivisa.serial.{VISASerialDevice, VISASerialHarvester}

object VISASerial extends App {
  
    var h = VISAHarvester
  h.harvest
  
  VISASerialHarvester.getResources.foreach {
      r => 
        
    }
  
  
  VISASerialHarvester.getResource[VISASerialDevice] match {
      case Some(firstSerial) =>
        println("FOund serial: "+firstSerial.getId)
        
        firstSerial.open
        
        //firstSerial.write("test")
        
        var received = firstSerial.readString("test")
        
        println("REceived: "+received)
        
        firstSerial.close
      case None => 
    }
  
  
}