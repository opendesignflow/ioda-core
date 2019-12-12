package org.odfi.ioda.data.protocols.log

import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.ioda.data.protocols.params.ParamsMessage

class SimpleLogProtocol extends ProtocolWithId {

  this.onDownMessage {
    case m =>
      
      logInfo[SimpleLogProtocol]("Received Message")
      logInfo[SimpleLogProtocol]("VCID=" + m.virtualChannel)
  }

  this.onDownMessage {

    case pm: ParamsMessage =>

      pm.parameters.foreach {
        case (k, v) =>
          logInfo[SimpleLogProtocol](s"$k=${v.value} (changed=${v.changed},param type: ${v.getClass().getCanonicalName})")
      }

      pm.metadata.foreach {
        case (k, v) =>
          logInfo[SimpleLogProtocol](s"Metadata $k=${v.value} (changed=${v.changed},param type: ${v.getClass().getCanonicalName})")
      }

    case d =>
      logInfo[SimpleLogProtocol]("Type=" + d.getClass.getCanonicalName)
      d.metadata.foreach {
        case (k, v) =>
          logInfo[SimpleLogProtocol](s"Metadata $k=${v.value} (changed=${v.changed},param type: ${v.getClass().getCanonicalName})")
      }
  }

}