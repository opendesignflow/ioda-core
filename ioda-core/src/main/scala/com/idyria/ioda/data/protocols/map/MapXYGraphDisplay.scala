package com.idyria.ioda.data.protocols.map

import com.idyria.ioda.data.protocols.ProtocolWithId
import com.idyria.ioda.data.types.DataMapMessage
import kit.edu.ipe.adl.chiptest.measurement.data.XYGraph
import org.jfree.data.xy.DefaultXYDataset

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