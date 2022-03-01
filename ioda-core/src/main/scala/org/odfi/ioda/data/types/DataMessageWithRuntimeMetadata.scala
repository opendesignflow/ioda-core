package org.odfi.ioda.data.types

import org.odfi.ioda.uwisk.WithRuntimeMetadata

trait DataMessageWithRuntimeMetadata extends DataMessage with WithRuntimeMetadata {


  override def nextMessage_=[T <: DataMessage](next: T) = {
    super.nextMessage_= (next)

    if (next.isInstanceOf[DataMessageWithRuntimeMetadata]) {
      next.asInstanceOf[DataMessageWithRuntimeMetadata].runtimeMetadata = this.runtimeMetadata
    }

  }

}
