package org.odfi.ioda.ui.charts

import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.ioda.data.types.{DoubleValuesMessage, IntValuesMessage}
import org.odfi.ioda.instruments.measurements.data.XYGraph

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