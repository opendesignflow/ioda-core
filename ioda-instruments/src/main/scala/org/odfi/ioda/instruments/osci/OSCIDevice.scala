package org.odfi.ioda.instruments.osci

import org.odfi.ioda.data.phy.Device
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ioda.instruments.data.XWaveform

object OSCIDeviceHarvester extends Harvester {

  this.onDeliverFor[OSCIDevice] {
    case d =>
      gather(d)
      true
  }

}

trait OSCIDevice extends Device {

  var osciTriggerPollWaitMs = 10

  // Acquisitation control
  //------------
  
  /**
   * Sets up acquisition for waveform to a certain channel with number of points
   * The implementation should setup the device in a generic way, functional for most usage
   */
  def setupAcquire(channel: Int, points: Int) : Unit
  def selectChannel(channel: Int) : Unit

  def enableSingle: Unit
  def enableRun: Unit

  // Trigger
  //-------------------
  def isTriggered: Boolean

  /**
   * Enable single mode then wait for triggers
   */
  def onTriggered(cl: OSCIDevice => Boolean): Unit = {

    this.enableSingle
    var continue = true
    while (continue) {

      isTriggered match {
        case true =>

          // Call handling closure
          continue = cl(this)

          // Start in single mode again
          this.enableSingle

        case false =>
          Thread.sleep(osciTriggerPollWaitMs)

      }

    }
    this.enableRun
  }

  def getWaveform: XWaveform

}