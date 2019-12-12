package org.odfi.ioda.data.protocols.display

import org.odfi.ioda.data.protocols.Protocol
import org.odfi.ioda.data.types.IntValuesMessage
import kit.edu.ipe.adl.chiptest.measurement.data.XYGraph
import org.odfi.ioda.data.types.DoubleValuesMessage
import org.odfi.ioda.data.protocols.ProtocolWithId

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