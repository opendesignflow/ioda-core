package com.idyria.ioda.simulation.data.phy

import com.idyria.ioda.data.phy.PhysicalInterface
import com.idyria.ioda.simulation.Simulated
import com.idyria.ioda.simulation.SimulationProvider
import com.idyria.ioda.env.EnvironmentTraitSimulationTraitPhysical
import org.odfi.indesign.core.config.ConfigInModel
import com.idyria.ioda.env.EnvironmentTraitDataSourceTraitPhysicalsTraitSimulationPhysical


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