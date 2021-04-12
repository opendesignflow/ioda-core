package org.odfi.ioda.instruments.nivisa

import org.odfi.ioda.instruments.nivisa.VisaLibrary
import org.bridj.Pointer

import scala.io.Source
import org.odfi.indesign.core.module.measurement.MeasurementDevice
import org.odfi.ioda.instruments.ieee.IEEE4882BinaryBlock

import java.io.ByteArrayOutputStream
import java.text.{DecimalFormat, DecimalFormatSymbols}
import java.util.Locale

class VISADevice(val deviceString: String) extends MeasurementDevice {

  def this(d: VISADevice) = this(d.deviceString)

  var deviceSession: Option[Long] = None

  // ID And descriptions
  def getId = deviceString

  /**
   * This method will open the interface if not openend and close it again
   * If the interface is openend, it won't close it
   */
  def getInterfaceDescription = {

    var desc = isOpen match {
      case true =>
        var description = Pointer.allocateChars(256)
        VisaLibrary.viGetAttribute(deviceSession.get, VisaLibrary.VI_ATTR_INTF_INST_NAME, description)
        description.getCString
      case false =>
        try {

          // Open - Ask - Close
          requireOpen
          var description = Pointer.allocateChars(256)
          VisaLibrary.viGetAttribute(deviceSession.get, VisaLibrary.VI_ATTR_INTF_INST_NAME, description)

          close

          description.getCString
        } catch {
          case e: Throwable => "Error while reading description: " + e.getLocalizedMessage
        }
    }
    desc

  }

  def requireOpen = deviceSession match {
    case Some(session) =>
    case None => open
  }

  def isOpen = this.deviceSession.isDefined

  def open = {


    //-- Prepare Session or isntrument
    var instrumentSession = Pointer.allocateCLong()
    VisaLibrary.viOpen(VISA.getResourceManagerHandler, Pointer.pointerToCString(deviceString), VisaLibrary.VI_NULL, VisaLibrary.VI_NULL, instrumentSession) match {

      case 0 =>
        println(s"Opened Device: ${deviceString}")
        sys.addShutdownHook(this.close)
      case other =>
        throw new RuntimeException(s"An Error Occured while opening device ${deviceString} code=$other, message=${VISA.getStatusDesc(other)}")

    }
    this.deviceSession = Some(instrumentSession.getInt)

  }

  def close = deviceSession match {
    case Some(session) =>
      this.deviceSession = None
      VisaLibrary.viClose(session)
    case None =>
  }

  // Type
  //--------------
  def isUSB = {
    this.getId.startsWith("USB")
  }
  def isSerial = {
    this.getId.startsWith("ASRL")
  }

  // USB
  //-----------
  def getVendorID = {
    require(isUSB)
    this.getId.split("::")(1)
  }

  def getProductID = {
    require(isUSB)
    this.getId.split("::")(2)
  }

  def getModelID = {
    require(isUSB)
    this.getId.split("::")(3)
  }

  // I/O
  //-----------------

  def readString(command: String): String = {
    this.synchronized {
      
      requireOpen
      
      //-- Discard Input Buffer to make sure we only get the result of the command
      VisaLibrary.viFlush(deviceSession.get, VisaLibrary.VI_READ_BUF.toShort)

      this.write(command)

      // Read bytes as string 
      new String(this.readBytes).trim

    }

  }

  def readDouble(command: String): Double = {
    this.synchronized {
      
      requireOpen
      
      //-- Discard Input Buffer to make sure we only get the result of the command
      VisaLibrary.viFlush(deviceSession.get, VisaLibrary.VI_READ_BUF.toShort)

      this.write(command)
      new String(this.readBytes).toDouble
    }
  }

  def readBytes(command: String): Array[Byte] = {

    this.synchronized {
      requireOpen
      
      //-- Discard Input Buffer to make sure we only get the result of the command
      VisaLibrary.viFlush(deviceSession.get, VisaLibrary.VI_READ_BUF.toShort)
      this.write(command)
      this.readBytes
    }
  }

  def readIEEE4882Bytes(command:String) = {
    new IEEE4882BinaryBlock(Some(readBytes(command)))
  }

  def readBytes: Array[Byte] = {
    this.synchronized {
      requireOpen
      
      //-- Read
      var resBytes = new ByteArrayOutputStream
      var readBuffer = Pointer.allocateBytes(4096)
      var readCount = Pointer.allocateCLong()
      var continue = true
      while (continue) {
        VisaLibrary.viRead(deviceSession.get, readBuffer, 4096, readCount) match {
          case 0 =>
            //println(s"Read: ${readCount.getInt}")
            resBytes.write(readBuffer.getBytes(readCount.getInt))
            continue = false
          //Some(new String(readBuffer.getBytes(readCount.getInt).dropRight(1)))

          // Maybe more available
          case 1073676294 =>

            // Write and continue
            resBytes.write(readBuffer.getBytes(readCount.getInt))
            continue = true

          case other =>
            throw new RuntimeException(s"Error While Reading data to device $deviceString, code=$other, message=${VISA.getStatusDesc(other)}")
        }
      }

      resBytes.toByteArray()

    }

  }

  /**
   * \n is added to the end of string if required
   */
  def write(command: String): Unit = {
    this.synchronized {
      requireOpen

      var finalCommand = command.last match {
        case '\n' => command
        case _ => command + "\n"
      }

      //-- Write
      var totalWritten = 0
      while (totalWritten < finalCommand.length()) {
        var written = Pointer.allocateCLong()
        VisaLibrary.viWrite(deviceSession.get, Pointer.pointerToCString(finalCommand), finalCommand.length().toLong, written) match {
          case 0 =>
            totalWritten += written.getInt
          case other =>
            throw new RuntimeException(s"Error While Writting data to device $deviceString, code=$other ")
        }
      }

      //-- FLush
      VisaLibrary.viFlush(deviceSession.get, VisaLibrary.VI_WRITE_BUF.toShort)
    }
  }

  /**
   * String is split to lines, lines are trimmed so code can be ugly
   */
  def writeLines(lines:String) = {
    Source.fromChars(lines.toCharArray()).getLines().filterNot(_.trim=="").foreach {
      l => 
        write(l.trim)
    }
  }
  
  // Utils
  //--------------

  def doubleToSCPIString(d:Double) = {
    
    var symbols = DecimalFormatSymbols.getInstance(Locale.US);
    symbols.setExponentSeparator(if (d>1 || d < -1) "E+" else "E-")
    
    val f = new DecimalFormat("0.00E0")
    f.setDecimalFormatSymbols(symbols)
    /*f.setMaximumIntegerDigits(2)
    f.setMaximumFractionDigits(2)
    f.setMinimumFractionDigits(1)
    f.setMinimumIntegerDigits(1)*/
    f.setDecimalSeparatorAlwaysShown(true)
    f.setNegativePrefix("-")
    f.setPositivePrefix("+")
    f.setParseBigDecimal(true)
    f.format(d)
  }
  
  def getDeviceId = {
    this.readString("*IDN?")

  }

}