package org.odfi.ioda.env

import org.odfi.ooxoo.core.buffers.structural.xattribute
import org.odfi.ooxoo.core.buffers.structural.xelement
import scala.language.implicitConversions
import org.odfi.ooxoo.core.buffers.datatypes.DateTimeBuffer
import org.odfi.ioda.data.protocols.Level0Protocol
import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.data.protocols.Protocol
import scala.reflect.ClassTag
import org.odfi.indesign.core.heart.HeartTask
import org.odfi.indesign.core.heart.Heart
import org.odfi.indesign.core.heart.DefaultHeartTask
import org.odfi.ioda.data.types.DataMessage
import org.odfi.tea.logging.TLog

class EnvironmentTraitDataPath extends EnvironmentTraitDataPathTrait {

  override def checkImmediateErrors = {

    this.protocolStack.protocols.reverse.zipWithIndex.foreach {
      case (p, i) =>
        p.implementationInstance match {
          case Some(p) if (i == 0 && !p.isInstanceOf[Level0Protocol[_]]) =>
            addImmediateError("Lowest Level of Protocol must be Level0 Protocol")
          case Some(p) if (i == 0 && p.asInstanceOf[Level0Protocol[_]].connectedPhy.isEmpty) =>
            addImmediateError("Level 0 Protocol has no connected Phy")
          case Some(p) =>
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
          case zero: Level0Protocol[_] =>
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
        logError[EnvironmentTraitDataPath]("Pushing to DataPath with no protocols containing a defined implementation, make sure the global setup has no errors, call repairErrors")
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
