package org.odfi.ioda.instruments.nivisa.tektronix

import org.odfi.ioda.instruments.data.XWaveform
import org.odfi.ioda.instruments.ieee.IEEE4882BinaryBlock
import org.odfi.ioda.instruments.nivisa.VISADevice
import org.odfi.ioda.instruments.osci.OSCIDevice

abstract class TekTronixOsci(baseDevice: VISADevice) extends TekTronixDevice(baseDevice) with OSCIDevice {

  // Trigger
  // TRIGger:STATE?
  /*
   * ARMED indicates that the oscilloscope is acquiring pretrigger information.
AUTO indicates that the oscilloscope is in the automatic mode and acquires data
even in the absence of a trigger.
READY indicates that all pretrigger information has been acquired and that the
oscilloscope is ready to accept a trigger.
SAVE indicates that the oscilloscope is in save mode and is not acquiring data.
TRIGGER indicates that the oscilloscope triggered and is acquiring the post trigger
information.
   */

  def isTriggered: Boolean = {
    this.readString("TRIGger:STATE?") match {
      case t if (t.startsWith("SAV"))  => true
      case t if (t.startsWith("TRIG")) => true
      case other =>
        //println("Trigger is: "+other)
        false
    }
  }

  // Channels
  //------------------

  def selectChannel(channel: Int) = {
    this.write(s":DATa:SOUrce CH${channel}")
  }

  // Acquire
  //---------------

  def enableSingle: Unit = {

    try {
      //this.write("ACQuire:STATE ON")
      // this.write("ACQuire:STOPAfter RUNSTop")
      this.write("ACQuire:STOPAfter SEQ")
      this.write("ACQuire:STATE 1")
    } catch {
      case e: Throwable =>
      /* Thread.sleep(10)
        this.write("ACQuire:STATE ON")
        Thread.sleep(10)
        this.write("ACQuire:STOPAfter RUNSTop")
        Thread.sleep(10)
        this.write("ACQuire:STOPAfter SEQ")
        Thread.sleep(10)*/
    }
  }

  def enableRun: Unit = {
    // println("custom runstop")
    this.write("ACQuire:STOPAfter RUNSTop")
    this.write("ACQuire:STATE 1")
  }

  /**
   * Makes acquisition OFF then rerun it
   */
  def withStopAndRestartAcquire[T](cl: => T): T = {

    acquireOff
    try {
      cl
    } finally {
      acquireRun
    }
  }

  def acquireOff = {
    this.write("ACQuire:STATE OFF")
  }

  def acquireRun = {
    this.write("ACQuire:STATE RUN")
  }

  def getPNGScreen = {

    //-- Save image
    write("SAVe:IMAge:FILEFormat PNG")
    write("SAVe:IMAge \"E:/scr.png\"")

    //-- Get Image
    readBytes("FILESystem:READFile \"E:/scr.png\"")

  }

  // Wavzeform
  //------------------

  def setupAcquire(channel: Int, points: Int) = {

    this.selectChannel(channel)

    this.write(s":ACQUIRE:NUMSAMples $points")
    this.write(s"WFMOutpre:NR_Pt $points")
    this.write("WFMOutpre:ENCdg RIBINARY")
    this.write("WFMOutpre:BIT_NR 8")

    this.write("WFMOutpre:BYT_Nr 1") // 1 byte per point
    this.write("DATa:STARt 1")
    this.write(s"DATa:STOP $points")

    enableRun

  }
  /**
   * Warning, this method does not stop the oscilloscope for acquire
   * If you want to force stop, you should use acquireOff/Run or withAcquireStopAnRestart
   * yourself
   *
   * WARNING: the Y offset is removed from the data here and Y origin set to 0
   * This is because the Y Origin in XWaveform is supposed to be a real value not a digitizing level value
   * And the osci gives 0 back for this value all the time, but offset, which is in digitizing value is correct.
   */
  def getWaveform: XWaveform = {

    var waveform = new XWaveform()

    var points = this.readDouble("WFMOutpre:NR_Pt?")
    var timeScale = this.readDouble("WFMOutpre:XINcr?")
    var yoffset = this.readDouble("WFMOutpre:YOFF?")
    var ymult = this.readDouble("WFMOutpre:YMUlt?")
    var xunit = this.readString("WFMOutpre:XUNit?")
    var yunit = this.readString("WFMOutpre:YUNit?")
    var yorigin = this.readDouble("WFMOutpre:YZEro?")
    var xorigin = this.readDouble("WFMOutpre:XZEro?")

    /*println("Origin is: "+yorigin, "offset: "+yoffset+", ymult: "+ymult+", points: "+points)
    println("State Acq: "+this.readDouble("ACQuire:NUMACq?"))
    println("BYT_NR"+this.readDouble("WFMInpre:BYT_Nr?"))
    println("Bits: "+ this.readDouble("WFMINPRE:BIT_NR?"))*/

    //-- Get curve
    //Thread.sleep(1000)
    var curve = this.readBytes("CURVE?")

    //-- First char must be #
    var dataBlock = new IEEE4882BinaryBlock(Some(curve))

    //-- Convert to int (one byte in one int)
    var dataInt = dataBlock.getData.map { b => b.toInt }

    //-- Save to waveform
    //println("Test: "+dataInt.size)

    waveform.data = dataInt
    waveform.points = points.toLong
    waveform.xIncrement = timeScale
    waveform.xUnit = xunit
    waveform.yIncrement = ymult
    waveform.yUnit = yunit


    //-- Y origin calculated from offset in digital levels * Y increments per bit
    waveform.yOrigin = (yoffset * ymult)

    //-- X origin?
    waveform.xOrigin = xorigin

    //-- Return
    waveform

  }

}