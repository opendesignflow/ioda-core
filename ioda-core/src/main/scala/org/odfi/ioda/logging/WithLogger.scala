package org.odfi.ioda.logging

import org.apache.logging.log4j.{Level, LogManager, Logger}
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.{ConfigurationBuilder, ConfigurationBuilderFactory}
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration

trait WithLogger {


  var verboseLevel = Level.INFO

  var loggerContext: Option[LoggerContext] = None
  var builder: ConfigurationBuilder[BuiltConfiguration] = _
  var _logger: Option[Logger] = None

  def logger = _logger match {
    case Some(l) => l
    case None =>
      try {
        LogManager.getLogger(getClass)
        /*LogManager.getLogger(getClass.getName).atLevel)
        _logger = Some(LogManager.getLogger(getClass.getName))
        _logger.get*/
       // LogManager.getContext(true).getLogger(getClass.getName)
        /*println("Creatting logger")
        // Init Context
        //-------------------
        if (loggerContext.isEmpty) {

          println("A")
          sys.props.put("log4j.skipJansi", "false")
          println("A")
          builder = ConfigurationBuilderFactory.newConfigurationBuilder();
          builder.setStatusLevel(Level.INFO);
          println("A")
          var appenderBuilder = builder.newAppender("console", "CONSOLE")
            .addAttribute(
              "target",
              ConsoleAppender.Target.SYSTEM_OUT)
            .add(builder.newLayout("PatternLayout")
              .addAttribute("pattern", "%highlight{%d [%c{1.}] %-5level: %msg %throwable}%n"));
          builder.add(appenderBuilder);
          println("A")

          builder.add(builder.newRootLogger(verboseLevel).add(builder.newAppenderRef("console")).addAttribute("additivity", false))
          println("A")
          loggerContext = Some(Configurator.initialize(builder.build()))
          println("A")
        }

        println("Creatting logger")
        // Create Logger if needed
        //--------------------
        val protocolLogger = loggerContext.get.getLogger(getClass.getCanonicalName) match {
          case null =>
            // println(s"Creating Protocol Logger")
            val protocolLogger = builder.newLogger(getClass.getCanonicalName, Level.INFO).add(builder.newAppenderRef("console"))
            loggerContext.get.setConfiguration(builder.build())
            loggerContext.get.reconfigure()
            loggerContext.get.getLogger(getClass.getCanonicalName)
          case other =>
            // println(s"Got Logger for "+p.getId+" -> "+other)
            other
        }
        println("Creatting logger")
        _logger = Some(protocolLogger)

        protocolLogger*/
      } catch {
        case e: Throwable =>
          e.printStackTrace()
          throw e
      }
  }

}
