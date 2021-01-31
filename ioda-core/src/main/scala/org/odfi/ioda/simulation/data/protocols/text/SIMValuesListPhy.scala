package org.odfi.ioda.simulation.data.protocols.text

import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.data.phy.TextSupportPhy
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterface
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterface
import org.odfi.ioda.data.phy.capabilities.ValuesListPhy
import scala.util.Random

class SIMValuesListPhy extends SIMLineSupportPhy with ValuesListPhy {

  this.onConfigModelUpdated {

    config.get.supportIntKey("speed", 100, "Speed in Hertz")
    config.get.supportBooleanKey("gaussian", false, "Gaussian Spread of the Random Values")
    config.get.supportRangeDouble("valueRange", "Random value range", Double.MinValue, Double.MaxValue)
    config.get.supportIntKey("valuesCount", 5, "Number of values")
    config.get.supportBooleanKey("generateInteger", false, "Generate Integers")
    //config.get.supportRangeDouble("maxValueRange", "Maximum Random value range")

  }

  def pollValue =  {

  }

  def receivedLineSendbackLine(line: String): String = {
    getValuesList.mkString(",")
  }

  def getValuesList: List[String] = {

    config.get.supportGetInt("valuesCount") match {
      case None => sys.error("No Values Count Config for creating list values")
      case Some(count) =>
        logFine[SIMValuesListPhy](s"Generating count: " + count)
        (0 until count).map {
          i =>
            val generated = (Math.abs(Random.nextGaussian()) * 1024 % 1024)
            if (config.get.supportGetBoolean("generateInteger").get) {
              generated.toInt.toString
            } else {
              generated.toString
            }
        }.toList
    }

  }

}