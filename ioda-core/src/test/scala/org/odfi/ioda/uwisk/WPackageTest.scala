package org.odfi.ioda.uwisk

import org.codehaus.plexus.util.StringInputStream
import org.scalatest.funsuite.AnyFunSuite

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
    val parsed = wpackage(new StringInputStream((testPackage)))
  }



}
