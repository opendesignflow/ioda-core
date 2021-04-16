package org.odfi.ioda.logging

import org.apache.logging.log4j.LogManager

object Sandbox extends App {

  println("Test..")

  IODALogging.configureDefaultColoredLogging
  LogManager.getLogger().info("Hello world")
  println("Done...")
  Thread.sleep(200)


}
