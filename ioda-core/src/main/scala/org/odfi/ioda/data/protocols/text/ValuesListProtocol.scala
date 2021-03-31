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
import org.odfi.ioda.pipelines.ScheduleCollectPipelineTrait

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

    //phy.sendLineReceiveLine(":VALUES?").split(",").toList
    phy.pollValue
    val res = phy.receiveLine().split(",").toList
    //println("REsult: " + res)
    res
    //phy.sendLineReceiveLine("").split(",").toList

  }

  def dataHarvest(phy: LineSupportPhy) = {

    /*    (getValuesList(phy), config.get.supportGetInt("valuesCount")) match {
          case (None, _) =>
            None

          case (Some(dm), Some(requestedCount)) if (requestedCount>=0 && requestedCount != dm.values.size) =>
            addError(s"Requested Values Count $requestedCount, obtained ${dm.values.size}")
            None
          case (Some(dm), _) =>
            Some(dm)
        }*/
    (getValuesList(phy))

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
    //println(s"Harvesting List of Values: " + phy)

    sendReceiveValuesList(phy) match {
      case notEmpty if (notEmpty.size > 0) =>
        val res = new IntValuesMessage
        res.values = notEmpty.map(_.trim.toInt)
        Some(res)
      case other =>
        None
    }

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