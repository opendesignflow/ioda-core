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

abstract class ValuesListProtocol[DT <: DataMessageValuesListMessage[_]] extends TextProtocol with Level0Protocol[LineSupportPhy] {

  this.onConfigModelUpdated {

    config.get.supportIntKey("valuesCount", -1, "Number of values expected, -1 means no checks")

  }
  
  /*def checkCompatibility[DT](p:PhysicalInterface)(implicit tag : ClassTag[DT]) = {
    p match {
      case tag(p) => true
      case other => false
    }
  }*/
  
   def sendReceiveValuesList(phy: LineSupportPhy) = {
   
    phy.sendLineReceiveLine(":VALUES?").split(",").toList
    
  }

  def dataHarvest(phy: LineSupportPhy) = {

    (getValuesList(phy), config.get.supportGetInt("valuesCount")) match {
      case (None, _) => 
        None

      case (Some(dm), Some(requestedCount)) if (requestedCount>=0 && requestedCount != dm.values.size) =>
        addError(s"Requested Values Count $requestedCount, obtained ${dm.values.size}")
        None
      case (Some(dm), _) => 
        Some(dm)
    }

  }

  def getValuesList(phy: LineSupportPhy): Option[DT]

}

class StringValuesListProtocol extends ValuesListProtocol[StringValuesMessage] {

 
  
  def getValuesList(phy: LineSupportPhy) = {

    val res = new StringValuesMessage
    
   // res.values = this.connectedPhy.get.getValuesList
    
    Some(res)
  }

}

class IntValuesListProtocol extends ValuesListProtocol[IntValuesMessage] {

  def getValuesList(phy: LineSupportPhy) = {
    //println(s"Harvesting List of Values")

    val res = new IntValuesMessage
    res.values = sendReceiveValuesList(phy).map(_.toInt)
    Some(res)
  }

}

class DoubleValuesListProtocol extends ValuesListProtocol[DoubleValuesMessage] {

  def getValuesList(phy: LineSupportPhy) = {
    //println(s"Harvesting List of Double Values")

    val res = new DoubleValuesMessage
    res.values = sendReceiveValuesList(phy).map(_.toDouble)
    Some(res)
  }

}

object ValuesListProtocol extends ProtocolDescriptor {

}