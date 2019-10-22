package com.idyria.ioda.data.protocols.text

import com.idyria.ioda.data.protocols.ProtocolDescriptor
import com.idyria.ioda.data.types.ValuesListProvider
import com.idyria.ioda.data.phy.capabilities.ValuesListPhy
import com.idyria.ioda.data.protocols.Level0Protocol
import scala.reflect.ClassTag
import scala.reflect._
import com.idyria.ioda.data.types.StringValuesMessage
import com.idyria.ioda.data.types.IntValuesMessage
import com.idyria.ioda.data.types.DoubleValuesMessage
import com.idyria.ioda.data.types.DataMessageValuesListMessage
import com.idyria.ioda.data.phy.LineSupportPhy
import com.idyria.ioda.data.phy.PhysicalInterface
import com.idyria.ioda.data.types.ValueMessage
import com.idyria.ioda.data.types.IntValueMessage
import com.idyria.ioda.data.types.IntValueMessage
import com.idyria.ioda.data.types.StringValueMessage
import com.idyria.ioda.data.types.DoubleValueMessage

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