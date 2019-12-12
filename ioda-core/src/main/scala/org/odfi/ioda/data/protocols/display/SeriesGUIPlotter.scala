package org.odfi.ioda.data.protocols.display

import org.odfi.instruments.measurements.data.XYGraph
import org.odfi.ioda.data.types.IntValuesMessage
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