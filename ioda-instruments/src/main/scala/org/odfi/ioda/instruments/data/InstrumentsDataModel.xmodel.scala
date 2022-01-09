package org.odfi.ioda.instruments.data

import org.odfi.ooxoo.model.ModelBuilder
import org.odfi.ooxoo.model.producer
import org.odfi.ooxoo.model.producers
import org.odfi.ooxoo.model.out.markdown.MDProducer
import org.odfi.ooxoo.model.out.scala.ScalaProducer
import org.odfi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait


@producers(Array(
  new producer(value = classOf[ScalaProducer])))
class InstrumentsDataModel extends ModelBuilder {

  val wp = "WaveformParameters" is {

    // POints  count
    "Points" ofType "long"
    "XReference" ofType "double"
    "XIncrement" ofType "double"
    "XOrigin" ofType "double"
    "XUnit" ofType "string"
    "YReference" ofType "double"
    "YIncrement" ofType "double"
    "YOrigin" ofType "double"
    "YUnit" ofType "string"

  }

  "XWaveform" is {
    elementsStack.head.makeTraitAndUseCustomImplementation
    withTrait("org.odfi.ioda.instruments.compress.XMLCompressOutput")

    attribute("name")



    // External
    attribute("externalFile") ofType ("string")

    // Parameters
    //---------
    importElement(wp)


    //-- Old
    "Points" ofType "long"
    "XReference" ofType "double"
    "XIncrement" ofType "double"
    "XOrigin" ofType "double"
    "XUnit" ofType "string"
    "YReference" ofType "double"
    "YIncrement" ofType "double"
    "YOrigin" ofType "double"
    "YUnit" ofType "string"

    // If embedded data
    "Data" ofType ("intbinary")

  }

}