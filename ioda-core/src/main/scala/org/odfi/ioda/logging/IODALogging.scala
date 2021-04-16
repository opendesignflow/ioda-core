package org.odfi.ioda.logging

import org.apache.logging.log4j.{Level, LogManager}
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.spi.{LoggerContext, LoggerContextFactory}

import java.net.URI

object IODALogging {

  def configureDefaultColoredLogging = {

    sys.props.put("log4j.skipJansi", "false")
    sys.props.put("log4j.debug", "false")

    // Create Configu Builder
    //-----------
    val builder = ConfigurationBuilderFactory.newConfigurationBuilder();
    builder.setStatusLevel(Level.INFO);

    // Now the Standard Console Appender
    //-----------
    var appenderBuilder = builder
      .newAppender("console", "CONSOLE")
      .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
      .add(builder.newLayout("PatternLayout")
        .addAttribute("pattern", "%highlight{%d [%c{1.}] %-5level: %msg %throwable}%n"))
    builder.add(appenderBuilder);


    // Create New Root Logger
    //--------------
    builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("console")).addAttribute("additivity", true))


    // Create new Top Context
    //-----------
    val config = builder.build()
    //val topContext = Configurator.initialize(builder.build())
    Configurator.reconfigure(config)
   /* LogManager.setFactory(new LoggerContextFactory {
      override def getContext(fqcn: String, loader: ClassLoader, externalContext: Any, currentContext: Boolean): LoggerContext = {
        topContext
      }

      override def getContext(fqcn: String, loader: ClassLoader, externalContext: Any, currentContext: Boolean, configLocation: URI, name: String): LoggerContext =  {
        topContext
      }

      override def removeContext(context: LoggerContext): Unit = {

      }
    })*/

  }

}
