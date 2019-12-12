package org.odfi.ioda.data.types

trait ValuesListProvider[LT] {
  
  def getValuesList : List[LT]
  
}