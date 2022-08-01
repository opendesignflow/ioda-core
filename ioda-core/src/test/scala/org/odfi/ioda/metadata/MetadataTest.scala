package org.odfi.ioda.metadata

import org.odfi.ioda.uwisk.MetadataContainer
import org.odfi.ooxoo.lib.json.model.JSONHelper
import org.scalatest.funsuite.AnyFunSuite

class MetadataContainerTest extends MetadataContainer {

}

class MetadataTest extends AnyFunSuite {

  test("Parse metadatas") {
    val container = JSONHelper.fromString[MetadataContainerTest](
      """{
        |"metadatas": [{
        | "id": "test",
        | "value": "hello"
        |}]
        |
        |}""".stripMargin)

    assert(container.metadatasAsScala.nonEmpty)
    println("Test value: "+container.metadatasAsScala.head.value)

  }

}
