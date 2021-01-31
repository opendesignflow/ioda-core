package org.odfi.ioda.data.protocols

import org.odfi.indesign.core.heart.{Heart, HeartTask}
import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.env.EnvironmentTraitDataPathTraitRun
import org.odfi.ioda.data.types.DataMessage

import scala.reflect.ClassTag
import org.odfi.ioda.env.EnvironmentTrait
import org.odfi.ioda.env.EnvironmentTraitDataSource

trait Level0Protocol[PT <: PhysicalInterface] extends Protocol with HeartTask[Any] {

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

  def collect = {
    this.dataHarvest(this.connectedPhy.get) match {
      case Some(dm) =>
        this.down(dm)
      case None =>
    }

  }


  /**
   * Only Called if a PHY is connected
   */
  def dataHarvest(phy: PT): Option[DataMessage]

  // Scheduling
  //-------------
  override def doTask: Any =  {
    //println("Collecting")
    this.collect
  }
  def scheduleCollect(delayMS:Long) = {
    this.scheduleEvery = Some(delayMS)
    this.reschedule
    //Heart.pump(this)
  }

}