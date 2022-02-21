package org.odfi.ioda.uwisk

import org.odfi.ioda.json.JsonExtensions.JsonValueHelperMethods
import jakarta.json.{Json, JsonObject, JsonValue}

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

  /**
   * Generic add which tries to determine type from input value
   *
   * @param name
   * @param value
   * @return
   */
  def addMetadataFromValue(id: String, value: Any): MetadataContainerTraitmetadata = {

    value match {
      case b: Boolean =>
        addMetadataFromValue(id, b)
      case b: String =>
        addMetadataFromValue(id, b)
      case b: Long =>
        addMetadataFromValue(id, b)
      case b: Double =>
        addMetadataFromValue(id, b)
      case b: Int =>
        addMetadataFromValue(id, b)
      case b: Float =>
        addMetadataFromValue(id, b)
      case b: JsonValue =>
        addMetadataFromValue(id, b)
      case other =>
        val m = getOrAddMetadata(id)
        m.objectValue = other
        m
    }

  }

  def importMetadata(m: MetadataContainerTraitmetadata) = {
    val newMetadata = this.getOrAddMetadata(m.id)
    newMetadata.id = m.id
    newMetadata.value = m.value
    newMetadata.`type` = m.`type`
    newMetadata.displayName = m.displayName
    newMetadata.tags = m.tags
    newMetadata.unit = m.unit
    newMetadata
  }

  def importMetadataWithNewID(id : String , m: MetadataContainerTraitmetadata) = {
    val newMetadata = this.getOrAddMetadata(m.id)
    newMetadata.id = id
    newMetadata.value = m.value
    newMetadata.`type` = m.`type`
    newMetadata.displayName = m.displayName
    newMetadata.tags = m.tags
    newMetadata.unit = m.unit
    newMetadata
  }


  def geMetadataJsonValue(id: String) = {
    this.metadatasAsScala.find(_.id == id) match {
      case Some(m) =>
        Some(m.value)
      case None => None
    }
  }

  def getMetadataJsonObject(id: String): Option[JsonObject] = {
    geMetadataJsonValue(id) match {
      case Some(obj: JsonObject) => Some(obj)
      case None => None
    }
  }

  def getMetadataAsPrimitiveOrJsonValue(id: String) = {
    getMetadataOption(id) match {
      case Some(m) =>
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

  def getMetadataEpochInstant(id: String) = {
    getMetadataOption(id) match {
      case Some(m) if (m.isTypeEpoch) => Some(m.asInstant)
      case other => None
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

  def getMetadataObject[T](id: String)(implicit tag: ClassTag[T]): Option[T] = {
    this.getMetadataOption(id) match {
      case Some(obj) if (obj.objectValue != null && obj.objectValue.isInstanceOf[T]) => Some(obj.objectValue.asInstanceOf[T])
      case other => None
    }
  }


}
