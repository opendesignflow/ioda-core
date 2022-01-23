package org.odfi.ioda.uwisk

import org.odfi.ioda.json.JsonExtensions.JsonValueHelperMethods

import jakarta.json.{JsonString, JsonValue}

class MetadataContainerTraitmetadata extends MetadataContainerTraitmetadataTrait {


  def asString : Option[String] = {
    this.value match {
      case v if (v!=null && v.getValueType==JsonValue.ValueType.STRING) =>
        Some(v.asInstanceOf[JsonString].getString)
      case other =>
        None
    }
  }

  def isTypeStdLong = `type`!=null && `type`=="std:long"
  def isTypeStdInt = `type`!=null && `type`=="std:int"
  def isTypeStdDouble = `type`!=null && `type`=="std:double"
  def isTypeStdBoolean = `type`!=null && `type`=="std:boolean"


  def asPrimitiveOrJsonValue = {
    value match {
      case v if (v.isBoolean) =>
        v.asBoolean
      case v if (v.isNumber && isTypeStdLong) =>
        v.asLong
      case v if (v.isNumber  && isTypeStdInt) =>
        v.asInt
      case v if (v.isNumber) =>
        v.asDouble
      case v if (v.isPrimitive) =>
        v.asString
      case v  =>
       v

    }
  }

  override def toString = asString match {
    case None => value.toString
    case Some(str) => str
  }

}
