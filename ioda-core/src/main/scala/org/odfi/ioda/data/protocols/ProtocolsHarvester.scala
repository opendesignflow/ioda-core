package org.odfi.ioda.data.protocols

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.data.protocols.text.ValuesListProtocol
import org.odfi.ioda.data.protocols.matrix.ListToMatrix
 
object ProtocolsHarvester  extends Harvester {
  
  saveToAvailableResources(ValuesListProtocol)
  saveToAvailableResources(ListToMatrix)
}