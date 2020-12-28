package org.odfi.ioda.pipelines

import org.odfi.ioda.data.types.DataMessage

trait PipelineForVCID extends Pipeline  {

  var acceptedVCID : Option[String] = None

  def acceptVCID(id:String) = this.acceptedVCID = Some(id)

  this.acceptDown[DataMessage] {
    case msg  if (msg.virtualChannel.isDefined && acceptedVCID.isDefined && msg.virtualChannel.get==acceptedVCID.get) => true
    case other => false
  }

}
