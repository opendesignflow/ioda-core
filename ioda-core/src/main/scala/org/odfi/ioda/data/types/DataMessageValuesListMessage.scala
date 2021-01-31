package org.odfi.ioda.data.types

import com.idyria.osi.ooxoo.core.buffers.structural.xelement

@xelement(name="ValuesListMessage")
abstract class DataMessageValuesListMessage[DT] extends ValuesListMessageTrait {
  
  var values = List[DT]()

  def takeValues(count:Int) = this.values = this.values.take(count)

  def cloneMessage : DataMessageValuesListMessage[DT]
}