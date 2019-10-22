package com.idyria.ioda.data.types

trait ValuesListProvider[LT] {
  
  def getValuesList : List[LT]
  
}