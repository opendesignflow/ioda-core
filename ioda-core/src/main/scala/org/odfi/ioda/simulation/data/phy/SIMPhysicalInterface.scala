package org.odfi.ioda.simulation.data.phy

import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.simulation.Simulated
import org.odfi.ioda.simulation.SimulationProvider
import org.odfi.ioda.env.EnvironmentTraitSimulationTraitPhysical
import org.odfi.indesign.core.config.ConfigInModel
import org.odfi.ioda.env.EnvironmentTraitDataSourceTraitPhysicalsTraitSimulationPhysical


/**
 * Implementation
 * with ConfigInModel[EnvironmentTraitDataSourceTraitPhysicalsTraitSimulationPhysical]
 */
trait SIMPhysicalInterface extends PhysicalInterface with Simulated  {
  

  this.isSimulated = true

  
  /*override def getId = configModel match {
    case Some(m) => m.re.toString
    case None => super.getId
  }*/

  
}


trait SIMPhysicalInterfaceProvider extends SimulationProvider