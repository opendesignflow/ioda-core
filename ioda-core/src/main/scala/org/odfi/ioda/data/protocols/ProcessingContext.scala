package org.odfi.ioda.data.protocols

import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration

class ProcessingContext extends MetadataContainer {

  var verbose = false
  var verboseLevel = Level.INFO

  var loggerContext: Option[LoggerContext] = None
  var builder : ConfigurationBuilder[BuiltConfiguration]= _
  var logger: Option[Logger] = None

  // Logging
  //--------------

  def enterProtocol(p: Protocol) = {
    if (verbose) {

      // Init Context
      //-------------------
      if (loggerContext.isEmpty) {
        sys.props.put("log4j.skipJansi", "false")

         builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.INFO);

        var appenderBuilder = builder.newAppender("console", "CONSOLE").addAttribute(
          "target",
          ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout")
          .addAttribute("pattern", "%highlight{%d [%c{1.}] %-5level: %msg %throwable}%n"));
        builder.add(appenderBuilder);

      /*  builder.add(builder.newLogger("ioda.core.protocol:", Level.INFO)
          .add(builder.newAppenderRef("console"))
          .addAttribute("additivity", false));*/
        
        builder.add(builder.newRootLogger(verboseLevel).add(builder.newAppenderRef("console")).addAttribute("additivity", false))

        loggerContext = Some(Configurator.initialize(builder.build()))
      }
      
      // Create Logger if needed
      //--------------------
      val protocolLogger = loggerContext.get.getLogger(p.getId) match {
        case null => 
         // println(s"Creating Protocol Logger")
           val protocolLogger = builder.newLogger(p.getId, Level.INFO).add(builder.newAppenderRef("console"))
            loggerContext.get.setConfiguration(builder.build())
            loggerContext.get.reconfigure()
            loggerContext.get.getLogger(p.getId)
        case other => 
         // println(s"Got Logger for "+p.getId+" -> "+other)
          other
      }
      
      this.logger = Some(protocolLogger)
  

      // this.loggerImpl = Some(logctx.getLogger("ioda.core.protocol:" + getId))
    }
  }

  def logInfo(cl: => String) = {

    this.logger match {
      case Some(logger) if (logger.isInfoEnabled()) =>
        logger.info(cl)
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
  
  def logError(th:Throwable)(cl: => String) : Unit = {

    this.logger match {
      case Some(logger) if (logger.isErrorEnabled()) =>
        logger.error(cl,th)
      case other =>
    }
    //this.logger.info(cl)
    //logger.info
  }
  
  def logError(cl: => String) : Unit = {

    this.logger match {
      case Some(logger) if (logger.isErrorEnabled()) =>
        logger.error(cl)
      case other =>
    }
    //this.logger.info(cl)
    //logger.info
  }
  
  def throwError(str:String)  = {
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