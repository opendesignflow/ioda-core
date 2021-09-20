package org.odfi.ioda.instruments.nivisa.keysight

import org.odfi.ioda.instruments.data.XWaveform
import org.odfi.ioda.instruments.nivisa.{VISADevice, VISAOsciDevice}
import org.odfi.ioda.instruments.nivisa.keysight.waveform.Preamble
import org.odfi.ioda.instruments.nivisa.tektronix.TekTronixDevice
import org.odfi.ioda.instruments.osci.OSCIDevice

class KeysightOsci(baseDevice: VISADevice) extends VISAOsciDevice(baseDevice) with KeysightDevice {

  def isTriggered = this.readString(":TER?").toInt match {case 1 => true ; case 0 => false}

  def forceTrigger = {

    this.write(":TRIGger:FORCe")


  }
  
  /**
   * Warning, points configuration not set
   */
  def setupAcquire(channel:Int, points:Int) = {
    selectChannel(channel)
  }
  
  def selectChannel(c:Int) = {
    this.write(s":WAVeform:SOURce CHANnel${c}")
  }
  
  def enableSingle = {
     this.write(":SINGLE")
  }
  
   def enableRun: Unit = {
    this.write(":RUN")
  }
  
  
  /**
   * Returns a PNG format screenshot
   */
  def saveScreenBytesPNG  = {


    
    this.readIEEE4882Bytes(":DISPlay:DATA? PNG, COLOR")
    
    
  }

  
  
  /**
   * Does not prepare acquisition
   */
  def getWaveform = {
    
    


    //println(s"Getting waveform")
    //Thread.sleep(100)


    //-- Get Number of points
    var pointsCount = this.readString(":WAVeform:COUNt?")
    //println("POints count: "+pointsCount)

    //-- Get preamble
    var pr = this.readString(":WAVeform:PREamble?")
    //println("Preamble: "+pr)
    var preamble =  new Preamble(pr)

   // println("PR format: "+preamble.format)
   // println("PR points: "+preamble.points)


    //-- Read WF
    //var waveform = new Waveform(preamble)
    var data = this.readIEEE4882Bytes(":WAVeform:DATA?")

    //waveform.fromBytes(data.getData)

    var xwaveform = new  XWaveform()
    xwaveform.data =  data.getData.map { b => b.toInt }
    xwaveform.points = preamble.points
    xwaveform.xIncrement= preamble.dblXIncrement
    xwaveform.xReference = preamble.lngXReference.toDouble
    xwaveform.xOrigin= preamble.dblXOrigin
    xwaveform.yIncrement=preamble.sngYIncrement
    xwaveform.yOrigin= preamble.sngYOrigin
    xwaveform.yReference= preamble.lngYReference.toDouble

    //println(s"Done waveform")

    xwaveform



  }


}