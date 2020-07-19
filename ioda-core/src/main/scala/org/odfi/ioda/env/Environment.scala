
package org.odfi.ioda.env

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.IODA
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.data.phy.PhysicalInterfaceHarvester
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterface

@xelement
class Environment extends EnvironmentTrait {

  def getId = name

  // Data SOurces
  //---------------

  /**
   * Add a board, return existing if id exists
   */
  def addDataSource(id: String) = {
    this.dataSources.findOrCreateByEId(id)
  }

  /**
   * Board created if necessary
   */
  def onDataSource[T](id: String)(cl: EnvironmentTraitDataSource => T) = {

    cl(addDataSource(id))
    
  }

  // Data Path
  //--------------
  def onDataPath[T](id: String)(cl: EnvironmentTraitDataPath => T) = {
    cl(this.dataPaths.findOrCreateByEId(id))
  }

  // Physical
  //------------

  def importGlobalPhysicalPhy(id: String) = {
    val gphysical = this.globalPhysicals.findOrCreateByEId(id)

    //-- Try to connect
    connectGlobalPhysical(gphysical)

    gphysical
  }

  /**
   * try to connect all global physicals using harvesters
   */
  def connectAllGlobalPhysicals = {

    this.globalPhysicals.filterNot(_.isConnected).foreach {
      gp =>
        connectGlobalPhysical(gp)
    }
  }

  def connectGlobalPhysical(gp: EnvironmentTraitGlobalPhysical) = {
    PhysicalInterfaceHarvester.findPhysicalInterfaces.find(p => p.getId == gp.eid.toString) match {
      case Some(phy) =>
        gp.implementationInstance = Some(phy)
        Some(phy)
      case None =>
        None
    }
  }

  /**
   * Search for a datasource that references the provided global physical interface
   */
  def usesGlobalPhysicalInterface(p: PhysicalInterface) = {
    this.dataSources.find {
      ds =>

        ds.usesGlobalPhysicalInterface(p)

    }.isDefined
  }
  
  
  def createGlobalPhysical[T](id:String)(cl: EnvironmentTraitGlobalPhysical => T) = {
   
    cl(this.globalPhysicals.findOrCreateByEId(id))
  }

  // Simulation
  //---------------

  /**
   * Returns the declared Physical
   */
  def getSimulationPhysicals = {
    this.dataSources.map {
      ds =>
        ds.physicalsOption match {
          case Some(ph) => ph.simulationPhysicals
          case None     => List()
        }
    }.flatten.toList
  }

  /**
   * Returns the Physical Interfaces defined for simulation,
   * which are not associated with a board
   */
  def getUnusedSimulationPhysical = {

    /*this.simulation.physicals.filterNot {
      simphysical => 
        this.boards.find {
          board => 
            //board.physicalOption.isDefined && board.physical.simulationRefIdOption.isDefined && board.physical.simulationRefId.toString==simphysical.virtualId.toString
            board.physicalOption.isDefined && board.physical.simulationRefIdOption == Some(simphysical.virtualId.toString)
        }.isDefined
    }**
    * List()
    */
    List()

  }

  /* /**
   * Returns the Globally available physical interfaces and the locally defined one for simulation
   */
  def getAllAvailablePhysicalInterfaces = {
    
    PhysicalInterfaceHarvester.getUnusedPhysicalDevices ++ getUnusedSimulationPhysical.filter(i => i.implementationInstance.isDefined).map(i => i.implementationInstance.get)
  }
  
  def getAllPhysicalInterfaces = {
    
    PhysicalInterfaceHarvester.getPhysicalDevices ++ getUnusedSimulationPhysical.filter(i => i.implementationInstance.isDefined).map(i => i.implementationInstance.get)
  }*/

  /**
   * Returns the available physical interface matching provided ID
   * Simulated or not
   */
  /*def getAvailablePhysicalInterfaceById(id:String) = {
    getAllAvailablePhysicalInterfaces.find {
      case sim : SIMPhysicalInterface if (sim.getId==id) => true
      case std : PhysicalInterface if(std.getId == id) => true
      case other => false
    }
  }
  
  /**
   * Returns the available physical interface matching provided ID
   * Simulated or not
   */
  def getPhysicalInterfaceById(id:String) = {
    getAllAvailablePhysicalInterfaces.find {
      case sim : SIMPhysicalInterface if (sim.getId==id) => true
      case std : PhysicalInterface if(std.getId == id) => true
      case other => false
    }
  }*/
  
  // Cleaning
  //-------------------
  def mrproper = {
    this.dataPaths.clear()
    this.dataSources.clear()
    this.globalPhysicals.clear()
  }

}