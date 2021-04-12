package org.odfi.ioda.ui.charts

import org.jfree.data.xy.DefaultXYDataset
import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.ioda.data.types.DataMapMessage
import org.odfi.ioda.instruments.measurements.data.XYGraph

class MapXYGraphDisplay extends ProtocolWithId  {
  
  var g = new XYGraph
  var chart : Option[DefaultXYDataset] = None
  this.onDownMessage {
    case cl : DataMapMessage => 
      
      cl.dataMap.headOption match {
        case Some((name,value)) => 
          
          g.addPoint(g.getYValues.size,value.toInt)
          
          chart match {
            case Some(ds) =>
             //ds.s
              ds.removeSeries(ds.getSeriesKey(0))
              ds.addSeries(name, g.toJFreeChartSeriesValues)
            case None => 
              g.name = name
              chart = Some(g.toJFreeChart)
              
          }
        case None => 
      }
      
      
      
    case cl => 
  }
  
}