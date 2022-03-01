package org.odfi.ioda.uwisk

import scala.reflect.ClassTag

trait WithRuntimeMetadata extends MetadataContainer {

  var runtimeMetadata = Map[String, Any]()

  def addRuntimeMetadata[T](name: String, obj: T) = {

    runtimeMetadata = runtimeMetadata + (name -> obj)
  }

  def getRuntimeMetadataOfType[T](name: String)(implicit tag: ClassTag[T]) = {
    println(s"Searching for M $name (size=${runtimeMetadata.size})")
    this.runtimeMetadata.get(name) match {
      case Some(v) if (tag.runtimeClass.isAssignableFrom(v.getClass)) => Some(v.asInstanceOf[T])
      case other => None
    }
  }

}
