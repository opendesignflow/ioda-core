package org.odfi.ioda.instruments.nivisa.keysight.wavegen

import org.odfi.ioda.instruments.nivisa.VISADevice
import org.odfi.ioda.instruments.nivisa.usb.VISAUSBDevice

class KSTrueForm33200B(d: VISADevice) extends VISADevice(d) {

  def outputOff = {
    write("OUTPut Off")
  }

  def outputOn = {
    write("OUTPut ON")
  }

  def outputSin(freq: Double, voltageLow: Double, voltageHigh: Double,phase:Double) = {
    outputOff

    val lowVoltageFormated = f"$voltageLow%2f"
    val highVoltageFormated = f"$voltageHigh%2f"
    val toSend = s"""
FUNCtion SIN
FREQuency  ${doubleToSCPIString(freq)}
VOLTage:HIGH ${doubleToSCPIString(voltageHigh)}
VOLTage:LOW ${doubleToSCPIString(voltageLow)}
PHASe ${doubleToSCPIString(phase)}
"""
    //println(toSend)

    d.writeLines(toSend)
   /* d.writeLines(s"""
FUNCtion SIN
FREQuency +1.0E+05
VOLTage:HIGH +2.0
VOLTage:LOW +0.0
PHASe +90.0""")*/

    outputOn

  }

}