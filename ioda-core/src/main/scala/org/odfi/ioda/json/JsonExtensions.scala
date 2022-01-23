package org.odfi.ioda.json


import jakarta.json.JsonValue.ValueType
import jakarta.json.{JsonNumber, JsonString, JsonValue}

object JsonExtensions {

  val jsonTypesToStdTypes = Map(
    JsonValue.ValueType.TRUE -> "std:boolean",
    JsonValue.ValueType.FALSE -> "std:boolean",
    JsonValue.ValueType.NULL -> "std:null",
    JsonValue.ValueType.ARRAY -> "std:array",
    JsonValue.ValueType.OBJECT -> "std:json",
    JsonValue.ValueType.NUMBER -> "std:number",
    JsonValue.ValueType.STRING -> "std:string",
  )

  implicit class JsonValueHelperMethods( value:JsonValue) {

    def toStdTypeDefinition = {

      value match {
        case null => jsonTypesToStdTypes(JsonValue.ValueType.NULL)
        case JsonValue.NULL => jsonTypesToStdTypes(JsonValue.ValueType.NULL)
        case JsonValue.TRUE => jsonTypesToStdTypes(JsonValue.ValueType.TRUE)
        case JsonValue.FALSE => jsonTypesToStdTypes(JsonValue.ValueType.FALSE)
        case typed => jsonTypesToStdTypes(typed.getValueType)
      }
    }

    def isPrimitive = {
     // println("Jsvalue type: "+value.getValueType)
      !isNotPrimitive
    }

    def isNotPrimitive = value.getValueType == JsonValue.ValueType.ARRAY || value.getValueType == JsonValue.ValueType.OBJECT

    def isBoolean = {
      isPrimitive && (value.getValueType == ValueType.TRUE || value.getValueType == ValueType.FALSE)
    }

    def isNumber = {
      isPrimitive && value.getValueType== ValueType.NUMBER
    }

    def asString = {
      assert(isPrimitive)
      value.getValueType match {
        case JsonValue.ValueType.STRING => value.asInstanceOf[JsonString].getString
        case JsonValue.ValueType.NUMBER => value.asInstanceOf[JsonNumber].toString
        case JsonValue.ValueType.TRUE => "true"
        case JsonValue.ValueType.FALSE => "false"
        case JsonValue.ValueType.NULL => "null"
      }
    }

    def asBoolean = {
      assert(isBoolean)
      value.getValueType match {
        case JsonValue.ValueType.TRUE => true
        case JsonValue.ValueType.FALSE => false
        case other => throw new IllegalStateException()

      }
    }

    def asDouble = {
      assert(isNumber)
      value.asInstanceOf[JsonNumber].doubleValue()
    }

    def asInt = {
      assert(isNumber)
      value.asInstanceOf[JsonNumber].intValue()
    }

    def asLong = {
      assert(isNumber)
      value.asInstanceOf[JsonNumber].longValue()
    }




  }

}
