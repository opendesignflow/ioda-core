package org.odfi.ioda.instruments.nivisa

import org.bridj.Pointer
import org.bridj.CLong
import org.bridj.Platform
import org.bridj.BridJ
import org.bridj.PlatformSupport
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.instruments.nivisa.keysight.{KeysightDevice, KeysightOsci}
import org.odfi.ioda.instruments.nivisa.tektronix.{TekTronixDevice, TekTronixOsci, TektronixHarvester}

object TryVISA extends App {

  //BridJ.get
  
  
  
  //Platform.addEmbeddedLibraryResourceRoot("lib")
  BridJ.addLibraryPath("/usr/local/lib64")
  println(s"Welcome to VISA Try -> "+BridJ.getNativeLibraryFile("nivisa64"))

  //--
  var h = VISAHarvester
  h.harvest
  
 h.onResources[VISADevice] {
    d => 
      println(s"Found VISA Device"+d+" -> "+d.getId)
  }
  
  /*

  h.childHarvesters.last.asInstanceOf[Harvester[VISADevice,KeysightDevice]].getResources.foreach {
    case r: TekTronixOsci =>

      println(s"Found device: " + r.deviceString + " -> " + r.getDeviceId)

      
      
      //-- Get Waveform
      //var c = r.getCurve
      //println(s""+c.toList.map(ca => ca.toChar))
      
      //-- Save image
      r.write("SAVe:IMAGe:FILEFormat PNG")
      r.write("SAVe:IMAGe \"E:/scr.png\"")
      
      //-- Get Image
      var bytesPNG = r.readBytes("FILESystem:READFile \"E:/scr.png\"")
      
      var oF = new FileOutputStream(new File("o.png"))
      oF.write(bytesPNG)
      oF.flush()
      oF.close()
      
      
      
    case r: KeysightOsci =>
      
     /* var screen = r.getScreen
      
      var oF = new FileOutputStream(new File("ok.png"))
      oF.write(screen)
      oF.flush()
      oF.close()*/
      

  }*/
  println(s"")

  VISA.close
  sys.exit()

  // Create RM Pointer

  var defaultRM = Pointer.allocateCLong()
  var res = VisaLibrary.viOpenDefaultRM(defaultRM)

  println(s"Default RM Result: $res -> ${defaultRM.getLong}")

  //-- get List
  //---------------
  var numInstr = Pointer.allocateCLong()
  var instrsList = Pointer.allocateCLong()
  var resourceString = Pointer.allocateBytes(VisaLibrary.VI_FIND_BUFLEN)

  println(s"Listing res before: ${numInstr.getLong}")
  res = VisaLibrary.viFindRsrc(defaultRM.getLong, Pointer.pointerToCString("USB?*INSTR"), instrsList, numInstr, resourceString)

  //VisaLibrary.vie
  println(s"Listing res: $res -> ${numInstr.getLong} -> ${resourceString.getCString}")

  numInstr.getLong match {
    case 0 =>
    case total =>

      (0 until total.toInt) foreach {
        i =>

          //-- Prepare Session or isntrument
          var instrumentSession = Pointer.allocateCLong()

          //-- Open
          VisaLibrary.viOpen(defaultRM.getLong, resourceString, VisaLibrary.VI_NULL, VisaLibrary.VI_NULL, instrumentSession) match {
            case 0 =>
              println(s"Opened Device: ${resourceString.getCString}")
            case other =>

          }

          //println(s"Getting Resource $i")

          //-- Go to next
          VisaLibrary.viFindNext(instrsList.getLong, resourceString);
      }

  }

  VisaLibrary.viClose(defaultRM.getLong)

}