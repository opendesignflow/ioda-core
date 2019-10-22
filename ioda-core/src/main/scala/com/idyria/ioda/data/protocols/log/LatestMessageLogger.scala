package com.idyria.ioda.data.protocols.log

import com.idyria.ioda.data.types.DataMessage

class LatestMessageLogger extends DataLogger {
  
  var latestMessages = scala.collection.mutable.Map[String,DataMessage]()
  
  this.onDownMessage {
    case dm => 
      
      this.latestMessages.update(dm.getVCAndUID, dm)
      
      
  }
  
}