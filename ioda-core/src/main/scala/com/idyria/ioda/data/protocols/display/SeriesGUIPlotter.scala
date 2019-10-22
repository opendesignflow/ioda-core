package com.idyria.ioda.data.protocols.display

import com.idyria.ioda.data.protocols.Protocol
import com.idyria.ioda.data.types.IntValuesMessage
import kit.edu.ipe.adl.chiptest.measurement.data.XYGraph
import com.idyria.ioda.data.types.DoubleValuesMessage
import com.idyria.ioda.data.protocols.ProtocolWithId

class SeriesGUIPlotter extends ProtocolWithId {

  //this.onDeliverFor

  this.onDownMessage {
    case ints: IntValuesMessage =>

      val g = XYGraph(ints.values)
      g.toJFreeChart

    case doubles: DoubleValuesMessage =>

      val g = XYGraph(doubles.values.toArray)
      g.toJFreeChart

    case other =>
  }
}