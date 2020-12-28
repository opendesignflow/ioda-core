package org.odfi.ioda.simulation.lib.matrix

import scala.util.Random
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import org.odfi.ioda.data.protocols.matrix.{DoubleMatrixValueMessage, MatrixValueMessage}
import org.odfi.ioda.simulation.SimulationGeneratorStep




class MatrixSimulationStep extends SimulationGeneratorStep {

  var rows = 1
  var columns = 5

  var minValue = 0.0
  var maxValue = 1024

  var vc : Option[String] = None
  def doTask = {
    println("Generating Matrix value")
    val doubleMessage = new DoubleMatrixValueMessage()
    doubleMessage.initRandom(rows,columns,minValue,maxValue)
    this.down(doubleMessage)
  //  println(s"Doing Matrix Simulation " + getId + " -> " + this.scheduleEvery)

    // Generate Random values
    // Sue rows/columns


   /* var update = new MatrixValueMessage
    update.id = this.name
    update.width  = this.width
    update.height = this.height
    update.virtualChannel = vc
    (0 until this.height.data).foreach {
      row =>
        (0 until this.width.data).foreach {
          column =>

            var value = (Math.abs(Random.nextGaussian()) * this.maxValue).toInt
            var pvalue = update.pixels.add
            pvalue.value = value
            pvalue.x = column
            pvalue.y = row
        }
    }*/
    
    //println(s"Sending Sim data")
    //ILModule.engine.network.dispatch(update)

    /*LocalWebEngine.topViewsIntermediary.intermediaries.foreach {
      case v: SingleViewIntermediary if (v.viewClass.getCanonicalName.startsWith(classOf[TopView].getCanonicalName)) =>
        v.viewPool.foreach {
          case (session, view) =>
            try {
              view.sendBackendMessage(update)
            } catch {
              case e : Throwable => 
                e.printStackTrace()
            }
        }
      case _ =>
    }*/

  }

}