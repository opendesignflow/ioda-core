package com.idyria.ioda.data.phy

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId
import org.odfi.indesign.core.config.ConfigSupport
import java.io.OutputStream
import java.io.InputStream
import org.odfi.indesign.core.config.ConfigInModel
import org.odfi.indesign.core.config.model.CommonConfig

trait PhysicalInterface extends HarvestedResourceDefaultId with ConfigInModel[CommonConfig] {

  var isSimulated = false

  // Open Closed
  //-----------------
  def open: Boolean
  def close: Boolean

  /**
   * To be overriden
   * 
   */
  def isOpenedNoErrors = true

  // VCID
  //------------
  def getVCID = configModel match {
    case Some(m) if (m.getString("vcid").isDefined) => Some(m.getString("vcid").get)
    case other                                       => None
  }

}

trait ManagedOpenClosePhy extends PhysicalInterface {

  var isOpened = false
  var openError: Option[Throwable] = None

  def open = {
    this.synchronized {
      if (!isOpened) {
        try {
          doOpen
          isOpened = true
          this.@->("opened")
          true
        } catch {
          case err: Throwable =>
            this.openError = Some(err)
            logWarn("Error while opening PHY : " + getId)
            false
        }

      } else {
        true
      }
    }
  }

  /**
   * This will try to open if not opened yet, the open function will catch errors and keep them here
   */
  override def isOpenedNoErrors = openError match {
    case Some(err) =>
      false
    case None if (isOpened == false) =>
      open
    case None if (isOpened == true) =>
      true

  }

  def onOpened(cl: => Any) = {
    this.on("opened")(cl)
  }

  def close = {
    this.synchronized {
      if (isOpened) {
        doClose
        isOpened = false
        this.@->("closed")
        true
      } else {
        false
      }
    }
  }

  def onClosed(cl: => Any) = {
    this.on("closed")(cl)
  }

  def doOpen
  def doClose

}