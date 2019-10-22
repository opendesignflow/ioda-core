package com.idyria.ioda.simulation

import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId

/**
 * Trait to help register Simulation INterface and devices providers
 */
trait SimulationProvider  extends HarvestedResourceDefaultId{
  
  
  /**
   * returns List[(id,name)]
   */
  def listSimulationObjects : List[(String,String,Class[_ <: Simulated])]
  
}