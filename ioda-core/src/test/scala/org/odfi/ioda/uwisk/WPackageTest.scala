package org.odfi.ioda.uwisk


import org.apache.commons.io.Charsets
import org.apache.commons.io.input.ReaderInputStream
import org.scalatest.funsuite.AnyFunSuite

import java.io.{InputStreamReader, StringReader}
import java.nio.charset.StandardCharsets

class WPackageTest extends AnyFunSuite {

  val testPackage =
    """
      |name: Hyprint Globals (Hyprint Specifics)
      |namespace: /
      |id: globals
      |pipelines:
      |  - id: organisation.add
      |    triggers:
      |      - /system/#global/@organisation.add
      |    steps:
      |      - id: /system/#notify/ms365.teams.channel.message
      |        metadatas:
      |          - id: teams.message
      |            value: Organisation ${organisationID} Added
      |  - id: organisation.remove
      |    triggers:
      |      - /system/#global/@organisation.remove
      |    steps:
      |      - id: /system/#notify/ms365.teams.channel.message
      |        metadatas:
      |          - id: teams.message
      |            value: Organisation ${organisationID} Removed
      |""".stripMargin


  test("T") {
    val reader = new StringReader(testPackage)
    val is = new ReaderInputStream(reader,StandardCharsets.UTF_8)
    val parsed = wpackage(is)
  }



}
