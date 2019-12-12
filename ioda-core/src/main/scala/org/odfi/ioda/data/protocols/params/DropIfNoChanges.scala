package org.odfi.ioda.data.protocols.params

import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.ioda.data.types.DataMessage

class DropIfNoChanges extends ProtocolWithId {
  
  this.onDownMessage {
    case dm : ParamsMessage => 
      
      dm.parameters.find {
        case (name,pv) => pv.changed
      } match {
        case Some(pv) => 
          
        case None => 
          dm.drop = true
      }
      
    case others => 
  }
  
}

class DropIfNoChangesNoTrue extends ProtocolWithId {
  
  this.onDownMessage {
    case dm : ParamsMessage => 
      
      dm.parameters.find {
        case (name,pv) => pv.changed && pv.isBoolean && pv.asBoolean==true
      } match {
        case Some(pv) => 
          
        case None => 
          dm.drop = true
      }
      
    case others => 
  }
  
}


class DropIfNoChangesInMetadata extends ProtocolWithId {
  
  this.onDownMessage {
    case dm : DataMessage => 
      
      dm.metadata.find {
        case (name,pv) => pv.changed
      } match {
        case Some(pv) => 
          
        case None => 
          dm.drop = true
      }
      
    case others => 
  }
  
}