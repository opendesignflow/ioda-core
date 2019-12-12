package org.odfi.ioda.data.protocols.text

import org.odfi.ioda.data.protocols.ProtocolDescriptor
import org.odfi.ioda.data.types.ValuesListProvider
import org.odfi.ioda.data.phy.capabilities.ValuesListPhy
import org.odfi.ioda.data.protocols.Level0Protocol
import scala.reflect.ClassTag
import scala.reflect._
import org.odfi.ioda.data.types.StringValuesMessage
import org.odfi.ioda.data.types.IntValuesMessage
import org.odfi.ioda.data.types.DoubleValuesMessage
import org.odfi.ioda.data.types.DataMessageValuesListMessage
import org.odfi.ioda.data.phy.LineSupportPhy
import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.data.types.ValueMessage
import org.odfi.ioda.data.types.IntValueMessage
import org.odfi.ioda.data.types.IntValueMessage
import org.odfi.ioda.data.types.StringValueMessage
import org.odfi.ioda.data.types.DoubleValueMessage

abstract class SingleValueProtocol[DT <: ValueMessage[_]] extends TextProtocol with Level0Protocol[LineSupportPhy] {

  this.onConfigModelUpdated {

    //config.get.supportIntKey("valuesCount", -1, "Number of values expected, -1 means no checks")

  }
  
  /*def checkCompatibility[DT](p:PhysicalInterface)(implicit tag : ClassTag[DT]) = {
    p match {
      case tag(p) => true
      case other => false
    }
  }*/
  
   def sendReceiveValue(phy: LineSupportPhy) = {
   
    phy.sendLineReceiveLine(":VALUE?").split("=").last.trim
    
  }

  def dataHarvest(phy: LineSupportPhy) = {

    getValue(phy) match {
      case None => 
        None

      case Some(dm)=> 
        Some(dm)
    }

  }

  def getValue(phy: LineSupportPhy): Option[DT]

}

class StringValueProtocol extends SingleValueProtocol[StringValueMessage] {

 
  
  def getValue(phy: LineSupportPhy) = {

    val res = new StringValueMessage
    
   // res.values = this.connectedPhy.get.getValuesList
    
    Some(res)
  }

}

class IntValueProtocol extends SingleValueProtocol[IntValueMessage] {

  def getValue(phy: LineSupportPhy) = {
    //println(s"Harvesting List of Values")

    val res = new IntValueMessage
    res.value = Some(sendReceiveValue(phy).toInt)
    Some(res)
  }

}

class DoubleValueProtocol extends SingleValueProtocol[DoubleValueMessage] {

  def getValue(phy: LineSupportPhy) = {
    //println(s"Harvesting List of Double Values")

    val res = new DoubleValueMessage
    res.value = Some(sendReceiveValue(phy).toDouble)
    Some(res)
  }

}

object SingleValueProtocol extends ProtocolDescriptor {

}