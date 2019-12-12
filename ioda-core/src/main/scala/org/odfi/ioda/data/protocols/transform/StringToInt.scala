package org.odfi.ioda.data.protocols.transform

import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.ioda.data.types.StringValueMessage
import org.odfi.ioda.data.types.IntValueMessage

class StringValueToIntValue extends ProtocolWithId {

  this.onDownMessage {
    case dm: StringValueMessage =>

      val iv = new IntValueMessage
      iv.value = Some(dm.value.get.toInt)

      dm.nextMessage = iv
  }

}