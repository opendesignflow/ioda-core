package org.odfi.ioda.data.types

import org.odfi.ooxoo.core.buffers.structural.xelement

import scala.language.implicitConversions
import org.odfi.ioda.data.protocols.params.ParamValue
import org.odfi.ioda.data.protocols.{PMetadataContainer, ProcessingContext}

import scala.reflect.ClassTag
import org.odfi.tea.listeners.ListeningSupport
import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId

@xelement(name = "DataMessage")
trait DataMessage extends DataMessageTrait with ListeningSupport with HarvestedResourceDefaultId with PMetadataContainer {


  var __nextMessage: Option[_ <: DataMessage] = None

  def nextMessage_=[T <: DataMessage](next: T) = {

    //-- Copy Virtual Channel
    next.virtualChannel match {
      case None if (this.virtualChannel.isDefined) =>
        next.virtualChannel = this.virtualChannel
      case other =>

    }

    //-- Transfer metadatas
    next.metadatas = next.metadatas :++ (this.metadatas)
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



  def addUID(uid: String) = {
    this.addMetadataFromValue("ioda.uid", uid)
  }

  def getUID = this.getMetadataString("ioda.uid")

  def getVCAndUID = this.getVirtualChannelAsString + (getUID match {
    case Some(p) => "." + p.toString()
    case None => ""
  })

}
