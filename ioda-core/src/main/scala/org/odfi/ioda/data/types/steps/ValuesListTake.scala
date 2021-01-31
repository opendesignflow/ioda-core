package org.odfi.ioda.data.types.steps

import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.ioda.data.types.DataMessageValuesListMessage

class ValuesListTake(var number: Int) extends ProtocolWithId {

  this.onDownMessage {
    case lst: DataMessageValuesListMessage[_] =>

      assert(lst.values.size >= number, "Cannot Take more values than available")

      // Clone an take values
      val next = lst.cloneMessage
      next.takeValues(number)

      // Next
      lst.nextMessage = next

  }

}
