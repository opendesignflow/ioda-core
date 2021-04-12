package org.odfi.ioda.instruments.nivisa

import org.bridj.BridJ
import org.bridj.Pointer
import org.odfi.tea.os.OSDetector
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.instruments.nivisa.VisaLibrary
import org.odfi.ioda.instruments.nivisa.keysight.KeysightHarvester
import org.odfi.ioda.instruments.nivisa.serial.{VISASerialDevice, VISASerialHarvester}
import org.odfi.ioda.instruments.nivisa.tektronix.TektronixHarvester
import org.odfi.ioda.instruments.nivisa.usb.VISAUSBHarvester

object VISAHarvester extends Harvester { 

  var resourceManagerHandler: Option[Long] = None

  /*this.onState("init") {

    VISA

  }*/

  // Use NI VISA 64 on windows
  //------------
  OSDetector.isWindows() match {
    case true =>
      BridJ.setNativeLibraryActualName("visa", "nivisa64")
      //BridJ.setNativeLibraryActualName("visa", "visa64")
    case false =>
  }

  // Default Children
  //------------------
  this.addChildHarvester(VISAUSBHarvester)
  this.addChildHarvester(VISASerialHarvester)
 // this.addChildHarvester(TektronixHarvester)
  //this.addChildHarvester(KeysightHarvester)

  // Harvest
  //---------------
  override def doHarvest = {

   // println(s"Harvesting Devices")
    //throw new RuntimeException("Test")

    //-- Get List of devices
    //-------------------------
    var numInstr = Pointer.allocateCLong() // Number of instruments
    var instrsList = Pointer.allocateCLong() // Instrument list used to loop
    var resourceString = Pointer.allocateBytes(VisaLibrary.VI_FIND_BUFLEN) // Resource String used when Finding devices

    //println(s"Listing res before: ${numInstr.getLong}")
    //Pointer.pointerToCString("USB?*INSTR")
    VisaLibrary.viFindRsrc(VISA.getResourceManagerHandler, Pointer.pointerToCString("?*INSTR"), instrsList, numInstr, resourceString) match {

      //-- Devices available
      case VisaLibrary.VI_SUCCESS =>

        // Go Through List
        //--------------------------
        (0 until numInstr.getInt) foreach {
          i =>

            //-- Create Device
            var device = new VISADevice(resourceString.getCString)

            //println("Found Device, doing openclose")
            //device.open
          //  device.close
            
            //-- Store and deliver it
            this.gather(device)


            //-- Go to next
            VisaLibrary.viFindNext(instrsList.getInt, resourceString);
        }

      //-- No Devices
      case VisaLibrary.VI_ERROR_RSRC_NFOUND =>

      //-- No Devices
      case 3221159953L =>

      //-- Other errors
      case other =>
        throw new RuntimeException(s"An Error Occured while listing devices code=${other.toHexString}, message=${VISA.getStatusDesc(other)}")
    }

  }

}

object VISA {

  var resourceManagerHandler: Option[Long] = None

  def getResourceManagerHandler = this.resourceManagerHandler match {
    case None =>

      //-- Get RM 
      var defaultRM = Pointer.allocateCLong()

      //println(s"Default RM Init: $res -> ${defaultRM.get}")
      VisaLibrary.viOpenDefaultRM(defaultRM) match {
        case VisaLibrary.VI_SUCCESS =>
          // defaultRM.getInt
          this.resourceManagerHandler = Some(defaultRM.getInt)
          println(s"Default RM: ${defaultRM.get}")
          
          sys.addShutdownHook(this.close)
          
          this.resourceManagerHandler.get
        case other =>
          throw new RuntimeException(s"An Error Occured while getting Default RM: code=$other")
      }

    case _ =>
      this.resourceManagerHandler.get
  }

  def close = this.resourceManagerHandler match {
    case Some(rm) =>
      VisaLibrary.viClose(rm)
      this.resourceManagerHandler = None
    case None =>

  }

  def getStatusDesc(code: Long) = {
    var readBuffer = Pointer.allocateBytes(4096)
    VisaLibrary.viStatusDesc(VISA.getResourceManagerHandler, code, readBuffer)
    readBuffer.getCString
  }

}