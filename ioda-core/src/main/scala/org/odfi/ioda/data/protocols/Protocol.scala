package org.odfi.ioda.data.protocols

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId
import org.odfi.indesign.core.config.ConfigInModel
import org.odfi.indesign.core.config.model.CommonConfig
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.ubroker.core.broker.tree.MessageIntermediary
import org.odfi.ioda.data.types.DataMessage
import scala.reflect.ClassTag
import scala.reflect._
import org.odfi.ubroker.core.message.Message
import scala.util.matching.Regex
import org.odfi.ubroker.core.broker.tree.single.SingleMessageIntermediary
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Configurator

trait Protocol extends MessageIntermediary[DataMessage] with ConfigInModel[CommonConfig] {

  //val ttag = classTag[DataMessage]
  // Message Type
  //-----------------
  def messageType = classOf[DataMessage]

  var verbose = false

  def getLoggerId  = this.getId match {
    case null =>
      getClass.getCanonicalName
    case other =>
      other
  }

  /*def initConfig = {
    this.configModel match {
      case None =>
        this.setConfigModel(new CommonConfig)
      case other =>
    }
  }*/

  //override def getId = super.getId

  this.acceptDown[DataMessage] {
    pm =>
      !pm.drop
  }

  /**
   * Processing of message
   */
  override def down(message: Message) = {

    message match {

      case dm: DataMessage =>

        /*this.loggerImpl match {
          case None =>
            this.loggerImpl = Some(LoggerFactory.getLogger(getClass))
          case Some(logger) =>
        }*/
        
        // Select Correct Message
        //---------------
        var selectedMessage = dm.getTransformedMessage

        // Processing Context Update
        //-------------------
        selectedMessage.processingContext.enterProtocol(this)
        selectedMessage.processingContext.logDebug("Entering Protocol Processing")

        // Filtering
        //------------------
        val vcidFilters = this.config match {
          case Some(config) if (config.getString("vcidFilters").isDefined) => config.getString("vcidFilters").get.split(',').map(_.r).toList
          case other => List[Regex]()
        }

        //-- filter
        var accept = vcidFilters.size match {

          //-- NO Filters, always process
          case 0 => true

          //-- No VCID and filters defined, always reject
          case filtersDefinedCount if (selectedMessage.virtualChannel.isEmpty) => false

          case filtersDefinedCount =>

            //-- Search for a matching filter
            vcidFilters.find {
              f => f.findFirstMatchIn(selectedMessage.virtualChannel.get).isDefined
            }.isDefined

        }


        if (selectedMessage.drop) {
          accept = false
        }

       // println(s"Message in ${toString} accepted=$accept")

        accept match {

          // Accept Message
          case true =>
            selectedMessage.processingContext.logDebug("Processing Message")

            super.down(selectedMessage)
          // pass this protocol
          case false =>

            selectedMessage.ignoreCurrentIntermediary = true
            super.down(selectedMessage)

        }

      case other =>
        sys.error("Only DataMessages are supported")
    }

  }

  this.onError {
    case (message, error) =>
      message.processingContext.logError(error)("An error occured while processing protocol step")
  }

  /**
   *
   * Post Processing of message
   */
  override def downTree(message: Message) = {
    //println("Downtree of :"+message)

    //-- Determine Message
    var selectedMessage =selectMessage(message)
    super.downTree(selectedMessage)

    // After subtree
    //-------------------
    //selectedMessage.processingContext.enterProtocol(this)
    //selectedMessage.processingContext.logInfo("After Down Tree")

  }

  private def selectMessage(message: Message) = {
    //-- Determine Message
    var selectedMessage = message match {

      case dm: DataMessage if (dm.nextMessage.isDefined) =>

        dm.nextMessage.get

      case m: DataMessage => m
      case other =>
        sys.error("Only DataMessages are supported")
    }

    if (selectedMessage.hasErrors) {
      logWarn[Protocol]("Current Message has errors, maybe from a previous protocol")
      selectedMessage.errors.foreach {
        e =>
          logWarn[Protocol]("-> " + e.getLocalizedMessage)
      }
    }
    
    selectedMessage
  }

  // Quick Sub Protocol
  //-----------------------
  def subProtocol[DT <: DataMessage](name: String)(cl: DT => Unit)(implicit tag: ClassTag[DT]) = {

    val subId = getId + "/" + name
    this <= new Protocol {
      def getId = subId

      this.onDownMessage {
        case d: DT =>
          //println(s"On Quick Sub protocol")
          cl(d)
      }

    }

  }

}

trait ProtocolWithId extends Protocol with HarvestedResourceDefaultId {

  //override def getId = super.getId
}