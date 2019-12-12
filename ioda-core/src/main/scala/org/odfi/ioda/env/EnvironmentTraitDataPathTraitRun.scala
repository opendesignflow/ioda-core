package org.odfi.ioda.env

import org.odfi.ubroker.core.broker.tree.Intermediary
import org.odfi.ioda.data.protocols.Level0Protocol
import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.simulation.data.phy.SIMPhysicalPushInterface
import org.odfi.ioda.data.phy.PushPhysicalInterface

class EnvironmentTraitDataPathTraitRun extends EnvironmentTraitDataPathTraitRunTrait {

  var protocolTree: Option[Intermediary] = None

  override def checkImmediateErrors = {

    /*protocolTree match {
      case None =>
        addImmediateError("Protocol Tree is absent")
      case Some(t) =>
    }*/

    parentReference match {
      case Some(pr) =>
      case None =>
        addImmediateError("No Reference to parent Datapath")
    }

  }

  /**
   *
   */
  /* override def repairErrors = {

    // Create Datapath tree
    //----------------
    this.parentReference match {
      case Some(datapath) =>

        var latest : Option[Intermediary] = None
        datapath.protocolStack.protocols.foreach {
          case protocolDefinition =>

            //-- Create
            val created = protocolDefinition.ensureInstance
            //-- Add to top or latest

        }

      case None =>
    }


  }*/

  /**
   * Collect data from Datasources and pass through datapaths
   * Don't paralellize here
   */
  def collectData = {

    //println(s"PR: "+this.parentReference)
    val dataSourceAndPhys = this.parentReference.get.dataSourceReferences.collect {
      case ds if (ds.getReferencedBuffer.isDefined) =>
        (ds.getReferencedBuffer.get, ds.getReferencedBuffer.get.selectPhy)

    }.collect {
      case (ds, phyOption) if (phyOption.size > 0) => (ds, phyOption)
    }.toList

    dataSourceAndPhys.foreach {
      case (ds, phys) =>

        phys.foreach {
          case phy: SIMPhysicalPushInterface =>

            logFine[EnvironmentTraitDataPathTraitRun](s"Sim Push Phy: " + phy.hashCode())
            phy.triggerPush

          // Push Phy
          case pushPhy: PushPhysicalInterface =>

          // NOrmal Phy
          case phy =>

            logFine[EnvironmentTraitDataPathTraitRun]("Trying Collecting from phy VCID=" + phy.getVCID)
            this.parentReference.get.protocolStack.protocols.lastOption match {
              case Some(p) if (p.getImplementation.isInstanceOf[Level0Protocol[_]]) =>
                if (p.getImplementation.asInstanceOf[Level0Protocol[PhysicalInterface]].checkCompatibility(phy)) {

                  logFine[EnvironmentTraitDataPathTraitRun]("Collecting from phy VCID=" + phy.getVCID)
                  p.getImplementation.asInstanceOf[Level0Protocol[PhysicalInterface]].collectData(ds, phy)
                }
              case other =>
                logFine[EnvironmentTraitDataPathTraitRun](s"Lowest Protocol is not PHY: " + other + " -> " + other.get.getImplementation)
            }
        }
    }

  }
}