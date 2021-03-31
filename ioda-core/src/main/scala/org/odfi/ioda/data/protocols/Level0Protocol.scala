package org.odfi.ioda.data.protocols

import org.odfi.indesign.core.heart.{Heart, HeartTask}
import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.env.EnvironmentTraitDataPathTraitRun
import org.odfi.ioda.data.types.DataMessage

import scala.reflect.ClassTag
import org.odfi.ioda.env.EnvironmentTrait
import org.odfi.ioda.env.EnvironmentTraitDataSource
import org.odfi.ioda.pipelines.ScheduleCollectPipelineTrait

trait Level0Protocol[PT <: PhysicalInterface] extends Protocol with HeartTask[Any]  with ScheduleCollectPipelineTrait{

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
        val e = s"Connecting PHY ${p.getClass} is not compatible with required ${tag.runtimeClass}"
        println("ERR: "+e)
        addImmediateError(e)
    }
    
  }

  def disconnectPhy = {
    connectedPhy match {
      case Some(phy) =>
        this.connectedPhy = None
      case None =>
    }
  }

  def collectData(datasource:EnvironmentTraitDataSource,phy: PT) = {
        keepErrorsOn(this, true) {
          dataHarvest(phy) match {
            case None =>
              //println("No Data fetched from Phy")
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
   * Runs based on scheduling to collect some data
   * This never fails and repeats at the scheduled rate
   */
  def collect = {

    keepErrorsOn(this,true) {
      println("Collecting: "+this.connectedPhy)
      this.connectedPhy match {
        case Some(phy) =>
          this.dataHarvest(phy) match {
            case Some(dm) =>
              this.down(dm)
            case None =>
          }
        case None =>
      }
    }



  }


  /**
   * Only Called if a PHY is connected
   */
  def dataHarvest(phy: PT): Option[DataMessage]

  // Scheduling
  //-------------
  /*override def doTask: Any =  {
    //println("Collecting")
    this.collect
  }*/
  /*def scheduleCollect(delayMS:Long) = {
    this.scheduleEvery = Some(delayMS)
    this.reschedule
    //Heart.pump(this)
  }*/

}