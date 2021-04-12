package org.odfi.ioda.instruments.nivisa

import org.odfi.indesign.core.module.IndesignModule
import org.bridj.BridJ
import org.odfi.indesign.core.harvest.Harvest

object VISAModule extends IndesignModule {


  this.onInit {
    BridJ.addLibraryPath("/usr/local/lib64")
  }

  this.onLoad {
    Harvest.addHarvester(VISAHarvester)
  }
}