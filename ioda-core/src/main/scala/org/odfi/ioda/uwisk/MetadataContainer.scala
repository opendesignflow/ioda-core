package org.odfi.ioda.uwisk

import org.odfi.ioda.json.JsonExtensions.JsonValueHelperMethods

import javax.json.{Json, JsonValue}
import scala.reflect.ClassTag

trait MetadataContainer extends MetadataContainerTrait {

  def getMetadataOption(name: String) = this.metadatasAsScala.find(_.id == name)

  def getOrAddMetadata(name: String) = {
    getMetadataOption(name) match {
      case Some(m) =>
        m
      case None =>
        val m = this.addMetadata
        m.id = name
        m
    }

  }

  def deleteMetadata(name: String) = {
    this.getMetadataOption(name) match {
      case Some(m) =>
        this.metadatas.remove(m)
      case None =>
        false
    }
  }

  def addMetadataFromValue(name: String, value: String): MetadataContainerTraitmetadata = {

    val m = this.getOrAddMetadata(name)
    m.value = Json.createValue(value)
    m.`type` = "std:string"

    m

  }

  def addMetadataFromValue(name: String, value: Int): MetadataContainerTraitmetadata = {

    val m = this.getOrAddMetadata(name)
    m.value = Json.createValue(value)
    m.`type` = "std:int"

    m
  }

  def addMetadataFromValue(name: String, value: Double): MetadataContainerTraitmetadata = {
    val m = this.getOrAddMetadata(name)
    m.value = Json.createValue(value)
    m.`type` = "std:double"

    m
  }

  def addMetadataFromValue(name: String, value: Long): MetadataContainerTraitmetadata = {
    val m = this.getOrAddMetadata(name)
    m.value = Json.createValue(value)
    m.`type` = "std:long"

    m
  }

  def addMetadataFromValue(name: String, value: Boolean): MetadataContainerTraitmetadata = {

    val m = this.getOrAddMetadata(name)
    m.value = if (value) JsonValue.TRUE else JsonValue.FALSE
    m.`type` = "std:boolean"

    m
  }

  def addMetadataFromValue(name: String, value: JsonValue): MetadataContainerTraitmetadata = {

    val m = this.getOrAddMetadata(name)
    m.value = value
    m.`type` = value.toStdTypeDefinition

    m
  }


  def geMetadataJsonValue(id: String) = {
    this.metadatasAsScala.find(_.id == id) match {
      case Some(m) =>
        Some(m.value)
      case None => None
    }
  }

  def getMetadataAsPrimitiveOrJsonValue(id: String) = {
    getMetadataOption(id) match {
      case Some(m)  =>
        Some(m.asPrimitiveOrJsonValue)
      case other =>
        None
    }
  }

  def getMetadataBoolean(name: String): Option[Boolean] = {
    geMetadataJsonValue(name) match {
      case Some(m) if (m.isBoolean) =>
        Some(m.asBoolean)
      case other =>
        None
    }
  }

  def getMetadataString(name: String): Option[String] = {
    geMetadataJsonValue(name) match {
      case Some(m) =>
        Some(m.asString)
      case other =>
        None
    }
  }

  def getMetadataDouble(name: String): Option[Double] = {
    geMetadataJsonValue(name) match {
      case Some(m) if (m.isNumber) =>
        Some(m.asDouble)
      case other =>
        None
    }
  }

  def getMetadataLong(name: String): Option[Long] = {
    geMetadataJsonValue(name) match {
      case Some(m) if (m.isNumber) =>
        Some(m.asLong)

      case other =>
        None
    }
  }

  def getMetadataInteger(name: String): Option[Integer] = {
    geMetadataJsonValue(name) match {
      case Some(m) if (m.isNumber) =>
        Some(m.asInt)
      case other =>
        None
    }
  }


}
