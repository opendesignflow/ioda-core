package org.odfi.ioda.data.protocols.log

import org.odfi.ioda.data.types.DataMessage

class LatestMessageLogger extends DataLogger {
  
  var latestMessages = scala.collection.mutable.Map[String,DataMessage]()
  
  this.onDownMessage {
    case dm => 
      
      this.latestMessages.update(dm.getVCAndUID, dm)
      
      
  }
  
}