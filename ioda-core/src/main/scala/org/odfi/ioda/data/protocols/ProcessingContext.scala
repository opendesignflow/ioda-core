package org.odfi.ioda.data.protocols

import org.apache.logging.log4j.{Level, LogManager, Logger}
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration

class ProcessingContext extends MetadataContainer {

  /*var verbose = true
  var verboseLevel = Level.INFO

  var loggerContext: Option[LoggerContext] = None
  var builder: ConfigurationBuilder[BuiltConfiguration] = _*/
  var logger: Option[Logger] = None


  // Logging
  //--------------

  def enterProtocol(p: Protocol) = {

    this.logger = Some(LogManager.getLogger(p.getClass))

    // Init Context
    //-------------------
    /*if (loggerContext.isEmpty) {
      sys.props.put("log4j.skipJansi", "false")
      sys.props.put("log4j.debug", "false")

      builder = ConfigurationBuilderFactory.newConfigurationBuilder();
      builder.setStatusLevel(Level.INFO);

      var appenderBuilder = builder
        .newAppender("console", "CONSOLE")
        .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
        .add(builder.newLayout("PatternLayout")
          .addAttribute("pattern", "%highlight{%d [%c{1.}] %-5level: %msg %throwable}%n"))
      builder.add(appenderBuilder);

      /*  builder.add(builder.newLogger("ioda.core.protocol:", Level.INFO)
          .add(builder.newAppenderRef("console"))
          .addAttribute("additivity", false));*/

      builder.add(builder.newRootLogger(verboseLevel).add(builder.newAppenderRef("console")).addAttribute("additivity", true))
      // builder.newLogger(p.getLoggerId).build()

      loggerContext = Some(Configurator.initialize(builder.build()))
    }*/

    // Create Logger if needed
    //--------------------
    // println(s"LoggerCtx for ${p.getLoggerId}: " + loggerContext)
    //loggerContext.get.updateLoggers()

    /*val protocolLogger = loggerContext.get.hasLogger(p.getLoggerId) match {
      case true =>
        loggerContext.get.getLogger(p.getLoggerId)
      case false =>


        val protocolLogger = builder.newLogger(p.getLoggerId, verboseLevel)
                                  .add(builder.newAppenderRef("console"))
        loggerContext.get.setConfiguration(builder.build())
        loggerContext.get.getLogger(p.getLoggerId)
    }*/

    /* loggerContext.get.getLogger(p.getId) match {
       case null =>
         // println(s"Creating Protocol Logger")

       case other =>
         // println(s"Got Logger for "+p.getId+" -> "+other)
         other
     }*/

    //this.logger = Some(protocolLogger)


    // this.loggerImpl = Some(logctx.getLogger("ioda.core.protocol:" + getId))

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
        logger.info(cl)
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