package org.odfi.ioda.data.protocols

import org.odfi.ioda.data.protocols.params.ParamValue

import scala.reflect.ClassTag

trait MetadataContainer {
  // Metadata
  //--------------
  var metadata = Map[String, ParamValue]()

  def addMetadata(name: String, value: ParamValue): ParamValue = {

    val v = ParamValue(value)
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
      case other =>
        None
    }
  }
}
