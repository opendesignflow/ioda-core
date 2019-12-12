package org.odfi.ioda.simulation.data.protocols.text

import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterface
import org.odfi.ioda.data.phy.LineSupportPhy
import java.io.ByteArrayOutputStream
import org.codehaus.plexus.util.StringInputStream
import java.io.DataInputStream
import java.io.ByteArrayInputStream

abstract class SIMLineSupportPhy extends SIMPhysicalInterface with LineSupportPhy {
  
  
  var os = new ByteArrayOutputStream {
    override def flush = {
      val res = SIMLineSupportPhy.this.receivedLineSendbackLine(new String(toByteArray()))
      sendBackLine(res)
    }
  }
  
  var is = new ByteArrayInputStream(new Array(0))
  
  def doClose: Unit =  {
    
  }
  def doOpen: Unit = {
    
  }
  
  // Members declared in org.odfi.ioda.data.phy.PhysicalInterface
  def phyGetInputStream: java.io.InputStream = {
    is
  }
  def phyGetOutputStream: java.io.OutputStream = {
    os
  }
  def phyRead(count: Int): Array[Byte] = {
    null
  }
  def phyWrite(b: Array[Byte]): Unit = {
    
  }

  // SIM PHY
  //-----------
  
  /**
   * To be implemented
   */
  def receivedLineSendbackLine(line:String) : String
  

  def sendBackLine(line:String) = {
    this.is = new ByteArrayInputStream(line.getBytes)
  }
  
}