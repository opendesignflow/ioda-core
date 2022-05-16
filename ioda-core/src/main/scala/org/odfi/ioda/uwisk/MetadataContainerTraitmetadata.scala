package org.odfi.ioda.uwisk

import javax.json.bind.annotation.JsonbTransient
import org.odfi.ioda.json.JsonExtensions.JsonValueHelperMethods
import javax.json.{JsonString, JsonValue}
import javax.persistence.Transient

import java.time.Instant

class MetadataContainerTraitmetadata extends MetadataContainerTraitmetadataTrait {

  // Runtime helpers

  /**
   * Changed can be used during runtime to identify modifications in settings for a given pipeline
   */
  @JsonbTransient
  @Transient
  var changed: Boolean = false

  @JsonbTransient
  @Transient
  var objectValue : Any = null


  def withType(str: String) = {
    this.`type` = str
    this
  }

  def withDisplayName(name: String) = {
    this.displayName = name
    this
  }

  def withUnit(u: String) = {
    this.unit = u
    this
  }

  def withMessageType = this.withType("std:message")

  def withUnitSeconds = this.withUnit("s")

  def withUnitMilliseconds = this.withUnit("ms")

  def withUnitMinutes = this.withUnit("m")

  def withUnitHours = this.withUnit("H")

  def tagWarning = {
    if (!tags.contains("std:warning")) {
      this.tags.add("std:warning")
    }
    this
  }

  def tagVisible = {
    if (!tags.contains("std:visible")) {
      this.tags.add("std:visible")
    }
    tags.remove("std:hidden")
    this
  }
  def tagHidden = {
    if (!tags.contains("std:hidden")) {
      this.tags.add("std:hidden")
    }
    tags.remove("std:visible")
    this
  }

  def isOfOneType(types:Iterable[String]) = {
    types.find( t => t == this.`type`).isDefined
  }


  def asString: Option[String] = {
    this.value match {
      case v if (v != null && v.getValueType == JsonValue.ValueType.STRING) =>
        Some(v.asInstanceOf[JsonString].getString)
      case other =>
        None
    }
  }

  def isTypeStdLong = `type` != null && `type` == "std:long"

  def isTypeStdInt = `type` != null && `type` == "std:int"

  def isTypeStdDouble = `type` != null && `type` == "std:double"

  def isTypeStdBoolean = `type` != null && `type` == "std:boolean"

  def isTypeEpoch = isOfOneType(List("std:epoch:unix","std:epoch:milli"))

  /**
   * This method doesn't check type!
   */
  def asInstant = `type` match {
    case "std:epoch:unix" => Instant.ofEpochSecond(value.asLong)
    case "std:epoch:milli" => Instant.ofEpochMilli(value.asLong)
    case other => throw new IllegalArgumentException("Metadata is not an Epoch type or value is null")
  }

  def asPrimitiveOrJsonValue = {
    value match {
      case v if (v.isBoolean) =>
        v.asBoolean
      case v if (v.isNumber && isTypeStdLong) =>
        v.asLong
      case v if (v.isNumber && isTypeStdInt) =>
        v.asInt
      case v if (v.isNumber) =>
        v.asDouble
      case v if (v.isPrimitive) =>
        v.asString
      case v =>
        v

    }
  }

  override def toString = asString match {
    case None => value.toString
    case Some(str) => str
  }

}
