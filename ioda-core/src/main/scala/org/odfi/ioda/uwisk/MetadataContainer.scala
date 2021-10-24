package org.odfi.ioda.uwisk

import javax.json.{Json, JsonValue}

trait MetadataContainer extends MetadataContainerTrait {

  def getMetadataOption(name:String) = this.metadatasAsScala.find(_.id==name)

  def addMetadataFromValue(name: String, value: String) : MetadataContainerTraitmetadata = {

    val m = this.addMetadata
    m.id = name
    m.value = Json.createValue(value)
    m.`type` = "std:string"

    m

  }

  def addMetadataFromValue(name: String, value: Int): MetadataContainerTraitmetadata = {

    val m = this.addMetadata
    m.id = name
    m.value = Json.createValue(value)
    m.`type` = "std:int"

    m
  }

  def addMetadataFromValue(name: String, value: Double): MetadataContainerTraitmetadata = {
    val m = this.addMetadata
    m.id = name
    m.value = Json.createValue(value)
    m.`type` = "std:double"

    m
  }

  def addMetadataFromValue(name: String, value: Long): MetadataContainerTraitmetadata = {
    val m = this.addMetadata
    m.id = name
    m.value = Json.createValue(value)
    m.`type` = "std:long"

    m
  }
  def addMetadataFromValue(name: String, value: Boolean): MetadataContainerTraitmetadata = {
    val m = this.addMetadata
    m.id = name
    m.value = if (value) JsonValue.TRUE else JsonValue.FALSE
    m.`type` = "std:boolean"

    m
  }


}
