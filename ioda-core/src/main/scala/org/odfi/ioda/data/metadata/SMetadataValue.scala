package org.odfi.ioda.data.metadata

import com.google.gson.annotations.{Expose, SerializedName}
import jakarta.json.bind.annotation.JsonbProperty

import scala.beans.BeanProperty
import scala.reflect.ClassTag

class SMetadataValue  {

  @Expose
  @BeanProperty
  @JsonbProperty("value")
  @SerializedName("value")
  var value: Any = _

  @Expose
  @BeanProperty
  @JsonbProperty("name")
  @SerializedName("name")
  var name : String = _

  // Changed state
  //-----------------
  var changed = false

  def updateChangedState(compareValue: Any) = {

    changed = !value.equals(compareValue)
  }

  // Conversions and types
  //----------------------

  def toInt = {
    this.value = this.value.toString.toInt
    this.asInt
  }

  def isInt = this.value.isInstanceOf[Int]
  def asInt = this.value.asInstanceOf[Int]

  def toDouble = {
    this.value = this.value.toString.toDouble
    this.asDouble
  }

  def isDouble = this.value.isInstanceOf[Double]
  def asDouble = this.value.asInstanceOf[Double]

  def toBoolean = {

    val booleanValue = this.value.toString() match {
      case "1" => true
      case "0" => false
      case "true" => true
      case "false" => false
      case other => false
    }

    this.value = booleanValue
    this.asBoolean
  }
  def isBoolean = this.value.isInstanceOf[Boolean]
  def asBoolean = this.value.asInstanceOf[Boolean]

  def isIntList = this.value.isInstanceOf[List[_]] && this.value.asInstanceOf[List[_]].size > 0 && this.value.asInstanceOf[List[_]](0).isInstanceOf[Int]
  def asIntList = this.value.asInstanceOf[List[Int]]

  def isString = this.value.isInstanceOf[String]
  def asString = this.value.asInstanceOf[String]

  def isOfType[T](implicit tag: ClassTag[T]) = {
    tag.runtimeClass.isAssignableFrom(value.getClass())

  }

  def asType[T](implicit tag: ClassTag[T]) = value.asInstanceOf[T]
}
object SMetadataValue {
  def apply(name:String, v: Any) = {
    var pv = new SMetadataValue
    pv.value = v
    pv.name = name
    pv
  }

  def apply(name:String,pv:SMetadataValue) = {
    val v = new SMetadataValue
    v.value = pv.value
    pv.name = name
    v
  }

}
