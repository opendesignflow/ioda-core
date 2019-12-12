package org.odfi.ioda.data.types

import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import scala.language.implicitConversions
import org.odfi.ioda.data.protocols.params.ParamValue
import org.odfi.ioda.data.protocols.ProcessingContext
import scala.reflect.ClassTag
import org.odfi.tea.listeners.ListeningSupport
import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId

@xelement(name = "DataMessage")
trait DataMessage extends DataMessageTrait with ListeningSupport with HarvestedResourceDefaultId {



  var __nextMessage: Option[_ <: DataMessage] = None

  def nextMessage_=[T <: DataMessage](next: T) = {

    //-- Copy Virtual Channel
    next.virtualChannel match {
      case None if (this.virtualChannel.isDefined) =>
        next.virtualChannel = this.virtualChannel
      case other =>

    }

    //-- Transfer metadatas
    next.metadata = next.metadata ++ this.metadata
    next.processingContext = processingContext

    this.__nextMessage = Some(next)

    // Set Previous Message, force next message to have this message as parent
    //--------------------
    next.parentResource = None
    this.addDerivedResource(next)

    // Signal
    //-----------
    this.@->("message.next.set", next)
  }
  def nextMessage = __nextMessage

  def onNextMessageOfType[T](cl: T => Unit)(implicit tag: ClassTag[T]) = {

    this.onMatch("message.next.set") {
      case message: T =>
        cl(message)
      case other => 
    }

  }

  /**
   * Follows the next Message trai until the last transformation
   */
  def getTransformedMessage = {

    var currentMessage = this
    while (currentMessage.nextMessage.isDefined) {
      currentMessage = currentMessage.nextMessage.get
    }

    currentMessage

  }

  // Processing Context
  //-------------------
  var processingContext = new ProcessingContext

  // Drop
  //-----------
  var drop = false

  // Virtual Channel
  //---------------
  def isOnVirtualChannel(str: String) = {

    this.virtualChannel match {
      case Some(vc) if (vc == str) => true
      case other => false
    }

  }

  // Metadata
  //--------------
  var metadata = Map[String, ParamValue]()

  def addMetadata(name: String, value: Any): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: String): ParamValue = {

    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: Int): ParamValue = {

    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: Double): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv
  }

  def addMetadata(name: String, value: Long): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv
  }
  def addMetadata(name: String, value: Boolean): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv
  }

  def getMetadata(name: String): Option[ParamValue] = {
    this.metadata.get(name)
  }

  def getMetadataOfType[T](name: String)(implicit tag: ClassTag[T]): Option[T] = {
    getMetadata(name) match {
      case Some(pv) if (pv.isOfType[T]) => Some(pv.asType[T])
      case other => None
    }
  }

  def getMetadataBoolean(name: String): Option[Boolean] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isBoolean) =>
        Some(m.asBoolean)
      case other =>
        None
    }
  }
  
  def getMetadataString(name: String): Option[String] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isString) =>
        Some(m.asString)
      case other =>
        None
    }
  }

  def addUID(uid: String) = {
    this.addMetadata("ioda.uid", uid)
  }

  def getUID = this.getMetadata("ioda.uid")

  def getVCAndUID = this.getVirtualChannelAsString + (getUID match {
    case Some(p) => "." + p.value.toString()
    case None => ""
  })

}
