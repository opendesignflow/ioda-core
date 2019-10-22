package com.idyria.ioda.data.protocols

import org.odfi.indesign.core.harvest.Harvester
import com.idyria.ioda.data.protocols.text.ValuesListProtocol
import com.idyria.ioda.data.protocols.matrix.ListToMatrix
 
object ProtocolsHarvester  extends Harvester {
  
  saveToAvailableResources(ValuesListProtocol)
  saveToAvailableResources(ListToMatrix)
}