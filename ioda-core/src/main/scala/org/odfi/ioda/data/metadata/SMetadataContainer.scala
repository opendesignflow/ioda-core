package org.odfi.ioda.data.metadata


import com.google.gson.annotations.{Expose, SerializedName}
import javax.json.bind.annotation.JsonbProperty

import java.util
import scala.beans.BeanProperty
import scala.reflect.ClassTag

trait SMetadataContainer {
  // Metadata
  //--------------
  @Expose
  @BeanProperty
  @JsonbProperty("metadatas")
  @SerializedName("metadatas")
  var metadata = new util.ArrayList[SMetadataValue]()



  /*
  def addMetadata(name: String, value: SMetadataValue): SMetadataValue = {

    val v = SMetadataValue(value)
    metadata = metadata + (name -> v)

    v

  }

  def addMetadata(name: String, value: Any): SMetadataValue = {
    val pv = SMetadataValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: String): SMetadataValue = {

    val pv = SMetadataValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: Int): SMetadataValue = {

    val pv = SMetadataValue(value)
    metadata = metadata + (name -> pv)

    pv

  }

  def addMetadata(name: String, value: Double): SMetadataValue = {
    val pv = SMetadataValue(value)
    metadata = metadata + (name -> pv)

    pv
  }

  def addMetadata(name: String, value: Long): SMetadataValue = {
    val pv = SMetadataValue(value)
    metadata = metadata + (name -> pv)

    pv
  }
  def addMetadata(name: String, value: Boolean): SMetadataValue = {
    val pv = SMetadataValue(value)
    metadata = metadata + (name -> pv)

    pv
  }

  def getMetadata(name: String): Option[SMetadataValue] = {
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
  def getMetadataDouble(name: String): Option[Double] = {
    this.metadata.get(name) match {
      case Some(m) if (m.isDouble) =>
        Some(m.asDouble)
      case Some(m) if (m.isString) =>
        Some(m.toDouble)
      case other =>
        None
    }
  }*/
}
