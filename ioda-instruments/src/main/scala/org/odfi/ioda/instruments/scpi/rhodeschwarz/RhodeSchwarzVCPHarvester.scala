package org.odfi.ioda.instruments.scpi.rhodeschwarz

import scala.sys.process._
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.instruments.scpi.serial.{SCPIVCPDevice, SCPIVCPHarvester}

import java.io.File
import scala.io.Source

/**
 * 0403:ed72
 */
object RhodeSchwarzVCPHarvester extends Harvester {

  
  this.onDeliverFor[SCPIVCPDevice] {
    case device => 
      
      
      // Determine if the VCP Device can be a RhodeSchwarz
      // Get product, should be HAMEG HO720 
      val usbProduct = Source.fromFile(new File(device.devicePath,"product"),"US-ASCII").mkString.trim
      
      println(s"Delivered Device: "+device+" -> "+usbProduct)
      usbProduct match {
        case "HAMEG HO720" => 
          
          //-- Get Serial
          val serial = Source.fromFile(new File(device.devicePath,"serial"),"US-ASCII").mkString.trim
          
          println(s"Ok for RS Device: "+device+" -> "+usbProduct)
          //-- Create Device
          var rsd = new RhodeSchwarzVCPDevice(device,serial)
          gather(rsd)
          true
          
        case _ => 
          false
      }
      
      
  }
  
  

}