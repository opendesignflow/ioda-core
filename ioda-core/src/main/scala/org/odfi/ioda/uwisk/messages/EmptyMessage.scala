package org.odfi.ioda.uwisk.messages

import org.odfi.ioda.data.types.DataMessage

class EmptyMessage extends DataMessage {

}

object EmptyMessage{
  def apply() = new EmptyMessage()
}
