package org.odfi.ioda.data.protocols

import org.apache.logging.log4j.{Level, LogManager, Logger}
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration

class ProcessingContext extends PMetadataContainer {

  /*var verbose = true
  var verboseLevel = Level.INFO

  var loggerContext: Option[LoggerContext] = None
  var builder: ConfigurationBuilder[BuiltConfiguration] = _*/
  var _logger: Option[Logger] = None
  def logger = _logger match {
    case Some(l) => Some(l)
    case None =>
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