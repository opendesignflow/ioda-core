package org.odfi.ioda.logging

import org.apache.logging.log4j.{Level, LogManager, Logger}
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.{ConfigurationBuilder, ConfigurationBuilderFactory}
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration


trait WithLogger/* extends Logging */ {


  val logger = LogManager.getLogger(getClass)



}
