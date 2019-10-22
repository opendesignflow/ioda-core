package com.idyria.ioda.env

import com.idyria.ioda.simulation.data.phy.SIMPhysicalInterface

class EnvironmentTraitSimulation extends EnvironmentTraitSimulationTrait {

  /**
   * Add a simulation Physical
   */
  def addPhysical(id: String)(cl: EnvironmentTraitSimulationTraitPhysical => Any) = {
    this.physicals.find(_.virtualId.toString() == id) match {
      case Some(p) => cl(p)
      case None =>
        this.physicals.addRollbackOnError {
          phy =>
            phy.virtualId = id: String
            cl(phy)
        }
    }

  }

  def removePhysical(interface: EnvironmentTraitSimulationTraitPhysical) = {
    this.physicals -= interface
    interface.deleteImplementation
  }

}