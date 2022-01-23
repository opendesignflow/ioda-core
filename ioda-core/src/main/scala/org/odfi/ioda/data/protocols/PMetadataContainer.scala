package org.odfi.ioda.data.protocols

import org.odfi.ioda.data.protocols.params.ParamValue

import jakarta.json.{JsonNumber, JsonObject, JsonString, JsonValue}
import scala.reflect.ClassTag

trait PMetadataContainer {
  // Metadata
  //--------------
  var metadata = Map[String, ParamValue]()

  def metadataAsScala = metadata.toList

  def importJsonValue(name: String, value: JsonValue): Unit = {

    value.getValueType match {
      case JsonValue.ValueType.NULL =>
      case JsonValue.ValueType.TRUE => addMetadata(name, true)
      case JsonValue.ValueType.FALSE => addMetadata(name, false)
      case JsonValue.ValueType.ARRAY =>

        val arr = value.asJsonArray()
        (0 until arr.size()).foreach {
          i =>
            val obj = arr.get(i)
            importJsonValue(name + "_" + i, obj)
        }

      case JsonValue.ValueType.NUMBER =>
        val number = value.asInstanceOf[JsonNumber]
        if (number.isIntegral) {
          addMetadata(name, number.longValue())
        } else {
          addMetadata(name, number.intValue())
        }

      case JsonValue.ValueType.STRING =>
        addMetadata(name, value.asInstanceOf[JsonString].getString)
      case JsonValue.ValueType.OBJECT =>
        sys.error("Object not supported as metadata")
    }
  }

  def addMetadata(name: String, value: ParamValue): ParamValue = {

    val v = ParamValue(value.value)
    metadata = metadata + (name -> v)

    v

  }

  def addMetadata(name: String, value: Any): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: String): ParamValue = {

    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: Int): ParamValue = {

    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: Double): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv
  }

  def addMetadata(name: String, value: Long): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv
  }

  def addMetadata(name: String, value: Boolean): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv
  }

  def addMetadataObject(name: String, value: JsonObject): ParamValue = {
    val pv = ParamValue(value)
    metadata = metadata + (name -> pv)

    pv
  }

  def getMetadata(name: String): Option[ParamValue] = {
    this.metadata.get(name)
  }

  def getMetadataOfType[T](name: String)(implicit tag: ClassTag[T]): Option[T] = {
    getMetadata(name) match {
      case Some(pv) if (pv.isOfType[T]) => Some(pv.asType[T])
      case other => None
    }
  }

  def getMetadataBoolean(name: String): Option[Boolean] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isBoolean) =>
        Some(m.asBoolean)
      case other =>
        None
    }
  }

  def getMetadataString(name: String): Option[String] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isString) =>
        Some(m.asString)
      case Some(m) =>
        Some(m.value.toString)
      case other =>
        None
    }
  }

  def getMetadataDouble(name: String): Option[Double] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isDouble) =>
        Some(m.asDouble)
      case Some(m) if (m.isInt) =>
        Some(m.asInt.toDouble)
      case Some(m) if (m.isLong) =>
        Some(m.asLong.toDouble)
      case Some(m) if (m.isString) =>
        Some(m.toDouble)
      case other =>
        None
    }
  }

  def getMetadataLong(name: String): Option[Long] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isLong) =>
        Some(m.asLong)
      case Some(m) if (m.isInt) =>
        Some(m.asInt.toLong)
      case Some(m) if (m.isString) =>
        Some(m.toLong)
      case other =>
        None
    }
  }

  def getMetadataInteger(name: String): Option[Integer] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isLong) =>
        Some(m.asLong.toInt)
      case Some(m) if (m.isInt) =>
        Some(m.asInt)
      case Some(m) if (m.isString) =>
        Some(m.toInt)
      case other =>
        None
    }
  }

  def getMetadataJsonObject(name: String): Option[JsonObject] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isJsonObject) =>
        Some(m.asJsonObject)
      case other =>
        None
    }
  }
}
