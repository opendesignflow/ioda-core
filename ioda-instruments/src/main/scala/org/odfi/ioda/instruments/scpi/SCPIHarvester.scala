package org.odfi.ioda.instruments.scpi

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.instruments.scpi.rhodeschwarz.RhodeSchwarzVCPHarvester
import org.odfi.ioda.instruments.scpi.serial.SCPIVCPHarvester

object SCPIHarvester extends Harvester {

  this.addChildHarvester(SCPIVCPHarvester)
  
}
