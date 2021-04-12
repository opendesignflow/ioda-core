package org.odfi.ioda.instruments.nivisa

import org.odfi.indesign.core.harvest.Harvest
import org.odfi.ioda.instruments.scpi.SCPIHarvester

object TrySCPI extends App {
  
  Harvest.addHarvester(SCPIHarvester)

  
  
  Harvest.run
  
  Harvest.printHarvesters
}