package org.odfi.ioda.instruments.nivisa.tektronix

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.instruments.nivisa.VISADevice
import org.odfi.ioda.instruments.nivisa.usb.VISAUSBDevice
import org.odfi.ioda.instruments.osci.OSCIDeviceHarvester

object TektronixHarvester extends Harvester {

  this --> OSCIDeviceHarvester
  
  this.onDeliverFor[VISAUSBDevice] {
    case r if (r.getVendorID=="0x0699") =>


     // println(s"TEK H delivered SUB device: "+r.getVendorID)
      
      r.getModelID match {
        case "C100876" =>
          gather(new TDS2002B(r))
          true
        case "C024106" =>
          gather(new MDO3024106(r))
          true
        case "C011774" => 
           gather(new C011774(r))
          true
        case other =>
          false
      }
      
      /*r.getDeviceId match {
        case id if (id.startsWith("TEKTRONIX") && id.contains("MDO3024")) =>
          gather(new TekTronixOsci(r).deriveFrom(r))
          true
        case o =>

          println(s"TEK H delivered device, not a TEK device: " + o)
          false
      }*/
  }
}
