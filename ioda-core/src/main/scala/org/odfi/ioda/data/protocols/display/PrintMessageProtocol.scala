package org.odfi.ioda.data.protocols.display

import org.odfi.ioda.data.protocols.Protocol
import org.odfi.ioda.data.protocols.ProtocolWithId

class PrintMessageProtocol extends ProtocolWithId {
  
  this.onDownMessage {
    msg => 
      println(msg.toXMLString)
  }
}