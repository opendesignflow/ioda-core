package org.odfi.ioda.instruments.measurements.data

import com.idyria.osi.ooxoo.model.{ModelBuilder, producer, producers}
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer


@producers(Array(
  new producer(value = classOf[ScalaProducer])))
class MeasurementDataModel extends ModelBuilder {


  // Data definition
  //----------------------------

  val graph = "Graph" is {
    isTrait
    withTrait("org.odfi.ioda.instruments.compress.XMLCompressOutput")
    attribute("name")
    attribute("creationDate") ofType "datetime"
    attribute("display") ofType "string" default "line"

    //-- External attributes
    attribute("externalFile") ofType "string"
    attribute("externalType") ofType "string"

    //-- Generated attribute to ease cleaning
    attribute("generated") default "false"

  }

  //-- XY
  val xyGraph = "XYGraph" is {
    withTrait(graph)
    elementsStack.top.makeTraitAndUseCustomImplementation

    // Statistics
    "Statistics" is {
      "Stat" multiple {
        ofType("string")
        attribute("name")
      }
    }

    // Output Products
    //------------
    "OutputProduct" multiple {

      attribute("file")
      attribute("fileType")
      attribute("generatorFunction")


    }


    // Data sources
    //----------------

    //-- Raw Data
    "RawValues" ofType "doublebinary"
    "RawPoints" ofType "doublebinary"

    //-- Points
    "Point" multiple {

      "X" ofType "double"
      "Y" multiple {
        ofType("double")
      }
    }

    //-- Waveform
    importElement("org.odfi.ioda.instruments.data.XWaveform")


  }


  // Multi Graph
  //-----------------
  val multiGraph = "MultiXYGraph" is {

    // Compress interface to easily save to zip for example
    withTrait("org.odfi.ioda.instruments.compress.XMLCompressOutput")
    elementsStack.top.makeTraitAndUseCustomImplementation

    // XYGrapgs
    importElement(xyGraph).setMultiple

    attribute("name")
    attribute("creationDate") ofType "datetime"

    // Waveform parameters
    // Saved here in case a measurement from OSCI, used to always know what was the OSCI setup like
    importElement("org.odfi.ioda.instruments.data.WaveformParameters")

    // Shift Register configuration state
    //importElement("kit.edu.ipe.adl.chiptest.shiftregister.model.SRCommon").name = "ShiftRegister"

  }


}