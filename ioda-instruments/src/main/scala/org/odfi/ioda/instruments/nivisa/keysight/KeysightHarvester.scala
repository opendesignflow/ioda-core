package org.odfi.ioda.instruments.nivisa.keysight

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.instruments.nivisa.VISADevice
import org.odfi.ioda.instruments.nivisa.keysight.wavegen.KSTrueForm33200B
import org.odfi.ioda.instruments.nivisa.usb.VISAUSBDevice

object KeysightHarvester extends Harvester {

  this.onDeliverFor[VISAUSBDevice] {

    // One vendor ID for Keysight
    case r if (r.getVendorID == "0x0957") =>
      //println(s"Keysight H delivered device")

      //-- Device Map
      r.getProductID match {

        // Oscilloscopes
        //---------------------
        case "0x1796" =>
          gather(new KSDSOX2024A(r))
          true
        case "0x179B" =>
          gather(new KSDSOX2002A(r))
          true

        // Function generator
        //----------------
        case "0x2C07" =>

          gather(new KSTrueForm33200B(r))
          true
        case other =>
          false
      }

    // Another vendor ID for Keysight
    case r if (r.getVendorID == "0x2A8D") =>

      //-- Device Map
      r.getProductID match {
        case "0x1772" =>
          gather(new KMSOX3054T(r))
          true
        case other =>
          false
      }

    case other =>
      println("KS got another usb device")
      false

  }

}