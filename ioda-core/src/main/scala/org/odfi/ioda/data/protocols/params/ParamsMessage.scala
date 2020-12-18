package org.odfi.ioda.data.protocols.params

import org.odfi.ioda.data.types.DataMessage
import org.odfi.ubroker.core.broker.tree.single.SingleMessage
import scala.collection.immutable.TreeMap
import scala.reflect.ClassTag

class ParamValue(var value: Any) {

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
  }

  def isInt = this.value.isInstanceOf[Int]
  def asInt = this.value.asInstanceOf[Int]

  def toDouble = {
    this.value = this.value.toString.toDouble
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

object ParamValue {
  def apply(v: Any) = {
    var pv = new ParamValue(v)
    pv
  }

  def apply(pv:ParamValue) = {
    val v = new ParamValue()
    v.value = pv.value
    v
  }

}

/**
 * Message for parameters
 */
class ParamsMessage extends DataMessage with SingleMessage {

  var parameters = TreeMap[String, ParamValue]()

  // Utils
  //----------------
  //def setParameters(m:
  /*d0ef sortParametersByName = {
    this.parameters = this.parameters.
  }*/

  // Accessors
  //----------------

  def apply(str: String) = this.parameters.get(str)

  /**
   * Run closure on parameter value if parameter exists
   */
  def onParameter(p: String)(cl: ParamValue => Any) = {
    this(p) match {
      case Some(v) =>
        cl(v)
      case None =>
    }
  }

  def addParameter(name: String, v: Any) = {
    parameters = parameters + (name -> ParamValue(v))
  }

  def addPlainParameter(pair: (String, ParamValue)) = {
    this.parameters = this.parameters + pair
  }

  /**
   * Return value
   */
  def updateParameter[T](pair: (String, Any)) = {
    this.onParameter(pair._1) {
      v =>
        v.value = pair._2
    }
  }

  def renameParameter(pname: String, newName: String) = {

    this.parameters.find {
      case (name, v) if (name == pname) => true
      case other => false
    } match {
      case Some(foundParameterToRename) =>
        this.parameters = this.parameters - foundParameterToRename._1
        this.parameters = this.parameters + (newName -> foundParameterToRename._2)
      case None =>
    }
  }

  // Testing
  //-----------------

  def parameterIsAndChanged(name: String, testValue: Any) = {
    this.parameters.get(name) match {
      case Some(value) =>
        value.changed == true && value.value.equals(testValue)
      case None =>
        false
    }
  }

  // Parameters Type
  //---------------------

  def toInt(param: String) = {
    onParameter(param) {
      v =>
        v.toInt
    }
  }
  def toDouble(param: String) = {
    onParameter(param) {
      v =>
        v.toDouble
    }
  }
  def toBoolean(param: String) = {
    onParameter(param) {
      v =>
        v.toBoolean
    }
  }

}