package com.idyria.ioda.data.protocols.transform

import com.idyria.ioda.data.protocols.ProtocolWithId
import com.idyria.ioda.data.types.StringValueMessage
import com.idyria.ioda.data.types.IntValueMessage

class StringValueToIntValue extends ProtocolWithId {

  this.onDownMessage {
    case dm: StringValueMessage =>

      val iv = new IntValueMessage
      iv.value = Some(dm.value.get.toInt)

      dm.nextMessage = iv
  }

}