package com.idyria.ioda.data.protocols.display

import com.idyria.ioda.data.protocols.Protocol
import com.idyria.ioda.data.protocols.ProtocolWithId

class PrintMessageProtocol extends ProtocolWithId {
  
  this.onDownMessage {
    msg => 
      println(msg.toXMLString)
  }
}