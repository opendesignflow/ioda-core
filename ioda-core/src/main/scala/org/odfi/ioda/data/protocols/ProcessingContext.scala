package org.odfi.ioda.data.protocols

import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.{ConfigurationBuilder, ConfigurationBuilderFactory}
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration
import org.apache.logging.log4j.{Level, LogManager, Logger}
import org.odfi.indesign.core.harvest.{HarvestedResource, HarvestedResourceDefaultId}

import scala.reflect.ClassTag

class ProcessingContext extends PMetadataContainer with HarvestedResourceDefaultId {


  // Context Objects
  var contextObjects = Map[String,Any]()

  def addContextObject(k:String,o:Any) = {
    this.contextObjects = this.contextObjects + (k -> o)
  }

  def getContextObject(k:String) = this.contextObjects.get(k)

  def getContextObjectAs[T](k: String)(implicit tag:ClassTag[T]) = this.contextObjects.get(k) match {
    case Some( v: T) => Some(v)
    case other => None
  }

  def findContextObjectAs[T](implicit tag:ClassTag[T]) = {
    this.contextObjects.values.collectFirst {
      case v : T => v
    }
  }


  var _logger: Option[Logger] = None
  def logger = _logger match {
    case Some(l) => Some(l)
    case None =>
      LogManager.getLogger()
      LogManager.getLogger(getClass)
      this._logger = Some(LogManager.getLogger(getClass))
      this._logger
  }

  // Logging
  //--------------

  def enterProtocol(p: Protocol) = {

    this._logger = Some(LogManager.getLogger(p.getClass))

  }

  def logInfo(cl: => String) = {

    this.logger match {
      case Some(logger) if (logger.isInfoEnabled()) =>
        logger.info(cl)
      case Some(logger) =>
       // println("Logger defined bu not enabled for info")
      case other =>
    }
    //this.logger.info(cl)
    //logger.info
  }

  def logDebug(cl: => String) = {

    this.logger match {
      case Some(logger) if (logger.isDebugEnabled()) =>
        logger.debug(cl)
      case other =>
    }
    //this.logger.info(cl)
    //logger.info
  }

  def logTrace(cl: => String) = {

    this.logger match {
      case Some(logger) if (logger.isTraceEnabled()) =>
        logger.trace(cl)
      case other =>
    }
    //this.logger.info(cl)
    //logger.info
  }

  def logError(th: Throwable)(cl: => String): Unit = {

    this.logger match {
      case Some(logger) if (logger.isErrorEnabled()) =>
        logger.error(cl, th)
      case other =>
    }
    //this.logger.info(cl)
    //logger.info
  }

  def logError(cl: => String): Unit = {

    this.logger match {
      case Some(logger) if (logger.isErrorEnabled()) =>
        logger.error(cl)
      case other =>
    }
    //this.logger.info(cl)
    //logger.info
  }

  def throwError(str: String) = {
    logError(str)
    sys.error(str)
  }

  def logWarn(cl: => String) = {

    this.logger match {
      case Some(logger) if (logger.isWarnEnabled()) =>
        logger.warn(cl)
      case other =>
    }
    //this.logger.info(cl)
    //logger.info
  }


}