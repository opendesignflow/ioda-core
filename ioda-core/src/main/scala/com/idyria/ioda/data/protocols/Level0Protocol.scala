package com.idyria.ioda.data.protocols

import com.idyria.ioda.data.phy.PhysicalInterface
import com.idyria.ioda.env.EnvironmentTraitDataPathTraitRun
import com.idyria.ioda.data.types.DataMessage
import scala.reflect.ClassTag
import com.idyria.ioda.env.EnvironmentTrait
import com.idyria.ioda.env.EnvironmentTraitDataSource

trait Level0Protocol[PT <: PhysicalInterface] extends Protocol {

  // Phy
  //-----------
  var connectedPhy: Option[PT] = None

  def checkCompatibility(p:PhysicalInterface)(implicit tag : ClassTag[PT]) = {
    logFine[Level0Protocol[_]](s"Checking phy ${p.getClass} against ${tag.runtimeClass}")
    p match {
      case tag(p) => true
      case other => false
    }
  }
  
  def connect(p: PT)(implicit tag : ClassTag[PT])  = {
    checkCompatibility(p) match {
      case true => 
        connectedPhy = Some(p)
      case false => 
        addImmediateError(s"Connecting PHY ${p.getClass} is not compatible with required ${tag.runtimeClass}")
    }
    
  }

  def collectData(datasource:EnvironmentTraitDataSource,phy: PT) = {
        keepErrorsOn(this, true) {
          dataHarvest(phy) match {
            case None =>
            case Some(m) =>
              m.virtualChannel = phy.getVCID match {
                case Some(vcid) => Some(vcid)
                case None => Some(datasource.eid.toString)
              }
              this.down(m)

          }
        }
  }


  /**
   * Only Called if a PHY is connected
   */
  def dataHarvest(phy: PT): Option[DataMessage]

}