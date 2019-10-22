package com.idyria.ioda.data.phy.capabilities

import com.idyria.ioda.data.phy.TextSupportPhy

trait ValuesListPhy extends TextSupportPhy {
  
  
  def getValuesList: List[String]
  
  
}