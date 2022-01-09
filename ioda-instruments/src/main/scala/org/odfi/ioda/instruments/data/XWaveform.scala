package org.odfi.ioda.instruments.data

import org.odfi.ooxoo.core.buffers.structural.xelement

@xelement(name = "XWaveform")
class XWaveform extends XWaveformTrait {

  /**
   * Returns the data as doubles translated based on the Waveform parameters
   */
  def getRealData = data match {
    case null => Array[Double]()
    case data =>
      data.data.map {
        case value =>

          (value * yIncrement.data) + yOrigin.data.toDouble
      }
  }


}

object XWaveform {

  def apply(url: java.net.URL) = {

    // Instanciate
    var res = new XWaveform

    // Set Stax Parser and streamIn
    var io = org.odfi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(url)
    res.appendBuffer(io)
    io.streamIn

    // Return
    res

  }

}