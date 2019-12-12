package org.odfi.ioda.data.protocols.remap

import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.ubroker.core.broker.tree.single.SingleMessageIntermediary
import org.odfi.ioda.data.types.DataMessage

class VirtualChannelRemap extends ProtocolWithId {

  this.onConfigModelUpdated {

  }

  this.onDownMessage {
    dm =>

      // If configuration has a remap key with same name, change
      dm.virtualChannel match {
        case Some(vc) =>

          // Remap Direct values
          this.config.get.getKeysByType("remap").find(_.name.toString == vc) match {
            case Some(key) =>
              dm.virtualChannel = Some(key.values.head.toString)
              replaceVCValues(dm)
            case None =>
          }

          // Remap Regexp
          this.config.get.getKeysByType("regexp").foreach {
            case regexpKey if (regexpKey.name.toString.r.findFirstMatchIn(vc).isDefined) =>

              dm.virtualChannel = Some(regexpKey.values.head.toString())
              replaceVCValues(dm)
            case other =>
          }

        case None =>
      }

  }

  def replaceVCValues(m: DataMessage) = {

    m.getUID match {
      case Some(uid) =>
        m.virtualChannel = Some(m.getVirtualChannelAsString.replace("$UID", uid.value.toString()))

      case other =>
    }

  }

}