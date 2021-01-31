package org.odfi.ioda.data.phy.comport

import org.odfi.indesign.core.harvest.Harvester
import com.fazecast.jSerialComm.SerialPort
import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.data.phy.LineSupportPhy
import org.odfi.ioda.data.phy.ManagedOpenClosePhy
import java.io.InputStream
import java.io.OutputStream

object COMPortHarvester extends Harvester {
  
  
  override def doHarvest = {
    
    println(s"harvesting com ports")
    SerialPort.getCommPorts().foreach {
      port => 
        gather(new COMPort(port))
    }
    
  }
  
}

class COMPort(val port : SerialPort) extends ManagedOpenClosePhy with LineSupportPhy {
  
  override def getId = port.getSystemPortName
  override def getDisplayName = port.getDescriptivePortName
  
  var speed = 115200
  var timeouts = 3000
  
  
  def doOpen = {
  
    println("Opening")
    
    this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, timeouts, timeouts)
    //this.port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING| SerialPort.TIMEOUT_WRITE_SEMI_BLOCKING, timeouts, timeouts)
    //this.port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0,0)
    this.port.setBaudRate(speed)
    this.port.setNumDataBits(8)
    this.port.setNumStopBits(1)
    this.port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED)
    this.port.setParity(SerialPort.NO_PARITY)
    this.port.openPort() match {
      case true => println("Done")
      case false => sys.error("Could not open port ")
    }
  }

  def doClose = {
    this.port.closePort()
  }
  
  // Interfaces
  //--------------
  
  def phyGetOutputStream : OutputStream = {
    open 
    this.port.getOutputStream
  }
  def phyGetInputStream : InputStream = {
    open
    this.port.getInputStream
  }
  
  def phyWrite(b:Array[Byte]) = {
    open
  }
  
  def phyRead(count:Int) : Array[Byte] = {
    open
    Array()
  }

  // Line
  //----------
  override def pollValue = send1ZeroByte
  
  

  
}