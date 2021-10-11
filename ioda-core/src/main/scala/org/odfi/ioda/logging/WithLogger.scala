package org.odfi.ioda.logging

import org.apache.logging.log4j.{Level, LogManager, Logger}
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.{ConfigurationBuilder, ConfigurationBuilderFactory}
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration
import org.apache.logging.log4j.scala.Logging

trait WithLogger extends Logging  {


  //def logger = LogManager.getLogger(getClass)



}
