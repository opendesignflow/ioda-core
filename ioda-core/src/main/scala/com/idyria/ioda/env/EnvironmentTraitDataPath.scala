package com.idyria.ioda.env

import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import scala.language.implicitConversions
import com.idyria.osi.ooxoo.core.buffers.datatypes.DateTimeBuffer
import com.idyria.ioda.data.protocols.Level0Protocol
import com.idyria.ioda.data.phy.PhysicalInterface
import com.idyria.ioda.data.protocols.Protocol
import scala.reflect.ClassTag
import org.odfi.indesign.core.heart.HeartTask
import org.odfi.indesign.core.heart.Heart
import org.odfi.indesign.core.heart.DefaultHeartTask
import com.idyria.ioda.data.types.DataMessage
import com.idyria.osi.tea.logging.TLog

class EnvironmentTraitDataPath extends EnvironmentTraitDataPathTrait {

  override def checkImmediateErrors = {

    this.protocolStack.protocols.reverse.zipWithIndex.foreach {
      case (p, i) =>
        p.implementationInstance match {
          case Some(i) if (i == 0 && !i.isInstanceOf[Level0Protocol[_]]) =>
            addImmediateError("Lowest Level of Protocol must be Level0 Protocol")
          case Some(i) if (i == 0 && i.asInstanceOf[Level0Protocol[_]].connectedPhy.isEmpty) =>
            addImmediateError("Level 0 Protocol has no connected Phy")
          case Some(i) =>
          case None =>
            addImmediateError("Protocol is missing implementation instance")
        }
    }

  }

  /**
   *
   */
  override def repairErrors = {

    /**
     * Create instances
     */
    var latest: Option[Protocol] = None
    this.protocolStack.protocols.reverse.foreach {
      case protocolDefinition =>

        //-- Create
        val created = protocolDefinition.ensureInstance

        //-- Add to top or latest
        if (latest.isDefined) {
          latest.get <= created
        }
        latest = Some(created)

    }

    /**
     * Connect Phy
     */
    this.protocolStack.protocols.reverse.headOption match {
      case Some(lp) =>
        lp.getImplementation match {
          case zero: Level0Protocol[PhysicalInterface] =>

          /*val dataSource = dataSourceReference.getReferencedBuffer.get
            
            dataSource.selectPhy match {
              case Some(phy) =>
                zero.connect(phy)
              case None => 
                logWarn("No PHY found to connect to : "+zero.getId)
            }*/

          /*parentReference.get.boards.find {
              b =>
                b.boardId.toString == this.boardRef.boardId.toString
            } match {
              case Some(b) if (b.getPhy.isDefined) =>

                zero.connect(b.getPhy.get)

              case other =>

            }*/

          case other                                   =>
        }
      case None =>
    }

  }

  def pushProtocol[PT <: Protocol](implicit tag: ClassTag[PT]) = {
    val protocol = this.protocolStack.protocols.addFirst
    protocol.implementationType = tag.runtimeClass
    protocol
  }

  def pushProtocolAnd[PT <: Protocol](cl: EnvironmentTraitDataPathTraitProtocolStackTraitProtocol => Any)(implicit tag: ClassTag[PT]): EnvironmentTraitDataPathTraitProtocolStackTraitProtocol = {
    val p = pushProtocol[PT]
    cl(p)
    p
  }

  def pushMessage(dm: DataMessage) = {

    protocolStack.protocols.lastOption match {
     case Some(p) if (p.implementationInstance.isDefined) =>
       //println("Pushing on: "+p.getImplementation)
       p.getImplementation.down(dm)
      case other =>
        logError("Pushing to DataPath with no protocols containing a defined implementation, make sure the global setup has no errors, call repairErrors")
        println("Can't Push")
    }

  }

  // Runs
  //-----------------

  def onRun[RT](id: String)(cl: EnvironmentTraitDataPathTraitRun => RT) = {

    cl(this.runs.findOrCreateByEId(id))

  }

  /**
   * Create a default static run
   * A Static Run starts and stops between a provided custom closure
   */
  def runStatic[RT](cl: EnvironmentTraitDataPathTraitRunTraitExecution => RT) = {

    this.onRun("default-static") {
      run =>
        val execution = run.executions.add
        keepErrorsOn(execution, verbose = true) {
          execution.startThenStop(cl(execution))
        }

    }
  }

  def runCollectStatic = {
    runStatic {
      exec =>
        exec.collectData
    }
  }

  def runEvery[RT](everyTime: Long)(cl: (HeartTask[Unit], EnvironmentTraitDataPathTraitRunTraitExecution) => RT): HeartTask[Unit] = {

    // Prepare Run and Execution
    this.onRun(s"default-every-$everyTime") {
      run =>
        val execution = run.executions.add

        // Prepare task
        execution.startNow
        val task = new DefaultHeartTask {

          this.scheduleEvery = Some(everyTime)

          def doTask = {

            keepErrorsOn(execution, verbose = true) {
              cl(this, execution)
            }

          }

          this.onClean {
            execution.stopNow
          }
        }

        // Start
        Heart.pump(task)
        task

    }
  }

  def runEveryForCount[RT](everyTime: Long, targetCount: Long)(cl: EnvironmentTraitDataPathTraitRunTraitExecution => RT): HeartTask[Unit] = {

    var count = 0
    var t = this.runEvery(everyTime) {
      case (task, exec) =>
        try {
          cl(exec)
        } finally {
          count += 1
          if (count >= targetCount) {
            task.kill
          }
        }
    }

    t
  }

  def runCollectEvery(everyTime: Long) = {
    this.runEvery(everyTime) {
      case (task, exec) =>
        logFine[EnvironmentTraitDataPath]("Collecting...")
        exec.collectData
    }
  }

  def runCollectEveryForCount(everyTime: Long, targetCount: Long) = {
    this.runEveryForCount(everyTime, targetCount) {
      exec =>
        exec.collectData
    }
  }

}
