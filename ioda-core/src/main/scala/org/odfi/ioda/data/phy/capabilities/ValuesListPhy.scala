package org.odfi.ioda.data.phy.capabilities

import org.odfi.ioda.data.phy.TextSupportPhy

trait ValuesListPhy extends TextSupportPhy {
  
  
  def getValuesList: List[String]
  
  
}