package com.idyria.ioda.data.types

import com.idyria.osi.ooxoo.core.buffers.structural.xelement

@xelement(name="ValuesListMessage")
class DataMessageValuesListMessage[DT] extends ValuesListMessageTrait {
  
  var values = List[DT]()
}